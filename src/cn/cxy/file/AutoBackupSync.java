package cn.cxy.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class AutoBackupSync {

	private static final String localRoot = "e:\\work";
	private static final String remoteRoot = "\\\\chenweiye\\chexingyou\\work";
	private static final Logger logger = Logger.getLogger(AutoBackupSync.class);

	private static Map<String, File> local = new HashMap<>();
	private static Map<String, File> remote = new HashMap<>();

	static {
		try {
			logger.debug("aaa");
			scanRemote(new File(remoteRoot));
			
			logger.debug("bbb");
		} catch (IOException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void scanLocal(File file) throws IOException {

		local.put(file.getPath().substring(localRoot.length()), file);

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null)
				for (File f : children) {
					scanLocal(f);
				}
		}

	}

	public static void scanRemote(File file) throws IOException {

		remote.put(file.getPath().substring(remoteRoot.length()), file);
		logger.debug(file.getPath());
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null)
				for (File f : children) {
					scanRemote(f);
				}
		}

	}

	public static void copyFile(File file, File dest) throws IOException {

		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}

		String cmd = "cmd.exe /c copy " + file + " " + dest.getParent() + "\\";
		Runtime.getRuntime().exec(cmd);
		logger.debug("copy file:" + file.getPath());
	}

	public static void main(String[] args) {

		while (true) {
			try {
				Thread.sleep(1000 * 2);
				logger.debug("ccc");
				scanLocal(new File(localRoot));
				logger.debug("ddd");
				for (Entry<String, File> entry : local.entrySet()) {
					String key = entry.getKey();
					File file = entry.getValue();
					File remoteFile = remote.get(key);
					if ((remoteFile != null && file.lastModified() >= remoteFile
							.lastModified()) || remoteFile == null) {
						File dest = new File(remoteRoot
								+ file.getPath().substring(localRoot.length()));

						copyFile(entry.getValue(), dest);

						if (remoteFile == null) {
							remote.put(key, dest);
						}
					}
				}

				Iterator<Entry<String, File>> it = remote.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, File> entry = (Entry<String, File>) it.next();
					if (local.get(entry.getKey()) == null) {
						it.remove();
						entry.getValue().delete();
					}
				}

			} catch (InterruptedException | IOException e) {
				logger.debug(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}

package cn.cxy.file;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class AutoBackup {

	private static final String root = "e:\\work\\";
	private static final String destRoot = "\\\\chenweiye\\chexingyou\\work\\";
	private static final Logger logger = Logger.getLogger(AutoBackup.class);

	private static long last = System.currentTimeMillis();


	public static void  scan(File file) throws IOException {

		if (file.isFile()) {
			copyFile(file);
		} else {
			File[] children = file.listFiles();
			if (children != null)
				for (File f : children) {
					scan(f);
				}
		}

	}

	public  static void copyFile(File file) throws IOException {

		if (file.lastModified() < last) {
			return;
		}

		File dest = new File(destRoot + file.getPath().substring(root.length()));
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
				Thread.sleep(1000*60*5);
				scan(new File(root));
				last = System.currentTimeMillis();
			} catch (InterruptedException | IOException e) {
				logger.debug(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}

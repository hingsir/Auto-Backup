package cn.cxy.file;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

public class AutoBackupThread {

	private static final String root = "e:\\work\\";
	private static final String destRoot = "\\\\chenweiye\\chexingyou\\work\\";
	private static final Logger logger = Logger
			.getLogger(AutoBackupThread.class);

	private static final BlockingQueue<File> queue = new ArrayBlockingQueue<File>(
			100);

	private static long last = addDays(new Date(System.currentTimeMillis()), -1)
			.getTime();

	private static java.util.Date addDays(java.util.Date date, int days) {

		if (date == null) {
			return date;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	public static void scan(File file) throws IOException, InterruptedException {

		if (file.isFile()) {
			if (file.lastModified() >= last) {
				queue.put(file);
			}
		} else {
			File[] children = file.listFiles();
			if (children != null)
				for (File f : children) {
					scan(f);
				}
		}

	}

	public static void copy(File file) throws IOException {

		File dest = new File(destRoot + file.getPath().substring(root.length()));
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		String cmd = "cmd.exe /c copy " + file + " " + dest.getParent() + "\\";
		Runtime.getRuntime().exec(cmd);
		logger.debug("copy file:" + file.getPath());
	}

	public static void main(String[] args) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					File file = null;
					try {
						file = queue.take();
						copy(file);
					} catch (InterruptedException | IOException e) {
						logger.debug(e.getMessage());
						e.printStackTrace();
					}

				}

			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000 * 60 * 5);
						scan(new File(root));
						last = System.currentTimeMillis();
					} catch (InterruptedException | IOException e) {
						logger.debug(e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}).start();

	}
}

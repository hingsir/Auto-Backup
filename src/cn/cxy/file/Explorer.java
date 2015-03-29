package cn.cxy.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeoutException;

public class Explorer {
	private final static ForkJoinPool forkJoinPool = new ForkJoinPool();

	private static class FileSizeFinder extends RecursiveTask<Long> {
		File file;

		public FileSizeFinder(File file) {
			this.file = file;
		}

		@Override
		protected Long compute() {
			// TODO Auto-generated method stub
			System.out.println(file.getName());
			long size = 0;
			if (file.isFile()) {
				return file.length();
			} else {
				File[] children = file.listFiles();
				if (children != null) {
					List<ForkJoinTask<Long>> tasks = new ArrayList<ForkJoinTask<Long>>();
					for (File child : children) {
						if (child.isFile()) {
							size += child.length();
						} else {
							tasks.add(new FileSizeFinder(child));
						}
					}

					for (ForkJoinTask<Long> task : invokeAll(tasks)) { // 等待所有的子任务完成之后才会执行下一步循环操作。在任务被阻塞时，
																		// 其他程序也可以去帮忙完成其他任务
						size += task.join();
					}
				}
			}
			return size;
		}
	}

	public static void main(final String[] args) throws InterruptedException,
			ExecutionException, TimeoutException {
		Scanner scanner = new Scanner(System.in);
		String str = scanner.next();
		System.out.println("get the output--->" + str);
		final long start = System.nanoTime();
		final long total = forkJoinPool
				.invoke(new FileSizeFinder(new File(str)));
		final long end = System.nanoTime();
		System.out.println("Total Size: " + total);
		System.out.println("Time taken: " + (end - start) / 1.0e9);
	}
}
/**
 * 
 */
package com.agoda.download.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.agoda.download.manager.model.ByteArrayContainer;
import com.agoda.download.task.DownloadTask;
import com.agoda.download.task.ITaskFactory;
import com.agoda.download.task.Task;
import com.agoda.download.task.TaskFactory;
import com.agoda.download.task.WriteTask;

/**
 * @author s0n00a7
 *
 */
public class DownloadManager {
	
	public static final String DOWNLOAD_LOCATION = "/Users/s0n00a7/sumit/";
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	private Map<String, List<Task>> tracker = new HashMap<>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DownloadManager manager = new DownloadManager();
		manager.testRunDownload(args);
	}
	
	/**
	 * @param strArgs
	 */
	public void testRunDownload(String[] strArgs) {
		ITaskFactory taskFactory = TaskFactory.getInstance();
		List<Task> taskList = new ArrayList<>();
		if (strArgs.length < 2) {
			return;
		}
		for (int i = 1; i < strArgs.length; i++) {
			AtomicBoolean isComplete = new AtomicBoolean();
			Exchanger<ByteArrayContainer> downloadBufferExchanger = new Exchanger<ByteArrayContainer>();
			DownloadTask task1 = taskFactory.createDownloadTask(strArgs[i], downloadBufferExchanger, isComplete);
			WriteTask task2 = taskFactory.createWriteTask(strArgs[i], downloadBufferExchanger, isComplete);

			taskList.add(task1);
			taskList.add(task2);
			tracker.put(strArgs[i], taskList);

			executor.submit(task1);
			executor.submit(task2);
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		}
	}
}
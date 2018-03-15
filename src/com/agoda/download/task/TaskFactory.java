/**
 * 
 */
package com.agoda.download.task;

import java.io.FileNotFoundException;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;

import com.agoda.download.manager.model.ByteArrayContainer;

/**
 * A factory for creating Task objects.
 *
 * @author s0n00a7
 */
public class TaskFactory implements ITaskFactory {
	
	/** The task factory. */
	private static ITaskFactory taskFactory = new TaskFactory();
	
	/**
	 * Instantiates a new task factory.
	 */
	private TaskFactory() {
		
	}
	
	/**
	 * Gets the single instance of TaskFactory.
	 *
	 * @return single instance of TaskFactory
	 */
	public static ITaskFactory getInstance() {
		return taskFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.agoda.download.task.ITaskFactory#createDownloadTask(java.lang.String)
	 */
	@Override
	public DownloadTask createDownloadTask(String downloadURL, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete) {
		DownloadTask task = null;
		String protocol = downloadURL.split(":")[0];
		if(protocol.equalsIgnoreCase("http")) {
			return new HttpDownloadTask(downloadURL, downloadBufferExchanger, isComplete);
		}
		else if(protocol.equalsIgnoreCase("ftp")) {
			return new FtpDownloadTask(downloadURL, downloadBufferExchanger, isComplete);
		}
		else if(protocol.equalsIgnoreCase("sftp")) {
			return new SftpDownloadTask(downloadURL, downloadBufferExchanger, isComplete);
		}
		return task;
	}
	
	/* (non-Javadoc)
	 * @see com.agoda.download.task.ITaskFactory#createWriteTask(java.lang.String, java.util.concurrent.Exchanger)
	 */
	@Override
	public WriteTask createWriteTask(String downloadURL, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete) {
		try {
			return new WriteTask(downloadURL, downloadBufferExchanger, isComplete);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}

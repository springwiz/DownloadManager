/**
 * 
 */
package com.agoda.download.task;

import static com.agoda.download.manager.DownloadManager.DOWNLOAD_LOCATION;
import static com.agoda.download.task.Task.TaskStatus.ERROR;
import static com.agoda.download.task.Task.TaskStatus.IN_PROGRESS;
import static com.agoda.download.task.Task.TaskStatus.STARTED;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agoda.download.manager.model.ByteArrayContainer;
import com.agoda.download.task.Task.TaskStatus;

/**
 * The Class WriteTask.
 *
 * @author s0n00a7
 */
public class WriteTask implements Task,Callable<TaskStatus> {
	/** Logger Instance. */
	private static final Logger logger = Logger.getLogger("WriteTask.class");
	
	/** The status. */
	protected TaskStatus status = STARTED;
	
	/** The download buffer exchanger. */
	private Exchanger<ByteArrayContainer> downloadBufferExchanger;
	
	/** The write buffer. */
	private ByteArrayContainer writeBuffer;
	
	/** The write to file OS. */
	private FileOutputStream writeToFileOS = null;
	
	/** The current path. */
	private File currentPath = null;
	
	/** The is complete. */
	private AtomicBoolean isComplete;
	
	/** The data written bytes. */
	protected long dataWrittenBytes;
	
	/**
	 * Instantiates a new write task.
	 *
	 * @param dataSource the data source
	 * @param isComplete the is complete
	 * @throws FileNotFoundException the file not found exception
	 */
	public WriteTask(String dataSource, AtomicBoolean isComplete) throws FileNotFoundException {
		writeBuffer = new ByteArrayContainer();
		writeBuffer.setByteBuffer(new byte[16384]);
		writeBuffer.setLength(16384);
		this.setIsComplete(isComplete);
		openFile(dataSource);
	}

	/**
	 * Instantiates a new write task.
	 *
	 * @param dataSource the data source
	 * @param downloadBufferExchanger the download buffer exchanger
	 * @param isComplete the is complete
	 * @throws FileNotFoundException the file not found exception
	 */
	public WriteTask(String dataSource, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete) throws FileNotFoundException {
		this.downloadBufferExchanger = downloadBufferExchanger;
		writeBuffer = new ByteArrayContainer();
		writeBuffer.setByteBuffer(new byte[16384]);
		writeBuffer.setLength(16384);
		this.setIsComplete(isComplete);
		openFile(dataSource);
	}
	
	/**
	 * Open file.
	 *
	 * @param dataSource the data source
	 * @throws FileNotFoundException the file not found exception
	 */
	public void openFile(String dataSource) throws FileNotFoundException {
		currentPath = new File(dataSource.replaceAll("[^\\p{L}\\p{Z}]", ""));
		writeToFileOS = new FileOutputStream(currentPath, false);
		dataWrittenBytes = 0;
	}

	/* (non-Javadoc)
	 * @see com.agoda.download.task.Runner#executeTask()
	 */
	@Override
	public TaskStatus executeTask() {
		// exchange the buffer with the downloader thread
		// start writing from the buffer into the file
		try {
			writeBuffer = downloadBufferExchanger.exchange(writeBuffer);
			dataWrittenBytes += writeBuffer.getLength();
			writeToFileOS.write(writeBuffer.getByteBuffer(), 0, writeBuffer.getLength());
		} catch (Exception e2) {
			logger.log(Level.SEVERE, e2.getMessage());
			try {
				writeToFileOS.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			return ERROR;
		}
		return IN_PROGRESS;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public TaskStatus call() throws Exception {
		while ((status.equals(IN_PROGRESS) && !isComplete.get()) 
				|| status.equals(STARTED)) {
			logger.log(Level.FINE, "Bytes written:: " + dataWrittenBytes);
			status = executeTask();
		}
		// move the file from temp location to the final location
		writeToFileOS.flush();
		writeToFileOS.close();
		if (isComplete.get() && !status.equals(ERROR)) {
			Path fileToMovePath = Paths.get(currentPath.getPath());
			Path targetPath = Paths.get(DOWNLOAD_LOCATION);
			Files.move(fileToMovePath, targetPath.resolve(fileToMovePath.getFileName()), REPLACE_EXISTING);
		}
		return status;
	}

	/**
	 * Gets the checks if is complete.
	 *
	 * @return the checks if is complete
	 */
	public AtomicBoolean getIsComplete() {
		return isComplete;
	}

	/**
	 * Sets the checks if is complete.
	 *
	 * @param isComplete the new checks if is complete
	 */
	public void setIsComplete(AtomicBoolean isComplete) {
		this.isComplete = isComplete;
	}
}
/**
 * 
 */
package com.agoda.download.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agoda.download.manager.model.ByteArrayContainer;
import com.agoda.download.task.Task.TaskStatus;

import static com.agoda.download.task.Task.TaskStatus.*;

/**
 * The Class DownloadTask.
 *
 * @author s0n00a7
 */
public abstract class DownloadTask implements Task, Callable<TaskStatus> {
	/** Logger Instance. */
	private static final Logger logger = Logger.getLogger("DownloadTask.class");

	/** The status. */
	protected TaskStatus status = STARTED;

	/** The download size bytes. */
	protected long downloadSizeBytes;

	/** The data downloaded bytes. */
	protected long dataDownloadedBytes;

	/** The url. */
	protected String url;

	/** The download buffer exchanger. */
	private Exchanger<ByteArrayContainer> downloadBufferExchanger;

	/** The download buffer. */
	private ByteArrayContainer downloadBuffer;

	/** The is complete. */
	private AtomicBoolean isComplete;

	/**
	 * Instantiates a new download task.
	 */
	public DownloadTask() {

	}

	/**
	 * Instantiates a new download task.
	 *
	 * @param url the url
	 * @param isComplete the is complete
	 */
	public DownloadTask(String url, AtomicBoolean isComplete) {
		this.url = url;
		downloadBuffer = new ByteArrayContainer();
		downloadBuffer.setByteBuffer(new byte[16384]);
		downloadBuffer.setLength(16384);
		this.setIsComplete(isComplete);
	}

	/**
	 * Instantiates a new download task.
	 *
	 * @param url the url
	 * @param downloadBufferExchanger the download buffer exchanger
	 * @param isComplete the is complete
	 */
	public DownloadTask(String url, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete) {
		this.url = url;
		this.downloadBufferExchanger = downloadBufferExchanger;
		downloadBuffer = new ByteArrayContainer();
		downloadBuffer.setByteBuffer(new byte[16384]);
		downloadBuffer.setLength(16384);
		this.setIsComplete(isComplete);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.agoda.download.task.Runner#executeTask()
	 */
	@Override
	public TaskStatus executeTask() {
		// get the specialized input stream depending on source
		// start reading from the stream into the buffer
		// exchange the buffer with the writer thread once full
		InputStream downloadStream = null;
		int i = 0;
		try {
			downloadStream = retrieveInputStream();
			i = downloadStream.read(downloadBuffer.getByteBuffer(), 0, downloadBuffer.getLength());
			downloadBuffer.setLength(i);
			dataDownloadedBytes += i;
		} catch (IOException e1) {
			logger.log(Level.SEVERE, e1.getMessage());
			try {
				closeInputStream();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			return INTERRUPTED;
		} catch (Exception e2) {
			logger.log(Level.SEVERE, e2.getMessage());
			try {
				closeInputStream();
			} catch (Exception e) {
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
		while ((status.equals(IN_PROGRESS) && dataDownloadedBytes < downloadSizeBytes)
				|| status.equals(STARTED)) {
			status = executeTask();
			logger.log(Level.FINE, "Bytes downloaded:: " + dataDownloadedBytes);
			logger.log(Level.FINE, "Download size:: " + downloadSizeBytes);
			downloadBuffer = downloadBufferExchanger.exchange(downloadBuffer);
		}
		if(!status.equals(ERROR) && !status.equals(INTERRUPTED)) {
			isComplete.set(true);
		}
		else {
			isComplete.set(false);
		}
		return status;
	}

	/**
	 * Gets the download buffer exchanger.
	 *
	 * @return the download buffer exchanger
	 */
	public Exchanger<ByteArrayContainer> getDownloadBufferExchanger() {
		return downloadBufferExchanger;
	}

	/**
	 * Sets the download buffer exchanger.
	 *
	 * @param downloadBufferExchanger the new download buffer exchanger
	 */
	public void setDownloadBufferExchanger(Exchanger<ByteArrayContainer> downloadBufferExchanger) {
		this.downloadBufferExchanger = downloadBufferExchanger;
	}

	/**
	 * Gets the download buffer.
	 *
	 * @return the download buffer
	 */
	public ByteArrayContainer getDownloadBuffer() {
		return downloadBuffer;
	}

	/**
	 * Sets the download buffer.
	 *
	 * @param downloadBuffer the new download buffer
	 */
	public void setDownloadBuffer(ByteArrayContainer downloadBuffer) {
		this.downloadBuffer = downloadBuffer;
	}

	/**
	 * Retrieve input stream.
	 *
	 * @return the input stream
	 * @throws Exception the exception
	 */
	protected abstract InputStream retrieveInputStream() throws Exception;

	/**
	 * Close input stream.
	 *
	 * @throws Exception the exception
	 */
	protected abstract void closeInputStream() throws Exception;

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
package com.agoda.download.task;

import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;

import com.agoda.download.manager.model.ByteArrayContainer;

/**
 * A factory for creating ITask objects.
 */
public interface ITaskFactory {

	/**
	 * Creates a new ITask object.
	 *
	 * @param downloadURL the download URL
	 * @param downloadBufferExchanger the download buffer exchanger
	 * @param isComplete the is complete
	 * @return the download task
	 */
	DownloadTask createDownloadTask(String downloadURL, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete);

	/**
	 * Creates a new ITask object.
	 *
	 * @param downloadURL the download URL
	 * @param downloadBufferExchanger the download buffer exchanger
	 * @param isComplete the is complete
	 * @return the write task
	 */
	WriteTask createWriteTask(String downloadURL, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete);

}
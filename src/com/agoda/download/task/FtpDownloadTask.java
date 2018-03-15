/**
 * 
 */
package com.agoda.download.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;

import com.agoda.download.manager.model.ByteArrayContainer;

/**
 * The Class FtpDownloadTask.
 *
 * @author s0n00a7
 * ftp://<username>:<password>@<hostname>:<port>/<path> i.e. 
 * ftp://tom:secret@www.myserver.com/project/2012/Project.zip
 */
public class FtpDownloadTask extends DownloadTask {
	
	/** The input stream. */
	private InputStream inputStream = null;
	
	/** The connection. */
	private URLConnection connection = null;

	/**
	 * Instantiates a new ftp download task.
	 */
	public FtpDownloadTask() {

	}

	/**
	 * Instantiates a new ftp download task.
	 *
	 * @param url the url
	 * @param downloadBufferExchanger the download buffer exchanger
	 * @param isComplete the is complete
	 */
	public FtpDownloadTask(String url, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete) {
		super(url, downloadBufferExchanger, isComplete);

	}

	/* (non-Javadoc)
	 * @see com.agoda.download.task.DownloadTask#retrieveInputStream()
	 */
	@Override
	protected InputStream retrieveInputStream() throws IOException {
		if(null == inputStream) {
			URL objUrl = new URL(url);
			connection = objUrl.openConnection();
	
			//add request properties
			connection.setReadTimeout(15*1000);
			connection.setConnectTimeout(15*1000);
			connection.setDoOutput(false);
			connection.connect();
			inputStream = connection.getInputStream();
	
			downloadSizeBytes = connection.getContentLength();
			if(downloadSizeBytes == -1) {
				downloadSizeBytes = inputStream.available();
			}
			dataDownloadedBytes = 0;
		}		
		return inputStream;
	}
	
	/* (non-Javadoc)
	 * @see com.agoda.download.task.DownloadTask#closeInputStream()
	 */
	@Override
	protected void closeInputStream() throws IOException {
		inputStream.close();
		inputStream = null;
	}
}

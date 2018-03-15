/**
 * 
 */
package com.agoda.download.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HttpsURLConnection;

import com.agoda.download.manager.model.ByteArrayContainer;

/**
 * The Class HttpDownloadTask.
 *
 * @author s0n00a7
 */
public class HttpDownloadTask extends DownloadTask {
	
	/** The input stream. */
	private InputStream inputStream = null;
	
	/** The connection. */
	private HttpURLConnection connection = null;
	
	/**
	 * Instantiates a new http download task.
	 */
	public HttpDownloadTask() {

	}

	/**
	 * Instantiates a new http download task.
	 *
	 * @param url the url
	 * @param downloadBufferExchanger the download buffer exchanger
	 * @param isComplete the is complete
	 */
	public HttpDownloadTask(String url, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete) {
		super(url, downloadBufferExchanger, isComplete);
	}

	/* (non-Javadoc)
	 * @see com.agoda.download.task.DownloadTask#retrieveInputStream()
	 */
	@Override
	protected InputStream retrieveInputStream() throws IOException {
		if(null == inputStream) {
			URL objUrl = new URL(url);
			connection = (HttpURLConnection) objUrl.openConnection();
	
			//add request properties
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/octet-stream;");
			connection.setUseCaches(false);
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
		connection.disconnect();
		inputStream = null;
	}
}

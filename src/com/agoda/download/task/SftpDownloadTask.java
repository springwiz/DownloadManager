/**
 * 
 */
package com.agoda.download.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;

import com.agoda.download.manager.model.ByteArrayContainer;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * The Class SftpDownloadTask.
 *
 * @author s0n00a7 sftp://address:port/path
 *         sftp://<username>@<hostname>:<port>/<path>
 */
public class SftpDownloadTask extends DownloadTask {

	/** The input stream. */
	private InputStream inputStream;
	
	/** The connection. */
	private Object connection;

	/**
	 * Instantiates a new sftp download task.
	 */
	public SftpDownloadTask() {

	}

	/**
	 * Instantiates a new sftp download task.
	 *
	 * @param url the url
	 * @param downloadBufferExchanger the download buffer exchanger
	 * @param isComplete the is complete
	 */
	public SftpDownloadTask(String url, Exchanger<ByteArrayContainer> downloadBufferExchanger, AtomicBoolean isComplete) {
		super(url, downloadBufferExchanger, isComplete);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.agoda.download.task.DownloadTask#retrieveInputStream()
	 */
	@Override
	protected InputStream retrieveInputStream() throws Exception {
		if (null == inputStream) {
			URI uri = new URI(url);

			String sftpHost = uri.getHost();
			String sftpUser = uri.getUserInfo().split(":")[0];
			String sftpPassword = uri.getUserInfo().split(":")[1];
			int sftpPort = uri.getPort();
			String sftpFilePath = uri.getPath();

			Session session = null;
			Channel channel = null;
			ChannelSftp channelSftp = null;

			JSch jsch = new JSch();
			session = jsch.getSession(sftpUser, sftpHost, sftpPort);
			session.setPassword(sftpPassword);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;

			inputStream = channelSftp.get(sftpFilePath);
			downloadSizeBytes = inputStream.available();
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
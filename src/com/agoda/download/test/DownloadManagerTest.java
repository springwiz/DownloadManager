/**
 * 
 */
package com.agoda.download.test;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.agoda.download.manager.DownloadManager;

/**
 * @author snarayan
 *
 */
public class DownloadManagerTest {

	private DownloadManager manager1 = null;
	
	private DownloadManager manager2 = null;
	
	private DownloadManager manager3 = null;
	
	private String[] paramArray1 = null;
	
	private String[] paramArray2 = null;
	
	private String[] paramArray3 = null;
		
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger("DownloadManagerTest.class");
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		manager1 = new DownloadManager();
		manager2 = new DownloadManager();
		manager3 = new DownloadManager();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.agoda.download.manager#runHttpDownload()}.
	 */
	@Test
	public void testHttpRunDownload() {
		paramArray1 = new String[2];
		paramArray1[0] = "DownloadManager";
		paramArray1[1] = "http://www.sparxsystems.com/bin/easetup.msi";
		manager1.testRunDownload(paramArray1);
	}
	
	/**
	 * Test method for {@link com.agoda.download.manager#runFtpDownload()}.
	 */
	@Test
	public void testFtpRunDownload() {
		paramArray2 = new String[2];
		paramArray2[0] = "DownloadManager";
		paramArray2[1] = "ftp://speedtest.tele2.net/512KB.zip";
		manager2.testRunDownload(paramArray2);
	}
	
	/**
	 * Test method for {@link com.agoda.download.manager#runSftpDownload()}.
	 */
	@Test
	public void runSftpDownload() {
		paramArray3 = new String[2];
		paramArray3[0] = "DownloadManager";
		paramArray3[1] = "sftp://demo:password@test.rebex.net:22/readme.txt";
		manager3.testRunDownload(paramArray3);
	}
}

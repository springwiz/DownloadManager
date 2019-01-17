# DownloadManager
Download manager built in Java, supports FTP,SFTP,HTTP

The download manager is a java based application which allows the user to down the content from the data sources over the network. The application as of now supports HTTP, FTP and SFTP protocol. But can be easily extended to support new protocols/data formats.
The destination folder on the running system is hardcoded as of now in one of the java files but can be moved to XML/JSON in the future.  The features covered in the current version of the application are:
1.	Data is only copied to the target folder if the file download is complete. Incomplete downloads are not copied to the target folder.
2.	Any incomplete downloads are restarted from scratch. Application can support resuming the download in the future.
3.	Fast/slow data sources over network are handled via multithreading.
4.	Data is read from network sources as in Byte Containers and the full download/file is not loaded in memory in 1 shot.
5.	Parallelism is introduced in the application via multithreading and thread synchronization.
6.	File name is uniquely determined from the download URL.

# Design



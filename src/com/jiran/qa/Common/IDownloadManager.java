package com.jiran.qa.Common;

public interface IDownloadManager {
    void startDownload(String fileName, long fileSize);
    void finishDownload(String fileName);
    void updateProgressBar(int percent);
}

package com.jiran.qa.Common;

public interface IDownloadManager {
    void startDownload(String fileName, long fileSize);
    void finishDownload(String fileName);
    void resetProgressBar();
    void updateProgressBar(int percent);
}

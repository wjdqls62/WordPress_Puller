package com.jiran.qa.Common;

import com.jiran.qa.View.MainView;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;

public class DownloadManager extends Thread {
    private ILogCallback logger = MainView.getLogger();
    private IDownloadManager downloadManagerCallback = MainView.getDownloadManagerCallback();
    private ArrayList<PostVO> postList;
    private HashSet<String> includeCategories;
    private long totalSize = 0;
    private String path;

    public DownloadManager(ArrayList<PostVO> postLost, String path, HashSet<String> includeCategories){
        if(Config.isDebug){
            logger.log("DownloadManager init.");
        }
        this.postList = postLost;
        this.path = path;
        this.includeCategories = includeCategories;
    }

    public void setIncludeCategories(HashSet<String> includeCategories){
        this.includeCategories = includeCategories;
    }

    /**
     * 다운로드 전 include에 포함된 카테고리인지 확인 후 FLAG변수를 정의한다.
     */
    public void before(){
        double totalFileSize = 0;
        // rest progressbar
        downloadManagerCallback.resetProgressBar();
        downloadManagerCallback.setButton(false);

        for(int i=0; i < postList.size(); i++){
            if(includeCategories.contains(postList.get(i).getCATEGORIES_NAME())){
                try {
                    postList.get(i).setInclude(true);
                    URL url = new URL(postList.get(i).getAttachment_Source_URL());
                    URLConnection urlConnection = url.openConnection();
                    totalFileSize += urlConnection.getContentLength();
                } catch (MalformedURLException e) {
                    logger.log(e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.log(e.toString());
                    e.printStackTrace();
                }
            }else{
                postList.get(i).setInclude(false);
            }
        }
        logger.log("Total Size : " + totalFileSize / 1024 + " KBytes");
    }

    /**
     * Local 경로에 다운로드 한다.
     */
    @Override
    public void run() {
        logger.log("DownloadManager thread is start.");

        before();

        for(int i = 0; i < postList.size(); i++){
            if(postList.get(i).isInclude()){
                String dirName = postList.get(i).getCATEGORIES_NAME();
                File file = new File(path + "\\" + dirName);
                if(!file.exists()){
                    file.mkdir();
                }

                String targetFilename = getFileName(postList.get(i).getAttachment_Source_URL());

                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    fos = new FileOutputStream(path+"/" + dirName + "/" + targetFilename);

                    URL url = new URL(postList.get(i).getAttachment_Source_URL());
                    URLConnection urlConnection = url.openConnection();
                    downloadManagerCallback.startDownload(targetFilename, urlConnection.getContentLengthLong());

                    is = urlConnection.getInputStream();
                    byte[] buffer = new byte[1024];
                    double fileSize = urlConnection.getContentLength();
                    int readBytes = 0;
                    while ((readBytes = is.read(buffer)) != -1) {
                        //System.out.format("Progress : %.1f%%%n", size / fileSize * 100.0);
                        fos.write(buffer, 0, readBytes);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    logger.log(e.toString());
                } catch (MalformedURLException e) {
                    logger.log(e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.log(e.toString());
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.log(e.toString());
                    }
                }
                downloadManagerCallback.finishDownload(targetFilename);
            }
        }
        after();
    }

    public void after(){
        downloadManagerCallback.setButton(true);
    }

    private String getFileName(String httpURL) throws NullPointerException{
        String result = httpURL.substring(httpURL.lastIndexOf('/')+1);
        if(result.contains(".")){
            return result;
        }else{
            return null;
        }
    }
}
package com.jiran.qa.Common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager implements Runnable{
    String httpUrl = null;

    public DownloadManager(String httpUrl){
        this.httpUrl = httpUrl;
    }

    @Override
    public void run() {
        String path = System.getProperty("user.dir");
        System.out.println("path : " + path);

        String targetFilename = getFileName(httpUrl);
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            fos = new FileOutputStream(path+"/" + targetFilename);

            URL url = new URL(httpUrl);
            URLConnection urlConnection = url.openConnection();
            is = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            double fileSize = urlConnection.getContentLength();
            double size = 0;
            int readBytes = 0;
            while ((readBytes = is.read(buffer)) != -1) {
                size += readBytes;
                System.out.format("Progress : %.1f%%%n", size / fileSize * 100.0);
                fos.write(buffer, 0, readBytes);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
            }
        }
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
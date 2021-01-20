package com.jiran.qa.Common;

public class PostVO {
    private String MEDIA_ID;
    private String POST_ID;
    private String CATEGORIES_ID;
    private String CATEGORIES_NAME;
    private String POST_URL;
    private String ATTACHMENT_SOURCE_URL;

    public void setMEDIA_ID(String id){
        this.MEDIA_ID = id;
    }

    public void setPOST_ID(String id){
        this.POST_ID = id;
    }

    public void setCATEGORIES_ID(String id){
        this.CATEGORIES_ID = id;
    }

    public void setCATEGORIES_NAME(String name){
        this.CATEGORIES_NAME = name;
    }

    public void setPOST_URL(String url){
        this.POST_URL = url;
    }

    public void setATTACHMENT_SOURCE_URL(String url){
        this.ATTACHMENT_SOURCE_URL = url;
    }

    public void log(){
        System.out.println("MEDIA_ID : " + MEDIA_ID);
        System.out.println("POST_ID : " + POST_ID);
        System.out.println("CATEGORIES_ID : " + CATEGORIES_ID);
        System.out.println("CATEGORIES_NAME : " + CATEGORIES_NAME);
        System.out.println("POST_URL : " + POST_URL);
        System.out.println("ATTACHMENT_SOURCE_URL : " + ATTACHMENT_SOURCE_URL);
        System.out.println("========================================");

    }
}

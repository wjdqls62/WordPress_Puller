package com.jiran.qa.Common;

public class Config {
    // Request 관련 변수
    public static Boolean isDebug = true;
    public static final String BASE_PROTOCOL = "http://";
    public static final String BASE_URL = "10.53.168.125:7070";
    public static final String REQUEST_PARAMETER = "?per_page=100&page=";
    public static final String REQUEST_MEDIA_URL = BASE_PROTOCOL + BASE_URL + "/wp-json/wp/v2/media" + REQUEST_PARAMETER;
    public static final String REQUEST_POST_URL = BASE_PROTOCOL + BASE_URL + "/wp-json/wp/v2/posts"+ REQUEST_PARAMETER;
    public static final String REQUEST_CATEGORIES_URL = BASE_PROTOCOL + BASE_URL + "/wp-json/wp/v2/categories"+ REQUEST_PARAMETER;

    // View 관련 변수
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 800;
}

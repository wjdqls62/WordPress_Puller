package com.jiran.qa.Common;

import com.jiran.qa.Main;
import com.jiran.qa.View.MainView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PostManager extends Thread {
    private JSONArray mediaJsonArray;
    private JSONArray postJsonArray;
    private JSONArray categoriesArray;
    private ArrayList<PostVO> postList;
    private HashMap<String, CategoriesVO> categoriesVOHashMap;
    private boolean isEmpty = false;
    private ILogCallback logger = MainView.getLogger();
    private IPostManagerCallback postManagerCallback = MainView.getPostManagerCallback();

    public PostManager(){
        if(Config.isDebug)  logger.log("PostManager init");
    }

    @Override
    public void run() {
        postManagerCallback.startParse();
        if(Config.isDebug)  logger.log("PostManager Thread is start..");

        mediaJsonArray =  get(Config.REQUEST_MEDIA_URL);
        postJsonArray = get(Config.REQUEST_POST_URL);
        categoriesArray = get(Config.REQUEST_CATEGORIES_URL);

        categoriesVOHashMap = new HashMap<>();
        setCategoriesArray();
        postList = parseJsonArray();

        if(postList.isEmpty()){
            isEmpty = true;
            if(Config.isDebug)  System.out.println("PostList is Empty.");
        }

        if(Config.isDebug){
            logger.log("Finish. Received " + postList.size() + " posts.");
        }

        postManagerCallback.finishParse();
    }

    public boolean isEmpty(){
        return isEmpty;
    }

    public JSONArray getCategoriesArray(){
        return categoriesArray;
    }

    public ArrayList<PostVO> getPosts(){
        return postList;
    }

    public JSONArray get(String requestURL){
        HttpURLConnection conn = null;
        JSONArray respJsonArray = null;
        int responseCode = 0;

        try{
            URL url = new URL(requestURL);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");

            JSONObject commands = new JSONObject();

            responseCode = conn.getResponseCode();
            if(responseCode == 400 || responseCode == 401 || responseCode == 500){
                logger.log("Error Code : " + responseCode);
            }else{
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";

                while ((line = br.readLine()) != null){
                    stringBuilder.append(line);
                }
                respJsonArray = new JSONArray(stringBuilder.toString());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            postManagerCallback.finishParse();
        } catch (SocketTimeoutException e){
            e.printStackTrace();
            postManagerCallback.finishParse();
            logger.log("Fail. Connection Timeout.");
        } catch (IOException e) {
            postManagerCallback.finishParse();
            e.printStackTrace();
        }
        return respJsonArray;
    }

    private void setCategoriesArray(){

        for(int i = 0; i < categoriesArray.length(); i++){
            JSONObject temp = categoriesArray.getJSONObject(i);
            CategoriesVO categoriesVO = new CategoriesVO();

            categoriesVO.setCategories(temp);
            categoriesVOHashMap.put(temp.get("id").toString(), categoriesVO);
        }
    }

    private ArrayList<PostVO> parseJsonArray(){

        ArrayList<PostVO> tempPost = new ArrayList<>();

        for(int i = 0; i < mediaJsonArray.length(); i++){
            PostVO postVO = new PostVO();
            String media_id, post_id, str_temp;

            // set attachment url
            str_temp = mediaJsonArray.getJSONObject(i).get("source_url").toString();
            postVO.setATTACHMENT_SOURCE_URL(str_temp);

            // set media id
            media_id = mediaJsonArray.getJSONObject(i).get("id").toString();
            postVO.setMEDIA_ID(media_id);

            // set post id
            post_id = mediaJsonArray.getJSONObject(i).get("post").toString();
            postVO.setPOST_ID(post_id);
            for(int j=0; j<postJsonArray.length(); j++){
                if(postJsonArray.getJSONObject(j).get("id").toString().equals(post_id)){
                    String temp = postJsonArray.getJSONObject(j).get("categories").toString().replaceAll("[^0-9]", "");
                    postVO.setCATEGORIES_ID(temp);
                    postVO.setPOST_URL(postJsonArray.getJSONObject(j).get("link").toString());
                    postVO.setCATEGORIES_NAME(categoriesVOHashMap.get(temp).getCategories_name());
                }
            }
            postVO.log();
            tempPost.add(postVO);
        }
        return tempPost;
    }
}
package com.jiran.qa.Common;

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
    private ArrayList<JSONArray> mediaJsonArray;
    private ArrayList<JSONArray> postJsonArray;
    private ArrayList<JSONArray> categoriesArray;
    private ArrayList<JSONArray> tempList;

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

    public ArrayList<JSONArray> getCategoriesArray(){
        return categoriesArray;
    }

    public ArrayList<PostVO> getPosts(){
        return postList;
    }

    public ArrayList<JSONArray> get(String requestURL){
        boolean is_400_Error = false;
        tempList = new ArrayList<>();
        int pageCnt = 1;
        HttpURLConnection conn = null;
        JSONArray respJsonArray = null;
        int responseCode = 0;

        while(!is_400_Error){
            try{
                URL url = new URL(requestURL + pageCnt);
                logger.log("RequestURL : " + requestURL + pageCnt);

                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");


                responseCode = conn.getResponseCode();
                if(responseCode == 400 || responseCode == 401 || responseCode == 500){
                    logger.log("Error Code : " + responseCode);
                    is_400_Error = true;
                }else{
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";

                    while ((line = br.readLine()) != null){
                        stringBuilder.append(line);
                    }
                    respJsonArray = new JSONArray(stringBuilder.toString());

                    if(respJsonArray.isEmpty()){
                        if(Config.isDebug){
                            logger.log("respJsonArray is null...");
                        }
                        break;
                    }
                    tempList.add(respJsonArray);
                    pageCnt += 1;
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
        }
        return tempList;
    }

    private void setCategoriesArray(){

        for(int i = 0; i < categoriesArray.size(); i++){
            for(int j=0; j< categoriesArray.get(i).length(); j++){
                JSONObject temp = categoriesArray.get(i).getJSONObject(j);
                CategoriesVO categoriesVO = new CategoriesVO();

                categoriesVO.setCategories(temp);
                categoriesVOHashMap.put(temp.get("id").toString(), categoriesVO);
            }
        }
    }

    private ArrayList<PostVO> parseJsonArray(){

        ArrayList<PostVO> tempPost = new ArrayList<>();

        for(int i=0; i< mediaJsonArray.size(); i++){
            for(int j = 0; j < mediaJsonArray.get(i).length(); j++){
                PostVO postVO = new PostVO();
                String media_id, post_id, str_temp;

                // set attachment url
                str_temp = mediaJsonArray.get(i).getJSONObject(j).get("source_url").toString();
                postVO.setATTACHMENT_SOURCE_URL(str_temp);

                // set media id
                media_id = mediaJsonArray.get(i).getJSONObject(j).get("id").toString();
                postVO.setMEDIA_ID(media_id);

                // set post id
                post_id = mediaJsonArray.get(i).getJSONObject(j).get("post").toString();
                postVO.setPOST_ID(post_id);
                for(int k=0; k<postJsonArray.size(); k++){
                    for(int l=0; l<postJsonArray.get(k).length(); l++){
                        if(postJsonArray.get(k).getJSONObject(l).get("id").toString().equals(post_id)){
                            String temp = postJsonArray.get(k).getJSONObject(l).get("categories").toString().replaceAll("[^0-9]", "");
                            postVO.setCATEGORIES_ID(temp);
                            postVO.setPOST_URL(postJsonArray.get(k).getJSONObject(l).get("link").toString());
                            postVO.setCATEGORIES_NAME(categoriesVOHashMap.get(temp).getCategories_name());
                        }
                    }
                }
                postVO.log();
                tempPost.add(postVO);
        }
        }
        return tempPost;
    }
}
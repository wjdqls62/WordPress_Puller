package com.jiran.qa.Common;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PostManager {
    private JSONArray mediaJsonArray;
    private JSONArray postJsonArray;
    private JSONArray categoriesArray;
    private ArrayList<PostVO> postList;

    public PostManager(){
        mediaJsonArray =  get(Config.REQUEST_MEDIA_URL);
        postJsonArray = get(Config.REQUEST_POST_URL);
        categoriesArray = get(Config.REQUEST_CATEGORIES_URL);

        postList = parseJsonArray();
    }

    public JSONArray getCategories(){
        return categoriesArray;
    }

    public JSONArray get(String requestURL){
        HttpURLConnection conn = null;
        JSONArray respJsonArray = null;

        try{
            URL url = new URL(requestURL);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");

            JSONObject commands = new JSONObject();

            int responseCode = conn.getResponseCode();
            if(responseCode == 400 || responseCode == 401 || responseCode == 500){
                System.out.println(responseCode + "Error");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respJsonArray;
    }

    public ArrayList<PostVO> parseJsonArray(){

        ArrayList<PostVO> tempPost = new ArrayList<PostVO>();

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
                    postVO.setPOST_URL(postJsonArray.getJSONObject(j).get("link").toString());
                    postVO.setCATEGORIES_ID(postJsonArray.getJSONObject(j).get("categories").toString());
                }
                break;
            }
            postVO.log();
            tempPost.add(postVO);
        }
        return tempPost;
    }
}

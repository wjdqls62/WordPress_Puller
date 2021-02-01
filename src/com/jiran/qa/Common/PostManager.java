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
        if(Config.isDebug)  logger.log("PostManager init.");
    }

    @Override
    public void run() {
        postManagerCallback.startParse();
        logger.log("PostManager started searching the attachments.");

        mediaJsonArray =  get(Config.REQUEST_MEDIA_URL);

        /**
         * MediaJson을 받아오지 못했을경우 post 및 categories에 대한 request를 하지 않도록 조건문으로 분기
         */
        if(!mediaJsonArray.isEmpty()){
            postJsonArray = get(Config.REQUEST_POST_URL);
            categoriesArray = get(Config.REQUEST_CATEGORIES_URL);
        }

        categoriesVOHashMap = new HashMap<>();
        setCategoriesArray();
        postList = parseJsonArray();

        if(postList.isEmpty()){
            isEmpty = true;
            if(Config.isDebug)  System.out.println("PostList is Empty.");
        }

        logger.log("Finish. Found " + postList.size() + " attachments.");


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
                //logger.log("RequestURL : " + requestURL + pageCnt);

                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");


                responseCode = conn.getResponseCode();
                if(responseCode == 400 || responseCode == 401 || responseCode == 500){
                    if (Config.isDebug) {
                        /**
                         * pageCnt 가 400에러가 return될때까지 반복문으로 동작하나 로그상 400에러로그가 표시됨으로
                         * 프로그램의 오류로 오해 할 요지가 있으므로 pageCnt == 1 에서 400에러 return시에만 에러로그 표시
                         */
                        if(pageCnt == 1){
                            logger.log("RequestURL : " + requestURL + pageCnt);
                            logger.log("Error Code : " + responseCode);
                        }
                    }
                    is_400_Error = true;
                }else{
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";

                    while ((line = br.readLine()) != null){
                        stringBuilder.append(line);
                    }
                    respJsonArray = new JSONArray(stringBuilder.toString());

                    /**
                     * Categories Request시 다른 API와 다르게 "[]" 만 리턴되어 Error 코드로 분류되지 않음.
                     * isEmpty()로 구분 가능함을 확인하여 break 해준다.
                     */
                    if(respJsonArray.isEmpty()){
                        break;
                    }
                    logger.log("RequestURL : " + requestURL + pageCnt);
                    tempList.add(respJsonArray);
                    pageCnt += 1;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                logger.log(e.toString());
                postManagerCallback.finishParse();
                break;
            } catch (SocketTimeoutException e){
                e.printStackTrace();
                logger.log(e.toString());
                postManagerCallback.finishParse();
                logger.log(e.toString());
                logger.log("Fail. Connection Timeout.");
                break;
            } catch (IOException e) {
                postManagerCallback.finishParse();
                logger.log(e.toString());
                e.printStackTrace();
                break;
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
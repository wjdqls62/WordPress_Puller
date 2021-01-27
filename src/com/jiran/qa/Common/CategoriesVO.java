package com.jiran.qa.Common;

import org.json.JSONObject;

public class CategoriesVO {
    private String Categories_id;
    private String Categories_name;
    private String Categories_slug;
    private String Categories_link;
    private String Categories_desc;

    public void setCategories(JSONObject object){
        Categories_id = object.get("id").toString();
        Categories_name = object.get("name").toString();
        Categories_slug = object.get("slug").toString();
        Categories_link = object.get("link").toString();
        Categories_desc = object.get("description").toString();
    }

    public String getCategories_id(){
        return Categories_id;
    }

    public String getCategories_name(){
        return Categories_name;
    }

    public String getCategories_slug(){
        return Categories_slug;
    }

    public String getCategories_link(){
        return Categories_link;
    }

    public String getCategories_desc(){
        return Categories_desc;
    }
}

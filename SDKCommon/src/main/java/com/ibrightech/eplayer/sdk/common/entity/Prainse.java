package com.ibrightech.eplayer.sdk.common.entity;

import org.json.JSONObject;

/**
 * Created by zhaoxu2014 on 15-1-8.
 */
public class Prainse {
    int count=0;
    int code=0;
    String teacherUUID="";
    public static Prainse fromJson(JSONObject jsonObject) {
        Prainse bean=new Prainse();
        bean.code=jsonObject.optInt("code");
        bean.count=jsonObject.optInt("count");
        bean.teacherUUID=jsonObject.optString("teacherUUID");
        return bean;
    }

    public boolean isSuccee(){
        return code==0;
    }
    public int getCount() {
        return count;
    }
}

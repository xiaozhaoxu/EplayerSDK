package com.ibrightech.eplayer.sdk.common.entity;

import org.json.JSONObject;

/**
 * Created by zhaoxu2014 on 14-11-19.
 */
public class LiveInfoChar {
    public String _id;
    public String nickname;
    public String content;
    public String createTime;

    public static LiveInfoChar fromJson(JSONObject jsonObject) {
        LiveInfoChar bean=new LiveInfoChar();
        bean._id=jsonObject.optString("_id");
        bean.nickname=jsonObject.optString("nickname");
        bean.content=jsonObject.optString("content");

        bean.createTime=jsonObject.optString("createTime");


        return bean;
    }



}

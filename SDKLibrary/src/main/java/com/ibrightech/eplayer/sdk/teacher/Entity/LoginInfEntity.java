package com.ibrightech.eplayer.sdk.teacher.Entity;

import com.ibrightech.eplayer.sdk.common.util.JsonUtil;

import org.json.JSONObject;

/**
 * Created by zhaoxu2014 on 15/7/29.
 */

public class LoginInfEntity extends SDKBaseEntity {
    String token;
    String invalidTime;
    long user_id;
    UserEntity user;

    public static LoginInfEntity fromJSON(JSONObject js) {
        if(null==js){
            return null;
        }
        LoginInfEntity bean= JsonUtil.json2Bean(js.toString(),LoginInfEntity.class);



        return bean;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(String invalidTime) {
        this.invalidTime = invalidTime;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String toString() {
        return getInvalidTime()+";"+getToken()+";"+ getUser();
    }
}

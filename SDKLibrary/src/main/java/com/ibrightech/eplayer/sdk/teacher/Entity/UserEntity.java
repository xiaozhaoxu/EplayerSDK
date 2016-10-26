package com.ibrightech.eplayer.sdk.teacher.Entity;


import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.JsonUtil;

import org.json.JSONObject;

/**
 *
 */
public class UserEntity extends SDKBaseEntity {
    public long id;
    public String name;
    public String nick;
    public String icon;
    public int gender;// 性别： 1男， 2⼥， 0未知
    public String mobile;
    public String address;
    public String idCard;
    public long ct;//创建时间

    public UserEntity() {
    }

    public String getUserShowName(){
        if(!CheckUtil.isEmpty(name)){
            return name;
        }
        return "老师";
    }

    public static UserEntity getEntityFromJson(JSONObject js) {
        if(null==js){
            return null;
        }
        return JsonUtil.json2Bean(js.toString(), UserEntity.class);
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}

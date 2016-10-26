package com.ibrightech.eplayer.sdk.common.entity.SessionData;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerUserInfoUserType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerUserInfoValidateType;

import org.json.JSONObject;

/**
 * Created by junhai on 14-8-13.
 */
public class UserSessionInfo {

   // public LoginType type;

    public String customer;
    public String exStr;
    public String liveClassroomId;

    public String pid;

    public String user;
    public String pwd;


    public EplayerUserInfoValidateType validateType;
    public int userId;

    public EplayerUserInfoUserType userType;

    public String nickname;
    public Object privilege;
    public String userRole;
    public String userIp;
    public String name;
    public String icon;

    public JSONObject writeToRedisData;


    public void fromJson(JSONObject jsonObject) {
        this.userId = jsonObject.optInt("userId");

        this.nickname = jsonObject.optString("nickname");
        this.userRole = jsonObject.optString("userRole");
        this.userIp = jsonObject.optString("userIp");

        this.name = jsonObject.optString("name");
        this.icon = jsonObject.optString("icon");

        int userType = jsonObject.optInt("userType");

        this.userType= EplayerUserInfoUserType.getEnumByKey(userType);


        int validateType = jsonObject.optInt("validateType");
        this.validateType=EplayerUserInfoValidateType.getEnumByKey(validateType);

    }

    public void clearLoginInfo() {

    }
}

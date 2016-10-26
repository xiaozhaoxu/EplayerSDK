package com.ibrightech.eplayer.sdk.common.entity.SessionData;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerMessageChatType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerUserInfoUserType;

import org.json.JSONObject;

/**
 * Created by junhai on 14-8-13.
 */
public class SocketMessage {

    public String userKey;
    public String chatInfoKey;
    public String liveClassroomId;

    public String nickname;

    public EplayerUserInfoUserType userType;
    public EplayerMessageChatType chatType;

    public String content;
    public String createTime;

    public static SocketMessage fromJson(JSONObject jsonObject) {
        SocketMessage socketMessage = new SocketMessage();
        socketMessage.chatInfoKey = jsonObject.optString("chatInfoKey");
        socketMessage.userKey = jsonObject.optString("userKey");
        socketMessage.liveClassroomId = jsonObject.optString("liveClassroomId");
        socketMessage.nickname = jsonObject.optString("nickname");
        socketMessage.content = jsonObject.optString("content");
        socketMessage.createTime=jsonObject.optString("createTime");


        int userType = jsonObject.optInt("userType");
        socketMessage.userType = EplayerUserInfoUserType.getEnumByKey(userType);



        int chatType = jsonObject.optInt("chatType");
        socketMessage.chatType=EplayerMessageChatType.getEnumByKey(chatType);


        return socketMessage;
    }

}

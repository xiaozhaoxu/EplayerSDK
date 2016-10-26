package com.ibrightech.eplayer.sdk.common.entity.SessionData;

import org.json.JSONObject;

/**
 * Created by junhai on 14-8-13.
 */
public class ForbidMessage {
    public String roomId;

    public boolean chatForbid;

    public String userKey;
    public String userUUID;

    public static ForbidMessage fromJson(JSONObject jsonObject) {
        ForbidMessage msg = new ForbidMessage();
        msg.chatForbid = (jsonObject.optInt("action")==1);
        msg.userKey = jsonObject.optString("userKey");
        msg.roomId = jsonObject.optString("liveRoomId");
        msg.userUUID = jsonObject.optString("userUUID");

        return msg;
    }
}

package com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo;

import org.json.JSONObject;

/**
 * Created by zhaoxu2014 on 16/6/2.
 */
public class LiveRoomStreamConfig {
    public String name;
    public String url;
    public static LiveRoomStreamConfig fromJson(JSONObject jsonObject) {
        LiveRoomStreamConfig msg = new LiveRoomStreamConfig();
        msg.name=jsonObject.optString("name");
        msg.url=jsonObject.optString("url");
        return msg;
    }
}

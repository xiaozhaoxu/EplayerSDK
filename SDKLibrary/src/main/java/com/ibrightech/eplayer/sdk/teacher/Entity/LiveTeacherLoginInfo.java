package com.ibrightech.eplayer.sdk.teacher.Entity;


import com.ibrightech.eplayer.sdk.common.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoxu2014 on 16/5/11.
 */
public class LiveTeacherLoginInfo {
    LiveUser user;
    LiveliveRoomInfo liveRoomInfo;
    List<String> privilege=new ArrayList<String>();
    LiveWriteToRedisData writeToRedisData;

    public static LiveTeacherLoginInfo fromJSON(JSONObject js) {
        if (null == js) {
            return null;
        }
        return JsonUtil.json2Bean(js.toString(), LiveTeacherLoginInfo.class);

    }

    public LiveUser getUser() {
        return user;
    }

    public void setUser(LiveUser user) {
        this.user = user;
    }

    public LiveliveRoomInfo getLiveRoomInfo() {
        return liveRoomInfo;
    }

    public void setLiveRoomInfo(LiveliveRoomInfo liveRoomInfo) {
        this.liveRoomInfo = liveRoomInfo;
    }

    public List<String> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<String> privilege) {
        this.privilege = privilege;
    }

    public LiveWriteToRedisData getWriteToRedisData() {
        return writeToRedisData;
    }

    public void setWriteToRedisData(LiveWriteToRedisData writeToRedisData) {
        this.writeToRedisData = writeToRedisData;
    }
}

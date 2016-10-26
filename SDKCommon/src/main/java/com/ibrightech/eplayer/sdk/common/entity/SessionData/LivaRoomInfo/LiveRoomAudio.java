package com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo;


import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by junhai on 14-8-13.
 */
public class LiveRoomAudio {
    public String streamId;
    public String sourceUrl;
    public String playUrl;

    public boolean isPushStream;

    public String nickName;

    public static LiveRoomAudio fromJson(JSONObject jsonObject) {
        LiveRoomAudio msg = new LiveRoomAudio();
        msg.streamId = jsonObject.optString("streamId");
        msg.sourceUrl = jsonObject.optString("sourceUrl");
        if (EplayerSetting.getInstance().isTestServer) {
            msg.playUrl = msg.sourceUrl;

        } else {

            if (!StringUtils.isValid(EplayerSetting.getInstance().playRtmpUrl)) {
                JSONArray infos = jsonObject.optJSONArray("info");
                for (int i = 0; i < infos.length(); i++) {
                    JSONObject info = infos.optJSONObject(i);
                    String streamType = info.optString("streamType");
                    if (streamType.equals("rtmp")) {
                        msg.playUrl = info.optString("playUrl");
                        break;
                    }
                }

                if (msg.playUrl == null) {
                    msg.playUrl = "rtmp://video.upuday.com:1935/live/" + msg.streamId;
                }
            } else {
                msg.playUrl = EplayerSetting.getInstance().playRtmpUrl + msg.streamId;
            }


        }



        msg.isPushStream = (jsonObject.optInt("pushStreamStatus")==1);

        msg.nickName = jsonObject.optString("nickName");

        return msg;
    }
}

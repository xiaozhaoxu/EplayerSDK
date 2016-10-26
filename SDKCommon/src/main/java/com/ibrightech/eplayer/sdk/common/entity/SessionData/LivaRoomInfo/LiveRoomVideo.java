package com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomMainVideoType;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by junhai on 14-8-13.
 */
public class LiveRoomVideo {


    public String streamId;
    public String sourceUrl;
    public String playUrl;

    public boolean channelOpen;

    public String channelId;
    public String channelName;

    public boolean isPushStream;
    public boolean flashPushStream;

    public EplayerLiveRoomMainVideoType mainVideo;

    public String nickName;

    public static LiveRoomVideo fromJson(JSONObject jsonObject,boolean supportMobile) {
        LiveRoomVideo msg = new LiveRoomVideo();
        msg.streamId = jsonObject.optString("streamId");
        msg.sourceUrl = jsonObject.optString("sourceUrl");
        if(EplayerSetting.getInstance().isTestServer){
            msg.playUrl = msg.sourceUrl;

        }else {

              if(!StringUtils.isValid(EplayerSetting.getInstance().playRtmpUrl)){
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
              }else{
                  msg.playUrl=EplayerSetting.getInstance().playRtmpUrl+msg.streamId;
              }

        }





        msg.channelOpen = jsonObject.optBoolean("channelStatus");

        msg.channelId = jsonObject.optString("channelId");
        msg.channelName = jsonObject.optString("channelName");

        msg.isPushStream = (jsonObject.optInt("pushStreamStatus")==1);
        msg.flashPushStream = (jsonObject.optInt("flashPushStream")==1);


        int mainVideo = jsonObject.optInt("mainVideo");
        msg.mainVideo = EplayerLiveRoomMainVideoType.getEnumBykey(mainVideo);

        msg.nickName = jsonObject.optString("nickName");

        return msg;
    }
}

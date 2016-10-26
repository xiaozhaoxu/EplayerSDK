package com.ibrightech.eplayer.sdk.common.entity;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;

import org.json.JSONObject;

/**
 * Created by zhaoxu2014 on 14-11-19.
 */
public class LiveInfo {
    public String _id;
    public String subject;
    public String introduce;
    public String beginTime;
    public EplayerLiveRoomLiveStatus liveStatus;
    public String posterURL;

    public static LiveInfo fromJson(JSONObject jsonObject) {
        LiveInfo liveInfo=new LiveInfo();
        liveInfo._id=jsonObject.optString("_id");
        liveInfo.subject=jsonObject.optString("subject");
        liveInfo.introduce=jsonObject.optString("introduce");
        liveInfo.posterURL=jsonObject.optString("posterURL");
        liveInfo.processLiveStatus(jsonObject.optInt("liveStatus"));

        String  bt=jsonObject.optString("beginTime");
        try {
            if(StringUtils.isValid(bt)){
              String []strs=bt.split(" ");
                if(strs.length==2){
                    String mhs=strs[1];
                    String []mhss=mhs.split(":");
                    if(mhss.length==3){
                        liveInfo.beginTime= mhs.substring(0, mhs.lastIndexOf(":"));
                    }else if(mhss.length==2){
                        liveInfo.beginTime=mhs;
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
            liveInfo.beginTime="";
        }


        return liveInfo;
    }

    public void processLiveStatus(int ls) {
        this.liveStatus = EplayerLiveRoomLiveStatus.getEnumByKey(ls);
    }


}

package com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLayoutType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveModeType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomScaleType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamIndex;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.TeacherInfo;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackSegment;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junhai on 14-8-13.
 */
public class LiveRoomInfoData {

    public EplayerLiveRoomStreamType streamType;
    public boolean isStreamPush;
    public EplayerLiveRoomStreamIndex streamIndex;

    public String subject;

    public EplayerLiveRoomScaleType scale;
    public EplayerLiveRoomLiveModeType liveMode;
    public EplayerLiveRoomLayoutType layout;

    public String beginTime;
    public String endTime;

    public String playbackBeginTime;
    public String playbackEndTime;

    public String jsonPath;

    public String shareUrl;
    public String qrcodeImg;

    public boolean playbackPrepare=true;//回看是否已经准备好了，没的准备好的话，需要关闭课堂

    public boolean canSplice;
    public PlaybackSegment currentPlaybackSegment=null;//当前播放的回看

    public boolean canShare;
    public boolean canChat;
    public boolean playMusic;
    public int  musicType;
    public String introduce;
    public int baseNum;

    public EplayerLiveRoomLiveStatus liveStatus;

    public boolean isOpen;
    public boolean isDelete;

    public LiveRoomVideo video1;
    public LiveRoomVideo video2;
    public LiveRoomVideo video3;

    public LiveRoomAudio audio1;
    public LiveRoomAudio audio2;
    public LiveRoomAudio audio3;
    public List<String> blackList=new ArrayList<String>();

    public List<TeacherInfo> teacherList =new ArrayList<TeacherInfo>();//主讲老师
    public String currentTeacherUUID="";
    public static final String TEACHER_DEAFULT_UUID="-1";

    public String getCurrentTeacherUUID() {
        if(StringUtils.isValid(currentTeacherUUID)){
            return currentTeacherUUID;
        }else{
            if(null!=teacherList&&teacherList.size()>0){
                return teacherList.get(0)._id;
            }else{
                //todo
                return TEACHER_DEAFULT_UUID;
            }
        }
    }

    public static LiveRoomInfoData fromJson(JSONObject jsonObject) {
        LiveRoomInfoData msg = new LiveRoomInfoData();
        msg.subject = jsonObject.optString("subject");

        String introduce=jsonObject.optString("introduce");
        if(StringUtils.isValid(introduce)){
            msg.introduce=introduce;
        }else{
            msg.introduce="暂无简介";
        }

        msg.playbackBeginTime =jsonObject.optString("playbackBeginTime");
        msg.playbackEndTime =jsonObject.optString("playbackEndTime");

        JSONArray jsonArray= jsonObject.optJSONArray("blackList");

        if(null!=jsonArray){
            for(int k=0;k<jsonArray.length();k++){
                try {
                    String black= (String) jsonArray.get(k);
                    msg.blackList.add(black);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        int scale = jsonObject.optInt("scale");
        msg.scale=EplayerLiveRoomScaleType.getEnumBykey(scale);


        int liveMode = jsonObject.optInt("liveMode");
        msg.liveMode=EplayerLiveRoomLiveModeType.getEnumBykey(liveMode);


        int layout = jsonObject.optInt("layout");
        msg.layout = EplayerLiveRoomLayoutType.getEnumBykey(layout);


        msg.beginTime = jsonObject.optString("beginTime");
        msg.endTime = jsonObject.optString("endTime");

        msg.shareUrl = jsonObject.optString("shareUrl");
        msg.qrcodeImg = jsonObject.optString("qrcodeImg");

        msg.canShare = jsonObject.optBoolean("canShare");
        msg.canChat = (jsonObject.optInt("canChat") == 0);
        msg.playMusic =( jsonObject.optInt("playMusic")==1);
        msg.musicType = 1;

        msg.baseNum =jsonObject.optInt("baseNum");

        msg.isOpen = jsonObject.optBoolean("state");
        msg.isDelete = jsonObject.optBoolean("del");

        boolean supportMobile =  jsonObject.optInt("supportMobile")==1;

        msg.video1 =LiveRoomVideo.fromJson(jsonObject.optJSONObject("video1"),supportMobile);
        msg.video2 =LiveRoomVideo.fromJson(jsonObject.optJSONObject("video2"),supportMobile);
        msg.video3 =LiveRoomVideo.fromJson(jsonObject.optJSONObject("video3"),supportMobile);

        msg.audio1 =LiveRoomAudio.fromJson(jsonObject.optJSONObject("audio1"));
        msg.audio2 =LiveRoomAudio.fromJson(jsonObject.optJSONObject("audio2"));
        msg.audio3 =LiveRoomAudio.fromJson(jsonObject.optJSONObject("audio3"));

        msg.processLiveStatus(jsonObject.optInt("liveStatus"),false);

        msg.checkStreamStatus();

        return msg;
    }

    public void processLiveStatus(int liveStatus,boolean isChangePlayMusicState) {
        if(isChangePlayMusicState){
            this.playMusic=false;
        }

        this.liveStatus=EplayerLiveRoomLiveStatus.getEnumByKey(liveStatus);

    }


    private void checkStreamStatus(){
        if(this.video1.isPushStream){
            this.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo;
            this.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;

            this.isStreamPush =  true;

        }
        else if(this.audio1.isPushStream){
            this.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio;
            this.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;

            this.isStreamPush =  true;

        }
    }
    public String  getPlayUrl(){
        if(this.streamType == EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo ){

            if(this.streamIndex == EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne){
                return this.video1.playUrl;
            }

        }else{
            if(this.streamIndex == EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne){
                return this.audio1.playUrl;
            }

        }


        return null;
    }


    public void processStreamStatus(JSONObject jsonObject){

        this.isStreamPush=(jsonObject.optInt("action")==1);
        String type = jsonObject.optString("type");
        if("audio".equals(type)){
            this.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio;
            int video = jsonObject.optInt("video");

            switch (video) {
                case 1:
                    this.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;
                    break;


            }
        }else if("video".equals(type)){
            this.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo;

            int video = jsonObject.optInt("video");

            switch (video) {
                case 1:
                    this.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;
                    break;

            }
        }else{
            this.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeNone;
            this.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;

        }

        currentTeacherUUID=jsonObject.optString("userUUID");


    }
}

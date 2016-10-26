package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONObject;

/**
 * Created by junhai on 14-11-27.
 */
public class PlaybackInfoProtocol  extends SyncBaseOkHttpProtocol {

    private static final String LIVE_CLASSROOM_ID = "liveId";
    private static final String PID = "pid";


    private static final String DATA = "data";


    private static final String SUBJECT = "subject";
    private static final String BEGIN_TIME = "beginTime";
    private static final String END_TIME = "endTime";

    private static final String JSON_PATH = "jsonPath";

    private String liveClassroomId;
    private String pid;

    private int failHandleMode;
    private String failHandleData;
    private EplayerSessionInfo eplayerSessionInfo;

    public static boolean isTest = false;


    public PlaybackInfoProtocol(String liveClassroomId, String pid,EplayerSessionInfo eplayerSessionInfo) {
        this.liveClassroomId = liveClassroomId;
        this.pid = pid;
        this.eplayerSessionInfo = eplayerSessionInfo;
    }


    @Override
    protected String getUrl() {
        String url = EplayerSetting.getInstance().host + "playback/get-info";
        return url;
    }

    @Override
    protected HttpParams getParams() throws Exception {
        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        JSONObject data = new JSONObject();
        data.put(LIVE_CLASSROOM_ID, this.liveClassroomId);
        if(StringUtils.isValid(this.pid))
            data.put(PID, this.pid);

        return data;
    }

    @Override
    protected Object handleJSON(String backresult) throws Exception {

        try {
            JSONObject result=new JSONObject(backresult);
            //解析Json数据
            JSONObject liveRoomInfoJson = result.getJSONObject(DATA);

            LiveRoomInfoData liveRoomInfo = eplayerSessionInfo.infoData;

            liveRoomInfo.playbackPrepare=(liveRoomInfoJson.optInt("playbackStatus")==0);
            liveRoomInfo.canSplice=(liveRoomInfoJson.optInt("canSplice")==1);
            String subject = liveRoomInfoJson.optString(SUBJECT);
            if(StringUtils.isValid(subject)){
                liveRoomInfo.subject = subject;
            }
            liveRoomInfo.playbackBeginTime = liveRoomInfoJson.optString(BEGIN_TIME);
            liveRoomInfo.playbackEndTime = liveRoomInfoJson.optString(END_TIME);

            liveRoomInfo.jsonPath = liveRoomInfoJson.optString(JSON_PATH);

            eplayerSessionInfo.infoData = liveRoomInfo;


        } catch (Exception e) {
            LogUtil.e("Parse LiveRoom data Exception! ", e.getMessage());
        }
        return null;

    }

    @Override
    protected boolean isGetMode() {
        return false;
    }
}

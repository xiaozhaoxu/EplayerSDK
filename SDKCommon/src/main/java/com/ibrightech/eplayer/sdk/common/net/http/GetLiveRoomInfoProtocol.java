package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.TeacherInfo;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junhai on 14-11-20.
 */
public class GetLiveRoomInfoProtocol extends SyncBaseOkHttpProtocol {

    private static final String LIVE_CLASSROOM_ID = "liveRoomId";

    private static final String LIVE_ROOM_INFO = "liveRoomInfo";

    private String liveClassroomId;
    private EplayerSessionInfo eplayerSessionInfo;

    private int failHandleMode;
    private String failHandleData;

    public static boolean isTest = false;

    LiveRoomInfoData liveRoomInfo;
    boolean isSave=true;//因为多个页面都会调用直播信息接口，其中有一部分是不需要将其保存在 EplayerSessionInfo.sharedSessionInfo().infoData中的，此处加了一个标识位



    public GetLiveRoomInfoProtocol(String liveClassroomId,EplayerSessionInfo eplayerSessionInfo) {
        this(liveClassroomId,false,eplayerSessionInfo);
    }
    public GetLiveRoomInfoProtocol(String liveClassroomId, boolean isSave,EplayerSessionInfo eplayerSessionInfo) {
        this.liveClassroomId = liveClassroomId;
        this.isSave=isSave;
        this.eplayerSessionInfo=eplayerSessionInfo;
    }



    public String getLiveClassroomId() {
        return liveClassroomId;
    }

    public void setLiveClassroomId(String liveClassroomId) {
        this.liveClassroomId = liveClassroomId;
    }

    @Override
    protected String getUrl() {
        String url = EplayerSetting.getInstance().host + "getLiveRoomInfo";
        return url;
    }

    @Override
    protected HttpParams getParams() throws Exception {
        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        JSONObject data = new JSONObject();

        data.put(LIVE_CLASSROOM_ID, getLiveClassroomId());

        return data;
    }

    @Override
    protected Object handleJSON(String backresult) throws Exception {
        LogUtil.d("UserLoginProtocol", backresult);



        try {
            JSONObject result=new JSONObject(backresult);
            if (result.isNull(LIVE_ROOM_INFO)) {
                this.failHandleMode = result.optInt("failHandleMode");
                this.failHandleData = result.optString("failHandleData");

                return null;
            }
            //解析Json数据
            JSONObject liveRoomInfoJson = result.getJSONObject(LIVE_ROOM_INFO);
            liveRoomInfo = LiveRoomInfoData.fromJson(liveRoomInfoJson);
            JSONArray jsonArray= (JSONArray) result.get("teacherInfo");
            List<TeacherInfo> teacherList =new ArrayList<TeacherInfo>();
            if(jsonArray!=null&&jsonArray.length()>0){
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jo  = (JSONObject) jsonArray.get(i);
                    String _id= jo.optString("_id");
                    String name=jo.optString("name");
                    String headImg=jo.optString("headImg");
                    TeacherInfo teacherInfo=new TeacherInfo(_id,name,headImg);
                    teacherList.add(teacherInfo);
                }
            }
            liveRoomInfo.teacherList=teacherList;

            if(isSave) {
                eplayerSessionInfo.infoData = liveRoomInfo;
            }

        } catch (Exception e) {
            LogUtil.e("Parse LiveRoom data Exception! ", e.getMessage());
        }
        return liveRoomInfo;
    }

    @Override
    protected boolean isGetMode() {
        return false;
    }
}
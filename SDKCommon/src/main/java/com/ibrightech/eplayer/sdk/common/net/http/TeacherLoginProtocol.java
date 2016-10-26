package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomStreamConfig;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.TeacherInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.UserSessionInfo;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daocren on 14-1-6.
 */
public class TeacherLoginProtocol extends AsyncBaseOkHttpProtocol {

    private static final String LIVE_CLASSROOM_ID = "liveClassroomId";
    private static final String USER_KEY = "userKey";
    private static final String USER_UUID= "userUUID";
    private static final String USER_TYPE= "userType";
    private static final String FROM_STR= "fromStr";

    private static final String LIVE_ROOM_INFO = "liveRoomInfo";
    private static final String USER = "user";
    private static final String PRIVILEGE = "privilege";

    private static final String CUSTOMER = "customer";//value:abc
    private static final String ACCOUNT = "account";
    private static final String PASSWD = "passwd";

    private static final String EXSTR = "exStr";
    private static final String PSTR = "p";


    EPlayerData playerData;
    EplayerSessionInfo eplayerSessionInfo;



    public TeacherLoginProtocol(EPlayerData playerData,EplayerSessionInfo eplayerSessionInfo) {
        this.playerData = playerData;
        this.eplayerSessionInfo = eplayerSessionInfo;
    }




    @Override
    protected String getUrl() {
        String url = EplayerSetting.getInstance().host + "entry/teacherLogin";

        return url;
    }

    @Override
    protected HttpParams getParams() throws Exception {
        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {

        JSONObject data = new JSONObject();
        if(!CheckUtil.isEmpty(playerData)) {
            data.put("customer", playerData.customer);
            data.put("liveClassroomId", playerData.liveClassroomId);
            data.put("userToken", playerData.validateStr);
        }




        return data;
    }

    @Override
    protected Object handleJSON(String backresult) throws Exception {
        LogUtil.d("UserLoginProtocol", backresult.toString());


        try {



            JSONObject result=new JSONObject(backresult);


            //解析Json数据
            JSONObject liveRoomInfoJson = result.getJSONObject(LIVE_ROOM_INFO);
            LiveRoomInfoData liveRoomInfo = LiveRoomInfoData.fromJson(liveRoomInfoJson);

            List<LiveRoomStreamConfig> streamConfigList =new ArrayList<LiveRoomStreamConfig>();
            JSONArray configJsonArray= (JSONArray) result.get("pushStreamConfig");
            if(null!=configJsonArray&&configJsonArray.length()>0){
                for(int i=0;i<configJsonArray.length();i++){
                    JSONObject jo  = (JSONObject) configJsonArray.get(i);
                    LiveRoomStreamConfig lc= LiveRoomStreamConfig.fromJson(jo);
                    streamConfigList.add(lc);
                }
            }



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

            JSONObject userJson = result.optJSONObject(USER);


            JSONObject  writeToRedisData= (JSONObject) result.opt("writeToRedisData");

            UserSessionInfo userInfo = new UserSessionInfo();


            userInfo.customer = playerData.customer;
            userInfo.exStr = playerData.validateStr;
            userInfo.liveClassroomId = playerData.liveClassroomId;

            userInfo.fromJson(userJson);
            Object privilege=result.opt(PRIVILEGE);
            userInfo.privilege=privilege;
            userInfo.writeToRedisData=writeToRedisData;


            eplayerSessionInfo.userInfo = userInfo;
            eplayerSessionInfo.infoData = liveRoomInfo;
            eplayerSessionInfo.streamConfigList=streamConfigList;



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

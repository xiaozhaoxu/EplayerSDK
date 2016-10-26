package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerLoginType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.TeacherInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.UserSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.util.DeviceUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daocren on 14-1-6.
 */
public class UserLoginProtocol extends SyncBaseOkHttpProtocol {

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

    private EPlayerData playerData;
    private EplayerSessionInfo eplayerSessionInfo;
    private String userKey;

    private int failHandleMode;
    private String failHandleData;

    public static boolean isTest=false;//false;



    public UserLoginProtocol(EPlayerData playerData,EplayerSessionInfo eplayerSessionInfo) {
        this.playerData = playerData;
        this.eplayerSessionInfo = eplayerSessionInfo;

        this.setUserKey(DeviceUtil.getUDID());
    }


    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }


    @Override
    protected String getUrl() {
        String url = EplayerSetting.getInstance().host + "entry/studentLogin";
        if (EplayerSetting.getInstance().isPlayback) {
            url = EplayerSetting.getInstance().host + "entry/replayLogin";
        }
        return url;
    }

    @Override
    protected HttpParams getParams() throws Exception {
        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        JSONObject data = new JSONObject();

        data.put(USER_KEY, getUserKey());
        data.put(USER_UUID, getUserKey());
        data.put(USER_TYPE, playerData.usertype.value());

        if(playerData!=null&&playerData.customer!=null)
            data.put(CUSTOMER, playerData.customer);

        if(playerData!=null&&playerData.liveClassroomId!=null)
            data.put(LIVE_CLASSROOM_ID, playerData.liveClassroomId);

        if(playerData.loginType== EPlayerLoginType.EPlayerLoginTypeNone){

        }else if(playerData.loginType== EPlayerLoginType.EPlayerLoginTypeUserPwd){

            if(playerData!=null&&playerData.user!=null)
                data.put(ACCOUNT, playerData.user);
            if(playerData!=null&&playerData.pwd!=null)
                data.put(PASSWD, playerData.pwd);

        }else if(playerData.loginType== EPlayerLoginType.EPlayerLoginTypeAuthReverse){

            if(playerData!=null&&playerData.validateStr!=null)
                data.put(EXSTR, playerData.validateStr);

        }else if(playerData.loginType== EPlayerLoginType.EPlayerLoginTypeAuthForward){

            if(playerData!=null&&playerData.validateStr!=null)
                data.put(PSTR, playerData.validateStr);

        }


        data.put("soooner", "xf123");
        if (EplayerSetting.getInstance().isPlayback) {
            data.put("soooner", "abc");
        }


        String ua =DeviceUtil.getUserAgentString();

        String baseString = android.util.Base64.encodeToString(ua.getBytes("utf-8"), android.util.Base64.DEFAULT);

        data.put(FROM_STR, "android|"+baseString);

        return data;
    }

    @Override
    protected Object handleJSON(String backresult) throws Exception {
        LogUtil.d("UserLoginProtocol", backresult.toString());


        try {
            JSONObject result=new JSONObject(backresult);
            if (result.isNull(LIVE_ROOM_INFO)) {
                this.failHandleMode =  result.optInt("failHandleMode");
                this.failHandleData =  result.optString("failHandleData");

                return null;
            }

            //解析Json数据
            JSONObject liveRoomInfoJson = result.getJSONObject(LIVE_ROOM_INFO);
            LiveRoomInfoData liveRoomInfo = LiveRoomInfoData.fromJson(liveRoomInfoJson);

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
            userInfo.user = playerData.user;
            userInfo.pwd = playerData.pwd;

            userInfo.fromJson(userJson);
            Object privilege=result.opt(PRIVILEGE);
            userInfo.privilege=privilege;
            userInfo.writeToRedisData=writeToRedisData;


            eplayerSessionInfo.userInfo = userInfo;
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

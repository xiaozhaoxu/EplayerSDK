package com.ibrightech.eplayer.sdk.common.loading;

import android.content.Context;
import android.os.AsyncTask;

import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerInitType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerUserInfoUserType;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.net.http.BaseOkHttpProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.GetLiveRoomInfoProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.GetWayProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.TeacherLoginProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.UserLoginProtocol;
import com.ibrightech.eplayer.sdk.common.net.ws.event.EplayerInitEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;

/**
 * Created by zhaoxu2014 on 16/8/29.
 */
public abstract class EplayerBaseLoading implements EplayerLoading{
    public boolean isCancelled = false;
    protected EPlayerData data;
    protected Context context;
    protected EplayerSessionInfo sessionInfo;  //基本数据

    public boolean executeCancel() {
        if (this.isCancelled) {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeCanceled);
            EventBus.getDefault().post(msg);
        }
        System.gc();
        return this.isCancelled;
    }

    @Override
    public void startLoading() {



        if(data==null){
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeDataError);
            msg.obj = "没有任何参数";
            EventBus.getDefault().post(msg);
        }else {

            String string = data.validate();
            if (!CheckUtil.isEmpty(string)) {
                EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeDataError);
                msg.obj = string;

                EventBus.getDefault().post(msg);
                return;
            }
        }


        tk.execute();
    }
    AsyncTask tk= new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] params) {
            refreshConfig();
            return null;
        }
    };

    @Override
    public void cancelLoading() {
        this.isCancelled =true;

    }

    private void refreshConfig(){
        if(executeCancel()){
            return;
        }

        EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeLoadConfig);
        EventBus.getDefault().post(msg);

        GetWayProtocol protocol  =  new GetWayProtocol();
        protocol.execute(context, new BaseOkHttpProtocol.CallBack() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int errorCode, String msg, Object object) {
                startLogin();
            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                startLogin();
                return true;
            }

            @Override
            public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

            }
        });


    }
    protected void startLogin(){
        if(executeCancel()){
            return;
        }

        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeLogin);
            EventBus.getDefault().post(msg);
        }

        if(data.usertype== EplayerUserInfoUserType.UserInfoUserTypeStudent){
            UserLoginProtocol protocol = new UserLoginProtocol(this.data,sessionInfo);
            protocol.execute(context ,new BaseOkHttpProtocol.CallBack(){

                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(int errorCode, String msg, Object object) {

                    if(errorCode==0){
                        getLiveRoomInfo();
                    }else{
                        if(errorCode==1){
                            msg="直播信息获取失败";
                        }else if(errorCode==2){
                            msg="直播信息无效";
                        }else if(errorCode==3){
                            msg="用户校验失败";
                        }else if(errorCode==4){
                            msg="此账号未购买此课程";
                        }else if(errorCode==5){
                            msg="账号密码错误";
                        }

                        EplayerInitEvent eventmsg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeLoginError);
                        eventmsg.obj=msg;
                        EventBus.getDefault().post(eventmsg);
                    }

                }

                @Override
                public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                    EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeLoginError);
                    EventBus.getDefault().post(msg);
                    return true;
                }

                @Override
                public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

                }
            });
        }else if(data.usertype== EplayerUserInfoUserType.UserInfoUserTypeTeacher){
            TeacherLoginProtocol protocol = new TeacherLoginProtocol(this.data,sessionInfo);
            protocol.execute(context,new BaseOkHttpProtocol.CallBack(){

                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(int errorCode, String message, Object object) {
                    EplayerInitEvent msg =null;

                    if(errorCode==0){
                        msg=new EplayerInitEvent(EplayerInitType.EplayerTeacherLoginFinished);
                    }else{
                        if(errorCode==1){
                            message="直播信息获取失败";
                        }else if(errorCode==2){
                            message="直播信息无效";
                        }else if(errorCode==3){
                            message="用户校验失败";
                        }else if(errorCode==4){
                            message="此账号未购买此课程";
                        }else if(errorCode==5){
                            message="账号密码错误";
                        }
                        msg=new EplayerInitEvent(EplayerInitType.EplayerInitTypeLoginError);
                        msg.obj=message;
                    }

                    EventBus.getDefault().post(msg);
                    getLiveRoomInfoSuccess();
                }

                @Override
                public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                    EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeLoginError);
                    EventBus.getDefault().post(msg);
                    return false;
                }

                @Override
                public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

                }
            });
        }


    }
    protected void getLiveRoomInfo(){
        if(executeCancel()){
            return;
        }

        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeGetRoomInfo);
            EventBus.getDefault().post(msg);
        }

        GetLiveRoomInfoProtocol liveRequest = new GetLiveRoomInfoProtocol(data.liveClassroomId,sessionInfo);

        liveRequest.execute(context, new BaseOkHttpProtocol.CallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int errorCode, String msg, Object object) {
                getLiveRoomInfoSuccess();

            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeGetRoomInfoError);
                EventBus.getDefault().post(msg);
                return false;
            }

            @Override
            public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

            }
        });
    }

    protected abstract  void getLiveRoomInfoSuccess();
}

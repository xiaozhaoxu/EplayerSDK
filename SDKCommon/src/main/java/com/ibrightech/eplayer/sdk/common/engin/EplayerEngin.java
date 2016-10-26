package com.ibrightech.eplayer.sdk.common.engin;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerPlayModelType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerConstant;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerVoteType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.UserSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.ISessionInfo;
import com.ibrightech.eplayer.sdk.common.loading.EplayerLoading;
import com.ibrightech.eplayer.sdk.common.net.ws.event.MsgStatusChangeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.UserCountReadEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.net.EplayerSocket;
import com.ibrightech.eplayer.sdk.common.net.ws.net.Ws;
import com.ibrightech.eplayer.sdk.common.net.ws.pb.ConverterMsg;
import com.ibrightech.eplayer.sdk.common.net.ws.pb.Wraper;
import com.ibrightech.eplayer.sdk.common.net.ws.vo.Msg;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.DeviceUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public abstract class EplayerEngin {
    protected EplayerSocket eplayerSocket;
    private static final String TAG = EplayerEngin.class.getSimpleName();
    public  static final String  TEST_USER="soooner2";//替人测试名
    protected   EventBus bus = EventBus.getDefault();

    protected ISessionInfo sessionInfo;  //基本数据
    protected EplayerLoading eplayerLoading;  //loading

    protected EPlayerData ePlayerData;
    protected Context context;
    private static EplayerEngin mInstance;                 //单例

    public static boolean isPlayback(){
        if(!CheckUtil.isEmpty(mInstance)&&mInstance instanceof EplayerPlaybackEngin )
            return true;
        return false;
    }
    public static EplayerEngin getInstance() {
        return mInstance;
    }

    public static EplayerEngin initInstance(Context context, EPlayerData ePlayerData) {

        synchronized (EplayerEngin.class) {
            mInstance =  allocInit(context, ePlayerData);
        }
        return mInstance;
    }

    public  static   EplayerEngin  allocInit(Context ctx, EPlayerData ePlayerData){
         if(!CheckUtil.isEmpty(ePlayerData)){
             if(ePlayerData.playModel== EPlayerPlayModelType.EPlayerPlayModelTypePlayback){
                 mInstance=new EplayerPlaybackEngin();
             }else{
                 mInstance=new EplayerLiveEngin();
             }

             mInstance.context=ctx;
             mInstance.ePlayerData=ePlayerData;
             mInstance.init();
         }

        return mInstance;

    };

    public EplayerSessionInfo getSessionInfo() {
        return (EplayerSessionInfo)sessionInfo;
    }




    public abstract void  init();

    public abstract void  distory();

    public abstract void  startLoading();

    public abstract void  cancelLoading();

    public abstract void  startClassEngin();

    public abstract void  finishClassEngin();



    //joinRoom
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void joinRoom() {

        JSONObject json = new JSONObject();
        try {
            UserSessionInfo user = getSessionInfo().userInfo;
            json.put("msgType", "joinRoomReq");
            json.put("liveClassroomId", user.liveClassroomId);
            json.put("nickname", user.nickname);
            json.put("privilege", user.privilege);
            json.put("userType", user.userType.value());
            json.put("userKey", DeviceUtil.getUDID());
            json.put("writeToRedisData", user.writeToRedisData);

            if(TEST_USER.equals(user.user)){
                json.put("userUUID", DeviceUtil.getUDID());
            }else{

                if(user.userId>0)
                    json.put("userUUID", user.userId);
                else
                    json.put("userUUID", DeviceUtil.getUDID());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtil.d(TAG, "joinRoomReq: " + json.toString());

        Msg msg = new Msg();
        msg.setEvent(json);
        msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

        LogUtil.d(TAG, "发送消息请求: 加入房间");
        new SendTask().execute(msg);
    }
    //改变流状态
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void changeStreamStatus(boolean isVideo,boolean isPush){
        JSONObject json = null;
        try {
            UserSessionInfo user =getSessionInfo().userInfo;
            json = new JSONObject();
            json.put("liveClassroomId", user.liveClassroomId);
            json.put("customer", user.customer);
            json.put("userKey", DeviceUtil.getUDID());
            json.put("msgType", EplayerConstant.MSG_TYPE_STREAM_STATUS_REQ);
            json.put("video", 1);
            json.put("audio", 1);
            if(isVideo){
                json.put("type", "video");
            }else{
                json.put("type", "audio");
            }

            if(isPush){
                json.put("action", 1);
            }else{
                json.put("action",0);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Msg msg = new Msg();
        msg.setEvent(json);
        msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

        Log.d(TAG, "发送消息请求: 改变流状态");
        new SendTask().execute(msg);
    }

    //聊天控制
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void changechatReq(boolean closeChat) {
        {  //
            int act=closeChat?0:1;
            JSONObject json = null;
            try {
                UserSessionInfo user = getSessionInfo().userInfo;
                json = new JSONObject();
                json.put("liveClassroomId", user.liveClassroomId);

                json.put("action",  act);
                json.put("customer", user.customer);

                json.put("msgType", EplayerConstant.MSG_TYPE_CHAT_CONTROL_REQ);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Msg msg = new Msg();
            msg.setEvent(json);
            msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

            Log.d(TAG, "发送消息请求: 更改聊天控制状态");
            new SendTask().execute(msg);
        }
    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void changeStatusReq(EplayerLiveRoomLiveStatus status) {
        {  //
            JSONObject json = null;
            try {
                UserSessionInfo user = getSessionInfo().userInfo;
                json = new JSONObject();
                json.put("liveClassroomId", user.liveClassroomId);

                json.put("action",  2);
                json.put("customer", user.customer);
                json.put("newStatus", status.value() );

                json.put("msgType", EplayerConstant.MSG_TYPE_LIVE_STATUS_REQ);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Msg msg = new Msg();
            msg.setEvent(json);
            msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

            Log.d(TAG, "发送消息请求: 更改直播状态");
            new SendTask().execute(msg);
        }
    }

    //initQAReq
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void initStatusReq() {
        {  //
            JSONObject json = null;
            try {
                UserSessionInfo user = getSessionInfo().userInfo;
                json = new JSONObject();
                json.put("liveClassroomId", user.liveClassroomId);
                json.put("customer", user.customer);
                json.put("action", 1);
                json.put("msgType", EplayerConstant.MSG_TYPE_LIVE_STATUS_REQ);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Msg msg = new Msg();
            msg.setEvent(json);
            msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

            Log.d(TAG,"发送消息请求: 直播状态");
            new SendTask().execute(msg);
        }
        {
            JSONObject json = null;
            try {
                String liveRoomId = getSessionInfo().userInfo.liveClassroomId;
                json = new JSONObject();
                json.put("liveClassroomId", liveRoomId);
                json.put("msgType", EplayerConstant.MSG_TYPE_INIT_QA_REQ);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Msg msg = new Msg();
            msg.setEvent(json);
            msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

            LogUtil.d(TAG,"发送消息请求: 初始化Q&A");
            new SendTask().execute(msg);
        }
    }



    /**
     * 用户发送聊天信息
     *
     * @param content
     * @param chatType //聊天消息类型  1系统消息 2 聊天消息  3提问消息
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void chatReq( String content, int chatType) {
        UserSessionInfo user= getSessionInfo().userInfo;
        String liveRoomId = user.liveClassroomId;

        UUID uuid = UUID.randomUUID();

        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("msgType", EplayerConstant.MSG_TYPE_CHAT_REQ);
            json.put("chatInfoKey", uuid + "");
            json.put("userKey", DeviceUtil.getUDID());
            json.put("nickname", user.nickname);
            json.put("liveClassroomId", liveRoomId);
            json.put("userType", user.userType.value());
            json.put("chatType", chatType);
            json.put("content", content);
            String time= DateUtil.getString(DateUtil.getNow());
            json.put("createTime",time);

            SocketMessage socketMessage= SocketMessage.fromJson(json);
            eplayerSocket.chatContentMap.put(uuid + "", socketMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Msg msg = new Msg();
        msg.setEvent(json);
        msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

        new SendTask().execute(msg);
    }
    /*
      学生投票
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void voteReq(String voteKey, EplayerVoteType voteType , int voteValue) {
        UserSessionInfo user= getSessionInfo().userInfo;
        String liveRoomId = user.liveClassroomId;

        UUID uuid = UUID.randomUUID();

        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("msgType", EplayerConstant.MSG_TYPE_STUDENT_VOTE_REQ);
            json.put("liveClassroomId", liveRoomId);
            json.put("voteType", voteType.value());
            json.put("userUUID", uuid + "");
            json.put("userKey", DeviceUtil.getUDID());
            json.put("voteKey", voteKey);
            json.put("voteValue", voteValue);



            SocketMessage socketMessage=SocketMessage.fromJson(json);
            eplayerSocket.chatContentMap.put(uuid + "", socketMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Msg msg = new Msg();
        msg.setEvent(json);
        msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

        new SendTask().execute(msg);
    }

    /*
   * ppt翻页
   *
    */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void initPptPageReq(String pptId,String filename,int page, double pageOffset,String resType) {
        UserSessionInfo user= getSessionInfo().userInfo;
        String liveRoomId = user.liveClassroomId;
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("msgType",EplayerConstant.MSG_TYPE_PPT_PAGE_REQ);
            json.put("pptId", pptId);
            json.put("fileName", filename);
            json.put("page", page);

            json.put("pageOffset", pageOffset);
            if(pageOffset>0){
                json.put("isWordScroll",1);
            }else{
                json.put("isWordScroll",0);
            }

            json.put("resType", resType);
            json.put("liveClassroomId", liveRoomId);
            json.put("isBlank",0);
            json.put("blankColor",2);
            json.put("userId", user.userId);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Msg msg = new Msg();
        msg.setEvent(json);
        msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

        new SendTask().execute(msg);
    }

    /*
  * ppt翻页
  *
   */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void initPptNotifyReq(String assertJson) {
        UserSessionInfo user= getSessionInfo().userInfo;
        String liveRoomId = user.liveClassroomId;
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("msgType",EplayerConstant.MSG_TYPE_MSG_NOTIFY_REQ);
            json.put("type", 21);
            json.put("data",assertJson);
            json.put("liveClassroomId", liveRoomId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Msg msg = new Msg();
        msg.setEvent(json);
        msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

        new SendTask().execute(msg);
    }

    /*
   * 获取点赞总数
   *
           */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void initPraiseReq( ) {
        UserSessionInfo user= getSessionInfo().userInfo;
        LiveRoomInfoData liveRoomInfo= getSessionInfo().infoData;
        String liveRoomId = user.liveClassroomId;
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("msgType", EplayerConstant.MSG_TYPE_INITTEACHERINFOREQ);
            json.put("customer", user.customer);
            if(null!=liveRoomInfo){
                json.put("teacherUUID",liveRoomInfo.getCurrentTeacherUUID());
            }else{
                //todo
                json.put("teacherUUID",LiveRoomInfoData.TEACHER_DEAFULT_UUID);
            }


            json.put("liveClassroomId", liveRoomId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Msg msg = new Msg();
        msg.setEvent(json);
        msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

        new SendTask().execute(msg);
    }
    /**
     * 用户发送赞
     *
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void praiseReq( ) {
        UserSessionInfo user= getSessionInfo().userInfo;
        LiveRoomInfoData liveRoomInfo= getSessionInfo().infoData;
        String liveRoomId = user.liveClassroomId;

        UUID uuid = UUID.randomUUID();

        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("msgType", EplayerConstant.MSG_TYPE_PRAISE_REQ);
            json.put("userKey", DeviceUtil.getUDID());
            json.put("userUUID", uuid);
            json.put("nickname", user.nickname);
            json.put("userType", user.userType.value());
            json.put("customer", user.customer);
            if(null!=liveRoomInfo){
                json.put("teacherUUID",liveRoomInfo.getCurrentTeacherUUID());
            }else{
                //todo
                json.put("teacherUUID",LiveRoomInfoData.TEACHER_DEAFULT_UUID);
            }


            json.put("liveClassroomId", liveRoomId);

            SocketMessage socketMessage=SocketMessage.fromJson(json);
            eplayerSocket.chatContentMap.put(uuid + "", socketMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Msg msg = new Msg();
        msg.setEvent(json);
        msg.setType(Msg.MESSAGE_TYPE_CLIENT_EVENT);

        new SendTask().execute(msg);
    }
    @Subscribe
    public void onEventBackgroundThread(UserCountReadEvent event) {
        LogUtil.d(TAG, "user count event");
        this.loadUserCount();
    }

    /**
     * 用户列表初始化请求
     * todo 该方法会被休眠线程调用有可能导致处理逻辑有问题，需要解决
     *
     * @throws Exception
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   void loadUserCount() {
        if(getSessionInfo().userInfo==null)
            return;

        String liveRoomId = getSessionInfo().userInfo.liveClassroomId;
        Msg msg = new Msg();
        msg.setEvent(liveRoomId);
        msg.setType(Msg.MESSAGE_TYPE_USER_COUNT);

        new SendTask().execute(msg);
    }



    private   byte[] boxing(byte[] data) {
        byte[] b = new byte[data.length + 1];
        b[0] = EplayerSocket.BAG_ID_MSG;
        System.arraycopy(data, 0, b, 1, data.length);
        return b;
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public   class SendTask extends AsyncTask<Msg, Void, Void> {

        @Override
        protected Void doInBackground(Msg... params) {
            final Msg vo = params[0];
            Wraper.Msg pb = ConverterMsg.msg(vo);
            if (!pb.isInitialized()) {
                throw new RuntimeException("pb is not initialized");
            }
            Ws.send(boxing(pb.toByteArray()), new Ws.SendCallback() {
                @Override
                public void onSent() {
                    sendComplete(vo, Msg.SEND_STATUS_DONE);
                }

                @Override
                public void onError(Exception e) {
                    sendComplete(vo, Msg.SEND_STATUS_ERROR);
                }
            });
            return null;
        }

        private void sendComplete(Msg msg, int status) {
            Log.i(TAG, "emit MsgStatusChangeEvent");
            bus.post(new MsgStatusChangeEvent(msg));
        }
    }


}

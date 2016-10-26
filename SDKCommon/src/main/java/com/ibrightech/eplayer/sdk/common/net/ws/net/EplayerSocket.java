package com.ibrightech.eplayer.sdk.common.net.ws.net;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerConstant;
import com.ibrightech.eplayer.sdk.common.entity.Prainse;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.ForbidMessage;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.SocketMessage;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.VoteMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.VoteStatisticMsgInfo;
import com.ibrightech.eplayer.sdk.common.net.ws.event.ChatControlEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawMsgInfoEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoChangeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoInitEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoResetOrChangeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoSwitchEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadResetEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.ForbinChatEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.FourceLogoutEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.HandShakeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.JoinRoomEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.MusicEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.PraiseEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.PraiseNumEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.SocketMessageEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.UserCountEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.UserCountReadEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VideoAudioStatusEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VoteMsgInfoEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VoteMsgInfoResEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VoteStatisticMsgInfoEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.WsOnBinaryEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.pb.ConverterMsg;
import com.ibrightech.eplayer.sdk.common.net.ws.pb.Wraper;
import com.ibrightech.eplayer.sdk.common.net.ws.vo.Cmt;
import com.ibrightech.eplayer.sdk.common.net.ws.vo.Msg;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * parse raw data into business message
 */
public class EplayerSocket {

    public static final byte BAG_ID_MSG = 0;
    public static final byte BAG_ID_CMD = 1;

    private String TAG = EplayerSocket.class.getSimpleName();

    private EventBus bus = EventBus.getDefault();

    static Timer timer; //定时获取在线人数
    static TimerTask tt;
    static  final int PERIOD_TIME=1000*5;

    private  EplayerSessionInfo eplayerSessionInfo;


    //以键值对的形式保存用户发送的聊天内容, 服务端处理成功后的响应中没有聊天内容只有发送时的绑定的key
    public Map<String, Object> chatContentMap = new HashMap<String, Object>();

    public EplayerSocket(EplayerSessionInfo eplayerSessionInfo) {
         this.eplayerSessionInfo =eplayerSessionInfo;
    }

    public   void init() {
        if(!bus.isRegistered(this))
            bus.register(this);

        Ws.connect();

        timer=new Timer();
        tt=new TimerTask() {
            @Override
            public void run() {
                bus.post(new UserCountReadEvent());
            }
        };
        timer.schedule(tt,0,PERIOD_TIME);

    }

    public   void close() {
        this.closeALL();

        if(null!=timer){
            tt.cancel();
            timer.cancel();
            timer=null;
        }

    }

    private void closeALL(){
        Ws.disconnect();

        if(bus.isRegistered(this))
            bus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(WsOnBinaryEvent event) {
        LogUtil.d(TAG, "binary event");

        if (event.getData().length <= 1) {
            LogUtil.e(TAG, "bad binary frame");
            return;
        }

        byte bagId = event.getData()[0];
        int size = event.getData().length - 1;
        byte[] data = new byte[size];

        System.arraycopy(event.getData(), 1, data, 0, size);

        switch (bagId) {
            case BAG_ID_MSG:
                try {
                    deliverData(data);
                } catch (InvalidProtocolBufferException e) {
                    LogUtil.e(TAG, "parse msg error"+e.getMessage());
                }
                break;
            case BAG_ID_CMD:
                try {
                    deliverCmd(data);
                } catch (InvalidProtocolBufferException e) {
                    LogUtil.e(TAG, "parse cmd error"+e.getMessage());
                }
                break;
            default:
                LogUtil.e(TAG, "unknown bag id: " + bagId);
        }
    }

    private void deliverCmd(byte[] data) throws InvalidProtocolBufferException {
        Wraper.Cmd cmd = Wraper.Cmd.newBuilder().mergeFrom(data).build();

        Cmt voCmt = ConverterMsg.cmt(cmd);

        if (voCmt.getType() == Cmt.CMT_TYPE_PING) {
            LogUtil.e(TAG, "ping...");
        } else if (voCmt.getType() == Cmt.CMT_TYPE_HANDSHAKE) {
            LogUtil.d(TAG, "handshake...");
            eplayerSessionInfo.socketId = voCmt.getTxt();
            bus.post(new HandShakeEvent());
        }

    }

    private void deliverData(byte[] data) throws InvalidProtocolBufferException {
        Wraper.Msg msg = Wraper.Msg.newBuilder().mergeFrom(data).build();

        Msg voMsg = ConverterMsg.msg(msg);

        if (voMsg.getType() == Msg.MESSAGE_TYPE_CLIENT_EVENT) {

            JSONObject jsonObject = (JSONObject) voMsg.getEvent();
            Log.d("---jsonObject---", jsonObject.toString()+"=--"+voMsg.getType());

            String msgType = jsonObject.optString(EplayerConstant.KEY_MSG_TYPE);

            if (EplayerConstant.MSG_TYPE_FORBID_CHAT_REQ.equals(msgType)) {
                Log.d(TAG, "收到新消息通知类型: 禁言/取消禁言");

                ForbidMessage forbidMessage = ForbidMessage.fromJson(jsonObject);
                bus.post(new ForbinChatEvent(forbidMessage));
            }else if (EplayerConstant.MSG_TYPE_INITTEACHERINFORES.equals(msgType) ){
                LogUtil.d(TAG, "收到新消息通知类型:点赞数");
                Prainse prainse=  Prainse.fromJson(jsonObject);
                bus.post(new PraiseNumEvent(prainse));
            } else if (EplayerConstant.MSG_TYPE_PRAISE_REQ.equals(msgType) ){
                LogUtil.d(TAG, "收到新消息通知类型:点赞数 用户点赞触发");
                Prainse prainse=  Prainse.fromJson(jsonObject);
                bus.post(new PraiseNumEvent(prainse));
            }
            else if (EplayerConstant.MSG_TYPE_LIVE_STATUS_REQ.equals(msgType) ) {
                LogUtil.d(TAG, "收到新消息通知类型: 直播状态");

                LiveRoomInfoData infoData = eplayerSessionInfo.infoData;

                infoData.processLiveStatus(jsonObject.optInt("liveStatus"),true);

                bus.post(new VideoAudioStatusEvent(infoData));

            }else if ( EplayerConstant.MSG_TYPE_LIVE_STATUS_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 直播状态 主动请求返回的");

                LiveRoomInfoData infoData = eplayerSessionInfo.infoData;

                infoData.processLiveStatus(jsonObject.optInt("liveStatus"),false);

                bus.post(new VideoAudioStatusEvent(infoData));

            }
            else if (EplayerConstant.MSG_TYPE_MUSIC_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 播放音乐控制");

                LiveRoomInfoData infoData = eplayerSessionInfo.infoData;
                infoData.musicType = jsonObject.optInt("musicType");
                infoData.playMusic = (jsonObject.optInt("action") == 1);

                bus.post(new MusicEvent(infoData));

            } else if (EplayerConstant.MSG_TYPE_STREAM_STATUS_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 流状态");

                LiveRoomInfoData infoData = eplayerSessionInfo.infoData;
                infoData.processStreamStatus(jsonObject);
                bus.post(new VideoAudioStatusEvent(infoData));

            } else if (EplayerConstant.MSG_TYPE_CHAT_CONTROL_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 聊天控制");

                LiveRoomInfoData infoData = eplayerSessionInfo.infoData;
                infoData.canChat = (jsonObject.optInt("action") == 0);

                bus.post(new ChatControlEvent(infoData));

            } else if (EplayerConstant.MSG_TYPE_MSG_NOTIFY_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 消息通知");

                // TODO:消息通知



            } else if (EplayerConstant.MSG_TYPE_FINGER_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到消息响应类型: 学员举手");

                // TODO:学员举手

            } else if (EplayerConstant.MSG_TYPE_ALLOW_ASK_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到消息响应类型: 提问管理");

                // TODO:提问管理

            } else if (EplayerConstant.MSG_TYPE_TEACHER_VOTE_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到消息通知类型: 老师投票");
                VoteMsgInfo voteMsgInfo= VoteMsgInfo.fromJson(jsonObject);
                bus.post(new VoteMsgInfoEvent(voteMsgInfo));


            } else if (EplayerConstant.MSG_TYPE_STUDENT_VOTE_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到消息响应类型: 学生投票");
                boolean success=jsonObject.optInt("code")==0;
                bus.post(new VoteMsgInfoResEvent(success));

            }  else if (EplayerConstant.MSG_TYPE_TEACHER_VOTESTATISTIC_REQ.equals(msgType)) {
                LogUtil.d(TAG, "到消息响应类型: 投票统计结果");

                VoteStatisticMsgInfo msgInfo= VoteStatisticMsgInfo.fromJson(jsonObject);
                bus.post(new VoteStatisticMsgInfoEvent(msgInfo));
            }
            else if (EplayerConstant.MSG_TYPE_INIT_USERLIST_RES.equals(msgType)) {
                LogUtil.d(TAG, "到消息响应类型: 用户列表初始化");

                // TODO:用户列表初始化

            } else if (EplayerConstant.MSG_TYPE_INIT_QA_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到消息响应类型: 初始化Q&A");

                List<SocketMessage> list = new ArrayList<SocketMessage>();

                JSONArray qaListArr = jsonObject.optJSONArray("qaList");
                for (int i = 0; i < qaListArr.length(); i++) {
                    JSONObject qaListDic = qaListArr.optJSONObject(i);

                    SocketMessage tMessage = SocketMessage.fromJson(qaListDic);
                    list.add(0,tMessage);

                }

                bus.post(new SocketMessageEvent(list));
            } else if (EplayerConstant.MSG_TYPE_INIT_BULLETIN_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到消息响应类型: 初始化系统消息");

                // TODO:初始化系统消息

            } else if (EplayerConstant.MSG_TYPE_CHAT_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 聊天信息");
                List<SocketMessage> list = new ArrayList<SocketMessage>();
                SocketMessage tMessage = SocketMessage.fromJson(jsonObject);
                list.add(tMessage);
                bus.post(new SocketMessageEvent(list));

            }else if (EplayerConstant.MSG_TYPE_CHAT_RES.equals(msgType)) {
                try{
                    LogUtil.d(TAG, "收到发言或提问的的返回信息");
                    String uuid=jsonObject.optString("chatInfoKey","");
                    Object ob= chatContentMap.get(uuid);
                    List<SocketMessage> list = new ArrayList<SocketMessage>();
                    SocketMessage tMessage = (SocketMessage) ob;
                    list.add(tMessage);
                    bus.post(new SocketMessageEvent(list));
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
            else if (EplayerConstant.MSG_TYPE_PPT_PAGE_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: PPT白板设置");
                DrawPadInfo drawPadInfo = DrawPadInfo.fromJson(jsonObject);

                new Delete().from(DrawMsgInfo.class).execute();

                bus.post(new DrawPadInfoResetOrChangeEvent(drawPadInfo));

            }  else if (EplayerConstant.MSG_TYPE_PPT_PAGE_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 设置PPT白板设置的反馈");
                int pptid=jsonObject.optInt("pptPageId");
                //todo 这个地方返回的pptid不对
//                DrawPadInfo drawPadInfo = EplayerSessionInfo.sharedSessionInfo().drawPadInfo;
//                drawPadInfo.pptId=pptid;
//                drawPadInfo.page=0;
//
//                bus.post(new DrawPadInfoChangeEvent(drawPadInfo));

            }

            else if (EplayerConstant.MSG_TYPE_DRAW_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 画笔");

                DrawMsgInfo drawMsgInfo = DrawMsgInfo.fromJson(jsonObject);
                drawMsgInfo.loadInfoType();
                drawMsgInfo.save();
                //检测显示页码，
                DrawPadInfo drawPadInfo =  eplayerSessionInfo.drawPadInfo;
                if(drawPadInfo.validateResetPageChange()){
                    bus.post(new DrawPadResetEvent());
                }else{
                    bus.post(new DrawMsgInfoEvent(drawMsgInfo));
                }

            } else if (EplayerConstant.MSG_TYPE_JOIN_ROOM_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到消息响应类型: 加入房间");

                JSONObject pageInfo = jsonObject.optJSONObject("pptPageInfo");
                JSONObject brushInfo = jsonObject.optJSONObject("brushInfo");

                JSONArray draws = brushInfo.optJSONArray("draw");


                for (int i = 0; i < draws.length(); i++) {
                    JSONObject draw = draws.optJSONObject(i);

                    DrawMsgInfo drawMsgInfo = DrawMsgInfo.fromJson(draw);
                    drawMsgInfo.loadInfoType();
                    drawMsgInfo.save();
                    eplayerSessionInfo.drawPageInfo.addDrawMsgInfo(drawMsgInfo);

                }

                DrawPadInfo drawPadInfo = DrawPadInfo.fromJson(pageInfo);
                eplayerSessionInfo.drawPadInfo = drawPadInfo;


                bus.post(new DrawPadInfoInitEvent(drawPadInfo));

                bus.post(new JoinRoomEvent());

            } else if (EplayerConstant.MSG_TYPE_CHG_WB_PROP_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 切换白板");
                DrawPadInfo drawPadInfo = DrawPadInfo.fromJson(jsonObject);

                new Delete().from(DrawMsgInfo.class).execute();

                bus.post(new DrawPadInfoSwitchEvent(drawPadInfo));


            } else if (EplayerConstant.MSG_TYPE_INIT_USER_COUNT_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到消息响应类型: 在线用户数");

                // TODO:获取在线用户数 不使用

            } else if (EplayerConstant.MSG_TYPE_USER_PIST_CHG_REQ.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型: 踢人");

                String socketId = eplayerSessionInfo.socketId;
                if ("del".equals(jsonObject.optString("action")) && socketId.equals(jsonObject.optString("socketId"))) {
                    bus.post(new FourceLogoutEvent());
                }

            } else if (EplayerConstant.MSG_TYPE_PRAISE_RES.equals(msgType)) {
                LogUtil.d(TAG, "收到新消息通知类型:赞");
                Prainse  prainse=Prainse.fromJson(jsonObject);
                bus.post(new PraiseEvent(prainse));

                //todo

            }else {
                LogUtil.d(TAG, "发现新消息类型: 未知格式的消息");

            }
        } else if (voMsg.getType() == Msg.MESSAGE_TYPE_USER_COUNT) {
           //todo 获取在线用户数
            LiveRoomInfoData infoData = eplayerSessionInfo.infoData;
            int userCount = voMsg.getUserCount()+infoData.baseNum;
            LogUtil.d(TAG, "获取在线用户数: "+userCount);


            bus.post(new UserCountEvent(userCount));

        }

    }



}

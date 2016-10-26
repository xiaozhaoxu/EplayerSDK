package com.ibrightech.eplayer.sdk.common.entity.Enum;

/**
 * Created by junhai on 14-8-13.
 */
public class EplayerConstant {

    public static final String KEY_WORD = "word";
    public static final String KEY_PPT = "ppt";

    public static final String KEY_MSG_TYPE = "msgType";

    //1.禁止聊天和取消禁言
    public static final String MSG_TYPE_FORBID_CHAT_REQ = "forbidChatReq";
    public static final String MSG_TYPE_FORBID_CHAT_RES = "forbidChatRes";

    //2.直播状态
    public static final String MSG_TYPE_LIVE_STATUS_REQ = "liveStatusReq";
    public static final String MSG_TYPE_LIVE_STATUS_RES = "liveStatusRes";

    //3.播放音乐
    public static final String MSG_TYPE_MUSIC_REQ = "musicReq";
    public static final String MSG_TYPE_MUSIC_RES = "musicRes";

    //4.流状态
    public static final String MSG_TYPE_STREAM_STATUS_REQ = "streamStatusReq";
    public static final String MSG_TYPE_STREAM_STATUS_RES = "streamStatusRes";

    //5.聊天控制
    public static final String MSG_TYPE_CHAT_CONTROL_REQ = "chatControlReq";
    public static final String MSG_TYPE_CHAT_CONTROL_RES = "chatControlRes";

    //6.消息通知
    public static final String MSG_TYPE_MSG_NOTIFY_REQ = "msgNotifyReq";
    public static final String MSG_TYPE_MSG_NOTIFY_RES = "msgNotifyRes";

    //7.学员举手
    public static final String MSG_TYPE_FINGER_REQ = "fingerReq";
    public static final String MSG_TYPE_FINGER_RES = "fingerRes";

    //8.提问管理
    public static final String MSG_TYPE_ALLOW_ASK_REQ = "allowAskReq";
    public static final String MSG_TYPE_ALLOW_ASK_RES = "allowAskRes";

    //9.老师投票
    public static final String MSG_TYPE_TEACHER_VOTE_REQ = "teacherVoteReq";
    public static final String MSG_TYPE_TEACHER_VOTE_RES = "teacherVoteRes";
    public static final String MSG_TYPE_TEACHER_VOTESTATISTIC_REQ= "voteStatisticReq";

    //10.学生投票
    public static final String MSG_TYPE_STUDENT_VOTE_REQ = "studentVoteReq";
    public static final String MSG_TYPE_STUDENT_VOTE_RES = "studentVoteRes";

    //11.用户列表初始化
    public static final String MSG_TYPE_INIT_USERLIST_REQ = "initUserListReq";
    public static final String MSG_TYPE_INIT_USERLIST_RES = "initUserListRes";

    //12.初始化Q&A
    public static final String MSG_TYPE_INIT_QA_REQ = "initQAReq";
    public static final String MSG_TYPE_INIT_QA_RES = "initQARes";

    //13.初始化系统消息
    public static final String MSG_TYPE_INIT_BULLETIN_REQ = "initBulletinReq";
    public static final String MSG_TYPE_INIT_BULLETIN_RES = "initBulletinRes";

    //14.发送聊天信息
    public static final String MSG_TYPE_CHAT_REQ = "chatReq";
    public static final String MSG_TYPE_CHAT_RES = "chatRes";

    //15.PPT白板设置   ppt翻页\插入图片\声音
    public static final String MSG_TYPE_PPT_PAGE_REQ = "pptPageReq";
    public static final String MSG_TYPE_PPT_PAGE_RES = "pptPageRes";

    //16.画笔
    public static final String MSG_TYPE_DRAW_REQ = "drawReq";

    //17.加入房间
    public static final String MSG_TYPE_JOIN_ROOM_REQ = "joinRoomReq";
    public static final String MSG_TYPE_JOIN_ROOM_RES = "joinRoomRes";

    //18.切换白板
    public static final String MSG_TYPE_CHG_WB_PROP_REQ = "chgWBPropReq";

    //19.获取在线用户数
    public static final String MSG_TYPE_INIT_USER_COUNT_REQ = "initUserCountReq";
    public static final String MSG_TYPE_INIT_USER_COUNT_RES = "initUserCountRes";

    //20.用户列表变化，踢人
    public static final String MSG_TYPE_USER_PIST_CHG_REQ = "userListChgReq";

    //21.点赞
    public static final String MSG_TYPE_PRAISE_REQ = "praiseReq";
    public static final String MSG_TYPE_PRAISE_RES = "praiseRes";
    public static final String MSG_TYPE_INITTEACHERINFOREQ = "initTeacherInfoReq";//获取点赞总数
    public static final String MSG_TYPE_INITTEACHERINFORES = "initTeacherInfoRes";//获取点赞总数

    //权限字段
    public static final String AUTH_TYPE_UPLOAD_COURSEWARE = "uploadCourseware";
    public static final String AUTH_TYPE_UPLOAD_MUSIC = "uploadMusic";
    public static final String AUTH_TYPE_UPLOAD_ATTACHMENT = "uploadAttachment";
    public static final String AUTH_TYPE_PUSH_VIDEO1 = "pushVideo1";
    public static final String AUTH_TYPE_PUSH_VIDEO2 = "pushVideo2";
    public static final String AUTH_TYPE_PUSH_VIDEO3 = "pushVideo3";
    public static final String AUTH_TYPE_CHAT = "chat";
    public static final String AUTH_TYPE_SEND_SYS_MSG = "sendSysMsg";
    public static final String AUTH_TYPE_PLAY_MUSIC = "playMusic";
    public static final String AUTH_TYPE_LIVE_STATUS = "liveStatus";
    public static final String AUTH_TYPE_WATCH_VIDEO = "watchVideo";
    public static final String AUTH_TYPE_DOWNLOAD_ATTACHMENT = "downloadAttachment";
    public static final String AUTH_TYPE_ASK = "ask";
    public static final String AUTH_TYPE_JOIN_CHOOSE = "joinChoose";


}
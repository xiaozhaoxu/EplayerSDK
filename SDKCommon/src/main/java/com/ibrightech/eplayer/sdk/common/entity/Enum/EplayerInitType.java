package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerInitType {

    EplayerInitTypeUnknown,  //未知错误
    EplayerInitTypeLoadConfig,  //加载网关配置
    EplayerInitTypeLoadConfigError, //加载网关配置错误

    EplayerInitTypeGetRoomInfo,     //获取房间信息
    EplayerInitTypeGetRoomInfoError, //获取房间信息错误

    EplayerInitTypeLogin,            //开始登录
    EplayerInitTypeLoginFinished,    //登录成功
    EplayerTeacherLoginFinished,    //老师登录完成

    EplayerInitTypeLoginError,        //登录失败

    EplayerInitTypeSocketConnect,            //socket连接
    EplayerInitTypeSocketConnectFinished,    //socket 连接完成
    EplayerInitTypeSocketConnectError,       //socket 连接失败

    EplayerInitTypeSocketStartJoinRoom,     //开始加入房间
    EplayerInitTypeSocketEndJoinRoom,       //加入房间完成

    EplayerInitTypePlaybackLogin,      //回看登录
    EplayerInitTypePlaybackLoginFinished,   //回看登录成功
    EplayerInitTypePlaybackLoginError,     //回看登录失败

    EplayerInitTypeGetPlaybackInfo,    //回看信息
    EplayerInitTypeGetPlaybackInfoError,   //回看信息失败

    EplayerInitTypePlaybackPlaylist,     //回看列表
    EplayerInitTypePlaybackPlaylistError,  //回看列表失败

    EplayerInitTypePlaybackStatus,  //获取回看状态
    EplayerInitTypePlaybackStatusError,  //回看状态错误

    EplayerInitTypePlaybackDocRcord,  //获取word
    EplayerInitTypePlaybackDocRcordError,  //获取word错误

    EplayerInitTypePlaybackWhiteboard,  //获取白板
    EplayerInitTypePlaybackWhiteboardError,  //回看白板错误

    EplayerInitTypePlaybackListError,  //回看列表失败

    EplayerInitTypeInitFinished,  //loading成功
    EplayerInitTypeInitError,    //loading失败

    EplayerInitTypeDataError,   //数据错误
    EplayerInitTypeCanceled,   //取消操作

}

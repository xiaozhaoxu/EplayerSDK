package com.ibrightech.eplayer.sdk.common.entity.session;

import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPageInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomStreamConfig;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.UserSessionInfo;
import com.ibrightech.eplayer.sdk.common.entity.system.DocumentMap;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junhai on 14-8-13.
 */
public class EplayerSessionInfo implements ISessionInfo{

    public UserSessionInfo userInfo;
    public LiveRoomInfoData infoData;
    public  List<LiveRoomStreamConfig> streamConfigList;

    public DrawPadInfo drawPadInfo;
    public DrawPageInfo drawPageInfo;

    public String socketId;

    public List<String> sendTokens;


//    private static EplayerSessionInfo sharedInstance = null;

    public EplayerSessionInfo() {
        super();
        this.drawPageInfo = new DrawPageInfo();
        this.sendTokens = new ArrayList<String>();
    }
//
//    public static EplayerSessionInfo sharedSessionInfo() {
//        if (sharedInstance == null)
//            sharedInstance = new EplayerSessionInfo();
//
//        return sharedInstance;
//
//    }
//
//    public static void releaseALL() {
//        if(sharedInstance!=null) {
//            sharedInstance.clearALL();
//            sharedInstance = null;
//        }
//    }

    public void clearALL() {
        this.userInfo =null;
        this.infoData = null;

        if(!CheckUtil.isEmpty(sendTokens)) {
            this.sendTokens.clear();
            this.sendTokens = null;
        }

        this.socketId =null;
        this.drawPadInfo = null;

        if(!CheckUtil.isEmpty(this.drawPageInfo)) {
            this.drawPageInfo.clearALL();
            this.drawPageInfo = null;
        }

        DocumentMap.clearALL();

    }

    public void clearSocketInfo(){
        this.userInfo.clearLoginInfo();
        this.drawPageInfo.clearALL();

        this.socketId = null;
        this.drawPadInfo = null;
        this.sendTokens.clear();
    }
}

package com.ibrightech.eplayer.sdk.common.engin;

import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerInitType;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.loading.living.EplayerLiveLoading;
import com.ibrightech.eplayer.sdk.common.net.ws.event.EplayerInitEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.HandShakeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.JoinRoomEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.net.EplayerSocket;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EplayerLiveEngin extends EplayerEngin {


    @Override
    public void init() {
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        new Delete().from(DrawMsgInfo.class).execute();


        sessionInfo = new EplayerSessionInfo();
        eplayerLoading = new EplayerLiveLoading(context,ePlayerData,(EplayerSessionInfo)sessionInfo);

    }

    @Override
    public void distory() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        if(!CheckUtil.isEmpty(eplayerSocket)){
            eplayerSocket.close();
        }
        sessionInfo.clearALL();
        sessionInfo = null;
        eplayerSocket = null;
    }

    @Override
    public void startLoading() {
        eplayerLoading.startLoading();
    }

    @Override
    public void cancelLoading() {
        eplayerLoading.cancelLoading();
        this.finishClassEngin();
    }

    @Override
    public void startClassEngin() {
        if(eplayerSocket!=null){
            eplayerSocket.close();
            eplayerSocket = null;
        }
        eplayerSocket = new EplayerSocket((EplayerSessionInfo)sessionInfo);
        eplayerSocket.init();
    }


    @Override
    public void finishClassEngin() {

        if(eplayerSocket!=null){
            eplayerSocket.close();
            eplayerSocket = null;
        }

    }
    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(EplayerInitEvent event) {
        if(event.type==EplayerInitType.EplayerInitTypeLoginFinished){
            this.startClassEngin();
        }
    }


    //EventBus回调函数
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(HandShakeEvent event) {
        EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeSocketStartJoinRoom);
        EventBus.getDefault().post(msg);
        joinRoom();
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(JoinRoomEvent event) {
        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeSocketEndJoinRoom);
            EventBus.getDefault().post(msg);
        }
        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeInitFinished);
            EventBus.getDefault().post(msg);
        }
    }





}

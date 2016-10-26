package com.ibrightech.eplayer.sdk.common.loading.living;


import android.content.Context;

import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerInitType;
import com.ibrightech.eplayer.sdk.common.entity.session.EplayerSessionInfo;
import com.ibrightech.eplayer.sdk.common.loading.EplayerBaseLoading;
import com.ibrightech.eplayer.sdk.common.net.ws.event.EplayerInitEvent;

import org.greenrobot.eventbus.EventBus;

public class EplayerLiveLoading extends EplayerBaseLoading {


    public EplayerLiveLoading(Context context,EPlayerData ePlayerData,EplayerSessionInfo sessionInfo){
        this.context=context;
        this.data=ePlayerData;
        this.sessionInfo=sessionInfo;
    }

    @Override
    protected void getLiveRoomInfoSuccess() {
        EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeLoginFinished);
        EventBus.getDefault().post(msg);
    }






}

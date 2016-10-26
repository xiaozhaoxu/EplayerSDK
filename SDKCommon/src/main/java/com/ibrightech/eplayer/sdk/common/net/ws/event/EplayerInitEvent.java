package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerInitType;

public class EplayerInitEvent {

    public EplayerInitType type;

    public Object obj;


    public EplayerInitEvent(EplayerInitType type){
        super();

        this.type= type;
    }

}

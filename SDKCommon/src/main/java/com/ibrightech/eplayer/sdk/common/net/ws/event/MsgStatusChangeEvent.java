package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.net.ws.vo.Msg;

/**
 *
 */
public class MsgStatusChangeEvent {
    private Msg msg;

    public MsgStatusChangeEvent(Msg msg) {
        this.msg = msg;
    }

    public Msg getMsg() {
        return msg;
    }
}

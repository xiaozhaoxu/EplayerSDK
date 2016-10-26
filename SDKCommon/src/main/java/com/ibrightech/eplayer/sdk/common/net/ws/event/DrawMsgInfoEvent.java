package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;

import java.util.List;

/**
 * Created by junhai on 14-8-13.
 */
public class DrawMsgInfoEvent {
    private DrawMsgInfo msgInfo;
    private List<DrawMsgInfo> msgInfos;

    public DrawMsgInfo getMsgInfo() {
        return msgInfo;
    }
    public List<DrawMsgInfo> getMsgInfos() {
        return msgInfos;
    }

    public DrawMsgInfoEvent(DrawMsgInfo msgInfo) {
        this.msgInfo = msgInfo;
    }

    public DrawMsgInfoEvent(List<DrawMsgInfo> msgInfos) {
        this.msgInfos = msgInfos;
    }
}

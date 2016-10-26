package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.SessionData.ForbidMessage;

/**
 * Created by junhai on 14-8-13.
 */
public class ForbinChatEvent {
    private ForbidMessage forbidMessage;

    public ForbinChatEvent(ForbidMessage forbidMessage) {
        this.forbidMessage = forbidMessage;
    }

    public ForbidMessage getForbidMessage() {
        return forbidMessage;
    }

    public void setForbidMessage(ForbidMessage forbidMessage) {
        this.forbidMessage = forbidMessage;
    }
}

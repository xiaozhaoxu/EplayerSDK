package com.ibrightech.eplayer.sdk.common.net.ws.event;

/**
 * Created by junhai on 14-8-13.
 */
public class VoteMsgInfoResEvent {
    private boolean success=false;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public VoteMsgInfoResEvent(boolean success) {
        this.success = success;
    }
}

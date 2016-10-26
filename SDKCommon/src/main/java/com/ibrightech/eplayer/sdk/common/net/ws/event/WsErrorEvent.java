package com.ibrightech.eplayer.sdk.common.net.ws.event;

/**
 *
 */
public class WsErrorEvent {
    private Exception err;

    public WsErrorEvent(Exception err) {
        this.err = err;
    }

    public Exception getErr() {
        return err;
    }
}

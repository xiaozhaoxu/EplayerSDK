package com.ibrightech.eplayer.sdk.common.net.ws.event;

/**
 *
 */
public class WsDisConnectedEvent {
    private int code;
    private String reason;

    public WsDisConnectedEvent(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}

package com.ibrightech.eplayer.sdk.common.net.ws.event;

/**
 * Created by junhai on 14-8-13.
 */
public class SocketMessageEvent {
    private Object data;


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    public SocketMessageEvent(Object data) {
        this.data = data;
    }
}

package com.ibrightech.eplayer.sdk.common.net.ws.event;

/**
 *
 */
public class WsOnBinaryEvent {
    private byte[] data;

    public WsOnBinaryEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}

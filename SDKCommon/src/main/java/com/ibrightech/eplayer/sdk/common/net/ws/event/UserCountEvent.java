package com.ibrightech.eplayer.sdk.common.net.ws.event;

/**
 * Created by junhai on 14-8-13.
 */
public class UserCountEvent {
    private  int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public UserCountEvent(int count) {
        this.count = count;
    }
}

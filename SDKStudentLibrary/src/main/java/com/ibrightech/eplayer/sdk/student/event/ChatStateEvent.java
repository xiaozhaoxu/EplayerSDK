package com.ibrightech.eplayer.sdk.student.event;

/**
 * Created by zhaoxu2014 on 16/9/2.
 */
public class ChatStateEvent {
    boolean isShow;

    public ChatStateEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }
}

package com.ibrightech.eplayer.sdk.common.net.ws.vo;

/**
 *
 */
public class Msg {

    public static final int SEND_STATUS_SENDING = 0;
    public static final int SEND_STATUS_ERROR = 1;
    public static final int SEND_STATUS_DONE = 2;

    public static final int MESSAGE_TYPE_CLIENT_EVENT = 0;
    public static final int MESSAGE_TYPE_USER_COUNT = 1;

    private int type;
    private Object event;
    private int userCount;

    private long st; //nearest server time

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public long getSt() {
        return st;
    }

    public void setSt(long st) {
        this.st = st;
    }
}

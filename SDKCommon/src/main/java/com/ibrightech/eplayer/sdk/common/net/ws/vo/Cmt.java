package com.ibrightech.eplayer.sdk.common.net.ws.vo;

public class Cmt {

    public static final int CMT_TYPE_PING = 1;
    public static final int CMT_TYPE_HANDSHAKE = 2;

    private String txt;
    private long ct;

    private int type;


    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

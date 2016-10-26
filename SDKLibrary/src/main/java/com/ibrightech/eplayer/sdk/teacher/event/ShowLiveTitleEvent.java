package com.ibrightech.eplayer.sdk.teacher.event;

/**
 * Created by zhaoxu2014 on 16/7/7.
 */
public class ShowLiveTitleEvent {
    boolean isShow=false;
    boolean isAnimation=true;

    public ShowLiveTitleEvent(boolean isShow,boolean isAnimation) {
        this.isShow = isShow;
        this.isAnimation=isAnimation;
    }

    public boolean isShow() {
        return isShow;
    }

    public boolean isAnimation() {
        return isAnimation;
    }
}

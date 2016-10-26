package com.ibrightech.eplayer.sdk.event;

public class DrawPadInfoLoadingEvent {

    //加载状态事件，控制loading是否显示和画笔是否显示

    public enum LoadingState {
        Loading_Start,
        Loading_End
    }


    public LoadingState loadingState;

    public DrawPadInfoLoadingEvent(LoadingState loadingState) {
        this.loadingState = loadingState;
    }
}

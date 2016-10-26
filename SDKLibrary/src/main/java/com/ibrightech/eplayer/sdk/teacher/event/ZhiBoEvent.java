package com.ibrightech.eplayer.sdk.teacher.event;

/**
 * Created by zhaoxu2014 on 16/7/7.
 */
public class ZhiBoEvent {
   public   boolean isRefresh;
   public int page;
    public int size;

    public ZhiBoEvent(boolean isRefresh, int page,int size) {
        this.isRefresh = isRefresh;
        this.page = page;
        this.size=size;
    }
}

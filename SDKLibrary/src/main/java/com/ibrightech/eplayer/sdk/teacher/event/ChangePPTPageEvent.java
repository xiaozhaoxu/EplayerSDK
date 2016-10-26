package com.ibrightech.eplayer.sdk.teacher.event;

public class ChangePPTPageEvent {
   public int page;
    public  int pagecount;

    public ChangePPTPageEvent(int page,int pagecount) {
        this.page = page;
        this.pagecount=pagecount;
    }
}

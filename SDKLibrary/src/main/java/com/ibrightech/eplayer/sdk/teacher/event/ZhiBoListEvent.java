package com.ibrightech.eplayer.sdk.teacher.event;

import com.ibrightech.eplayer.sdk.teacher.Entity.TeacherCourseEntity;

import java.util.List;

/**
 * Created by zhaoxu2014 on 16/7/7.
 */
public class ZhiBoListEvent {
   public boolean isRefresh;
   public int page;
   public List<TeacherCourseEntity> list;

    public ZhiBoListEvent(boolean isRefresh, int page, List<TeacherCourseEntity> list) {
        this.isRefresh = isRefresh;
        this.page = page;
        this.list = list;
    }
}

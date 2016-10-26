package com.ibrightech.eplayer.sdk.teacher.Entity;


import com.ibrightech.eplayer.sdk.common.util.JsonUtil;

import org.json.JSONObject;

/**
 * Created by yrhr on 2015/10/15.
 */


public class TeacherSectionEntity extends SDKBaseEntity {


    int zhibo_state;

    String data_type;
    String url;//": "http://cache.upuday.com/teacher/index.html?liveClassroomId=570e09a0deb7e3ce5302f2fa&customer=ynx&customerType=soooner&sp=0&p=258157b0194b2aa0084276e07479b5d1|1462945639401|1000184|13301245685|0",
    String is_free;//": "n",
    long course_id;//": 2302,
    String tvideo_url;//": null,
    String taudio_url;//": null


    public static TeacherSectionEntity fromJSON(JSONObject js) {
        if (null == js) {
            return null;
        }
        return JsonUtil.json2Bean(js.toString(), TeacherSectionEntity.class);

    }

    public int getZhibo_state() {
        return zhibo_state;
    }

    public void setZhibo_state(int zhibo_state) {
        this.zhibo_state = zhibo_state;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIs_free() {
        return is_free;
    }

    public void setIs_free(String is_free) {
        this.is_free = is_free;
    }

    public long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(long course_id) {
        this.course_id = course_id;
    }

    public String getTvideo_url() {
        return tvideo_url;
    }

    public void setTvideo_url(String tvideo_url) {
        this.tvideo_url = tvideo_url;
    }

    public String getTaudio_url() {
        return taudio_url;
    }

    public void setTaudio_url(String taudio_url) {
        this.taudio_url = taudio_url;
    }
}

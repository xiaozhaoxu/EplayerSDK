package com.ibrightech.eplayer.sdk.common.entity.playback;

import org.json.JSONObject;

/**
 * Created by zhaoxu2014 on 15-1-29.
 */
public class PlaySplice {
    public long start;
    public long end;

    public String url;
    public String path;
    public boolean needdownload=true;

    public static PlaySplice fromJson(JSONObject jsonObject) {
        PlaySplice bean =new PlaySplice();
        bean.start=jsonObject.optLong("start");
        bean.end=jsonObject.optLong("end");
        bean.needdownload=true;
        return bean;

    }
}

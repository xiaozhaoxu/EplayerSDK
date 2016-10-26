package com.ibrightech.eplayer.sdk.common.entity.playback;


import com.ibrightech.eplayer.sdk.common.util.DateUtil;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by junhai on 14-12-2.
 */
public class PackbackData {

    public String key;
    public String name;
    public long startTime;
    public long endTime;

    public static PackbackData fromJson(JSONObject jsonObject) {
        PackbackData msg = new PackbackData();

        msg.name = jsonObject.optString("subject");
        msg.key = jsonObject.optString("_id");

        String beginTime = jsonObject.optString("beginTime").replaceAll(":","").replaceAll(" ","").replaceAll("-","");
        String endTime = jsonObject.optString("endTime").replaceAll(":","").replaceAll(" ","").replaceAll("-","");


        Date startDate = DateUtil.getSimpleDate(PackbackData.fixedTime(beginTime));
        Date endDate = DateUtil.getSimpleDate(PackbackData.fixedTime(endTime));

        msg.startTime = startDate.getTime();
        msg.endTime = endDate.getTime();

        return msg;
    }

    public static String fixedTime(String time){
        //最多修正一下分和秒，小时部分不再修正
        time=time+"0000";

        return time.substring(0,14);
    }
}

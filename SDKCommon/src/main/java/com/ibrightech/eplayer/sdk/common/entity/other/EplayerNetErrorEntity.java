package com.ibrightech.eplayer.sdk.common.entity.other;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.JsonUtil;

import org.json.JSONObject;

/**
 * Created by zhaoxu2014 on 16/8/25.
 */
public class EplayerNetErrorEntity {
    String msg;//":"你已提交申请"
    String code;//":400,
    String data;
    String msg_code;//":"REPEATE"" +
    String errmsg;

    public static EplayerNetErrorEntity fromJSON(JSONObject js) {
        if (null == js) {
            return null;
        }
        EplayerNetErrorEntity bean = JsonUtil.json2Bean(js.toString(), EplayerNetErrorEntity.class);
        return bean;
    }


    public static String getMsg(EplayerNetErrorEntity entity) {

        String msg = "";
        if (CheckUtil.isEmpty(entity)) {
            msg = "";
            return msg;
        }

        if (!CheckUtil.isEmpty(entity.getMsg())) {
            msg = entity.getMsg();
            return msg;
        }

        return msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg_code() {
        return msg_code;
    }

    public void setMsg_code(String msg_code) {
        this.msg_code = msg_code;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}

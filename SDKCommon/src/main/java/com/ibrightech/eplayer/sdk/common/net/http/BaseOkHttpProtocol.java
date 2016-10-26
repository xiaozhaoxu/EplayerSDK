package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;

import okhttp3.Call;

/**
 * Created by zhaoxu2014 on 15-1-31.
 */
public abstract class BaseOkHttpProtocol {

    public static final int MSG_TOKEN_INVALID = 1001;

    public static final int DEFAULT_CODE = -1;
    public static final String PROTOCOL_KEY_CODE = "code";
    public static final String PROTOCOL_KEY_MSG = "msg";
    public static final String PROTOCOL_KEY_MSSAGE = "message";

    public static  int DEAFULTE_CODE=-1;


    public int errorCode = DEAFULTE_CODE;
    public String msg;
    CallBack myCallback;


    public int getCode() {
        return errorCode;
    }


    public String getMsg() {
        return msg;
    }



    public interface CallBack{
        public void onStart();//开始联网

        public void onSuccess(int errorCode, String msg, Object object) ;//联网成功

        public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity);//联网失败
        public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed);
    }

    protected abstract void execute();
    protected abstract Object handleJSON(String result) throws Exception;

    protected abstract void handleResult(String result);
    protected abstract boolean isGetMode();
}

package com.ibrightech.eplayer.sdk.common.net.http;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.ibrightech.eplayer.sdk.common.config.EplayerConfigBuilder;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.CodeMsgUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhaoxu2014 on 15-3-12.
 */
public abstract class AsyncBaseOkHttpProtocol extends BaseOkHttpProtocol {


    public String TAG="----Protocol----";
    boolean isCancel=false;
    Handler tokenHandler;
    UUID uuid=null;
    Context context;
    AbsCallback callback;

    public AbsCallback getCallback() {
        if(CheckUtil.isEmpty(callback)){
            callback=new MyStringCallback();
        }
        return callback;
    }

    public class MyStringCallback extends StringCallback
    {
        @Override
        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            if(null!=myCallback) {
                myCallback.onUpProgress(currentSize,  totalSize,  progress,  networkSpeed);
            }
            super.upProgress(currentSize, totalSize, progress, networkSpeed);
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            if(isCancel)return;
            JSONObject js=null;
            int statusCode=DEFAULT_CODE;
            String body="";
            try {
                body=response.body().string();
                js=new JSONObject(body);
                statusCode=js.optInt("code", DEFAULT_CODE);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            EplayerNetErrorEntity neterror= EplayerNetErrorEntity.fromJSON(js);

            boolean failResult = false;
            if(CheckUtil.isEmpty(body)){
                body="";
            }
            LogUtil.d(TAG, "联网失败，，访问地址" + getUrl() + "---->statusCode:" + statusCode+ " body:" +body);
            if (!CheckUtil.isEmpty(myCallback)) {
                failResult = myCallback.onFailure(call, neterror);
            }


            if (!failResult) {
                ToastUtil.showStringToast("联网失败");
            }
            cancel(false);
            super.onError(isFromCache, call, response, e);
        }

        @Override
        public void onResponse(boolean b, String resultData, Request request, @Nullable Response response) {
            if(isCancel)return;

            if (CheckUtil.isEmpty(resultData)) {
                if (null != myCallback) {
                    myCallback.onFailure(null, null);
                }
            }else{
                LogUtil.d(TAG, "获取数据成功，访问地址" + getUrl() + " ----> 返回结果" + response);
                handleResult(resultData);
            }
            cancel(false);
        }
    }
    public void execute( CallBack myCallback){
        execute(EplayerConfigBuilder.getInstance().getContext(),myCallback);

    }

    public void execute(Context context, CallBack myCallback){
        execute(context,myCallback,null);
    }

    public void execute(Context context, CallBack myCallback, Handler tokenHandler){
        this.context =context;
        this.myCallback=myCallback;
        isCancel=false;
        this.tokenHandler=tokenHandler;
        execute();
    }


    @Override
    protected void execute() {
        try {
            if (isCancel) return;
            if (!CheckUtil.isEmpty(myCallback)) {
                myCallback.onStart();
            }

            BaseRequest request= null;
            if (isGetMode()) {
                LogUtil.d(TAG, "get方式--》请求地址Url:" + getNetUrl());
                request = OkHttpUtils.get(getUrl()).params(getParams());

            } else {

                if(!CheckUtil.isEmpty(getJsonParams())){
                    LogUtil.d(TAG, "post方式--》请求地址url;" + getUrl() + "   -->JSONObject:" + getJsonParams().toString());

                    request=OkHttpUtils.post(getUrl()).postJson(getJsonParams().toString());
                }else{
                    LogUtil.d(TAG, "post方式--》请求地址url;" + getUrl() + "   -->getParams:" + getUrlParamsByMap(getParams()));
                    request = OkHttpUtils.post(getUrl()).params(getParams());
                }


            }
            uuid=UUID.randomUUID();
            request.tag(uuid);
            request.execute( getCallback());


        } catch (Exception e) {
            LogUtil.e(TAG, "Action failed: " + e.getMessage());
        }
    }

    private String getNetUrl() throws Exception {
        HttpParams obj=getParams();
        if(isGetMode()){
            return getUrl()+"?"+ getUrlParamsByMap( obj);
        }else {
            return getUrl();
        }

    }

    public static String getUrlParamsByMap(HttpParams map) throws Exception{
        if (CheckUtil.isEmpty(map)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Map<String, List<String>> urlParamsMap=map.urlParamsMap;
        for(Map.Entry<String,  List<String>> entry:urlParamsMap.entrySet()){
            sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue().get(0), "UTF-8"));
            sb.append("&");
        }

        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0,s.length()-1);
        }
        return s;
    }

    public void cancel() {
        cancel(true);
    }
    public void cancel(boolean showLog) {
        if(showLog){
            LogUtil.d(TAG, "取消 url: " + getUrl() + "的接口");
        }
        isCancel = true;
        if (null != uuid) {
            OkHttpUtils.getInstance().cancelTag(uuid);
        }
    }


    protected abstract String getUrl();



    /*
       url地址中?号后面跟着的参数
      */
    protected abstract HttpParams getParams() throws Exception;
    protected abstract JSONObject getJsonParams() throws Exception;


   @Override
    public void handleResult(String result ){
        Object be = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            Iterator it = jsonObject.keys();
            for (; it.hasNext(); ) {
                String name = (String) it.next();
                if (name.equalsIgnoreCase(PROTOCOL_KEY_CODE)) {
                    errorCode = jsonObject.optInt(PROTOCOL_KEY_CODE, DEAFULTE_CODE);
                }else if (name.equalsIgnoreCase(PROTOCOL_KEY_MSSAGE) ) {
                    msg = jsonObject.optString(PROTOCOL_KEY_MSSAGE);
                }else if (name.equalsIgnoreCase(PROTOCOL_KEY_MSG) ) {
                    msg = jsonObject.optString(PROTOCOL_KEY_MSG);
                }
            }
            be = handleJSON(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != myCallback) {
                myCallback.onSuccess(errorCode, getMsg(), be);

            }
            //todo 处理token过期的
            if (CodeMsgUtil.tokenInvalid(getCode())) {
                if (null != tokenHandler) {
                    tokenHandler.sendEmptyMessage(MSG_TOKEN_INVALID);
                }
            }
        }

    }


}

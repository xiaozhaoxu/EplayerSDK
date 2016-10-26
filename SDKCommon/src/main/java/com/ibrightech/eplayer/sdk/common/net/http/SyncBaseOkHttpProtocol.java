package com.ibrightech.eplayer.sdk.common.net.http;


import android.content.Context;

import com.ibrightech.eplayer.sdk.common.config.EplayerConfigBuilder;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Response;

/**
 * Created by zhaoxu2014 on 15-3-12.
 */
public abstract class SyncBaseOkHttpProtocol extends BaseOkHttpProtocol {


    public String TAG="----Protocol----";
    boolean isCancel=false;
    UUID uuid=null;
    Context context;
    BaseRequest request= null;

    public void execute( CallBack myCallback){
        execute(EplayerConfigBuilder.getInstance().getContext(),myCallback);
    }

    public void execute(Context context, CallBack myCallback){
        this.context =context;
        this.myCallback=myCallback;
        isCancel=false;
        execute();
    }
    @Override
    protected void execute() {
        try {
            if (isCancel) return;
            if (!CheckUtil.isEmpty(myCallback)) {
                myCallback.onStart();
            }



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
            Response response= request.execute();

            if(response.isSuccessful()){
                String resultData=response.body().string();
                if (StringUtils.isValid(resultData)) {
                    LogUtil.d(TAG, "获取数据成功，访问地址" + getUrl() + " ----> 返回结果" + resultData);
                    handleResult(resultData);

                } else {
                    if(null!=myCallback) {
                        myCallback.onFailure(null,null);
                    }

                }
            }else{
                if(null!=myCallback) {

                    myCallback.onFailure(null,null);
                }
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "Action failed: " + e.getMessage());
        }
    }

    private String getNetUrl() throws Exception {
        HttpParams obj=getParams();
        if(isGetMode()){
            return getUrl()+"?"+ getUrlParamsByMap(  obj);
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

    public void cancel(){
        LogUtil.d(TAG, "取消 url: "+getUrl()+"的接口");
        isCancel=true;
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
                if (name.equals(PROTOCOL_KEY_CODE)) {
                    errorCode = jsonObject.optInt(PROTOCOL_KEY_CODE, DEAFULTE_CODE);
                }else if (name.equals(PROTOCOL_KEY_MSSAGE) ) {
                    msg = jsonObject.optString(PROTOCOL_KEY_MSSAGE);
                }
            }
            be = handleJSON(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != myCallback) {
                myCallback.onSuccess(errorCode, getMsg(), be);
            }
        }

    }


}

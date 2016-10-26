package com.ibrightech.eplayer.sdk.teacher.net;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.ibrightech.eplayer.sdk.teacher.activity.SDKBaseActivity;
import com.ibrightech.eplayer.sdk.teacher.cache.CacheUtil;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.CodeMsgUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.util.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
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
 * Created by zhaoxu2014 on 15/11/17.
 */
public abstract class HttpJsonBaseProtocol extends HttpBaseProtocol {
    public static final String TAG = "---protocol---";
    public static final String MEDIA_TYPE = "application/json; charset=utf-8";
    Handler tokenHandler;

    UUID uuid=null;
    public boolean isCancel = false;
    public boolean useBufferData = true;

    public class MyStringCallback extends StringCallback
    {
        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            if (isCancel) return;
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

            EplayerNetErrorEntity neterror= EplayerNetErrorEntity.fromJSON( js);
            if (CodeMsgUtil.tokenInvalid(statusCode)) {
                if (null != tokenHandler) {
                    tokenHandler.sendEmptyMessage(SDKBaseActivity.MSG_TOKEN_INVALID);
                }
            }


            boolean failResult = false;
            if(CheckUtil.isEmpty(body)){
                body="";
            }
            LogUtil.d(TAG, "联网失败，，访问地址" + getUrl() + "---->statusCode:" + statusCode+ " body:" +body);
            if (!CheckUtil.isEmpty(myCallback)) {
                failResult = myCallback.onFailure(call, neterror);
            }


            if (!failResult) {
                ToastUtil.showStringToast("获取数据失败");
            }
            cancel(false);
            super.onError(isFromCache, call, response, e);
        }

        @Override
        public void onResponse(boolean b, String response, Request request, @Nullable Response rs) {
            if (isCancel) return;
            if (CheckUtil.isEmpty(response)) {
                if (null != myCallback) {
                    myCallback.onFailure(null, null);
                }
            }

            if (StringUtils.isValid(response)) {
                LogUtil.d(TAG, "获取数据成功，访问地址" + getUrl() + " ----> 返回结果" + response);
                //缓冲本次数据到本地
                if (useBufferData) {
                    CacheUtil.saveString(getUrl(), response);
                }

            }
            handleResult(response);
            cancel(false);
        }
    }



    public void handleResult(Object response) {
        Object be = null;
        try {
            String result = response.toString();
            JSONObject jsonObject = new JSONObject(result);
            Iterator it = jsonObject.keys();
            for (; it.hasNext(); ) {
                String name = (String) it.next();
                if (name.equals("code")) {
                    code = jsonObject.optInt("code", DEFAULT_CODE);
                } else if (name.equals("msg") || name.equals("message")) {
                    msg = jsonObject.optString(name);
                }

            }
            be = handleJSON(jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != myCallback) {
                myCallback.onSuccess(isSuccess(), getMsg(), be);

                //todo 处理token过期的
                if (CodeMsgUtil.tokenInvalid(getCode())) {
                    if (null != tokenHandler) {
                        tokenHandler.sendEmptyMessage(SDKBaseActivity.MSG_TOKEN_INVALID);
                    }
                }
            }

        }

    }

    public boolean handlerBufferData(String result) {
        if (CheckUtil.isEmpty(result)) {
            if (null != myCallback) {
                myCallback.onUseBufferDataAndCancelNetwork(null);
                return false;
            }
        }


        Object be = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            Iterator it = jsonObject.keys();
            for (; it.hasNext(); ) {
                String name = (String) it.next();
                if (name.equals("code")) {
                    code = jsonObject.optInt("code", DEFAULT_CODE);
                } else if (name.equals("msg") || name.equals("message")) {
                    msg = jsonObject.optString(name);
                }
            }
            be = handleJSON(jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != myCallback) {
                //当缓冲的数据不对，返回null
                if (isSuccess()) {
                    return myCallback.onUseBufferDataAndCancelNetwork(be);
                } else {
                    return myCallback.onUseBufferDataAndCancelNetwork(null);
                }
            } else {
                return false;
            }


        }
    }

    public void execute(Context context, CallBack myCallback) {
        execute(context, false, myCallback);
    }

    public void execute(Context context, boolean useBufferData, CallBack myCallback) {
        execute(context, null, useBufferData, myCallback);
    }

    public void execute(Context context, Handler tokenHandler, CallBack myCallback) {
        execute(context, tokenHandler, false, myCallback);
    }

    public void execute(Context context, Handler tokenHandler, boolean useBufferData, CallBack myCallback) {
        this.tokenHandler = tokenHandler;
        this.context = context;
        this.useBufferData = useBufferData;
        this.myCallback = myCallback;
        isCancel = false;
        execute();
    }


    @Override
    protected void execute() {

        try {
            if (isCancel) return;
            if (!CheckUtil.isEmpty(myCallback)) {
                myCallback.onStart();
            }

            //先处理缓冲数据
            if (useBufferData) {
                String bfData = CacheUtil.getStringByKey(getUrl());
                boolean useBufferData = handlerBufferData(bfData);
                if (useBufferData) {
                    return;
                }
            }
            BaseRequest request=null;
            if (isGetMode()) {
                LogUtil.d(TAG, "get方式--》请求地址Url:" + getNetUrl());

                request= OkHttpUtils.get(getUrl())
                        .params((HttpParams) getParams());

            }else {
                Object object=getParams();
                if(!CheckUtil.isEmpty(object)&&object instanceof JSONObject){
                    LogUtil.d(TAG, "post方式--》请求地址url;" + getUrl() + "   -->JSONObject:" + object.toString());
                    request=OkHttpUtils.post(getUrl()).postJson(object.toString());
                }else{

                    LogUtil.d(TAG, "post方式--》请求地址url;" + getUrl() + "   -->getParams:" + getUrlParamsByMap((HttpParams) object));

                    request=OkHttpUtils.post(getUrl()).params((HttpParams) object);
                }

            }
            uuid=UUID.randomUUID();
            request.headers(getHeaderMap()).tag(uuid);
            request.execute(new MyStringCallback());



        } catch (Exception e) {
            LogUtil.e(TAG, "Action failed: " + e.getMessage());
            if (!CheckUtil.isEmpty(myCallback)) {
                myCallback.onFailure(null , null);
            }
        }
    }

    private String getNetUrl() throws Exception {
        Object obj=getParams();
        if(isGetMode()&&obj instanceof  HttpParams){
            return getUrl()+"?"+ getUrlParamsByMap(  (HttpParams)obj);
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
}

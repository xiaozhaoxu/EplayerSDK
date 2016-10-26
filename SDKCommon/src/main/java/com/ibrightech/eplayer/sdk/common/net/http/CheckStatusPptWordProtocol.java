package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.CheckStatusEntity;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONObject;

/**
 * 上传ppt.word 资源后检测状态
 */
public class CheckStatusPptWordProtocol extends AsyncBaseOkHttpProtocol {


    String  statusPath;

    public CheckStatusPptWordProtocol(String statusPath) {
        this.statusPath = statusPath;
    }

    @Override
    protected String getUrl() {

        return EplayerSetting.getInstance().uploadhost + "ppt/Status/"+statusPath+"?stamp="+ System.currentTimeMillis();
    }

    @Override
    protected HttpParams getParams() throws Exception {

        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        return null;
    }

    @Override
    protected Object handleJSON(String objResult) throws Exception {

        return null;
    }

    @Override
    protected boolean isGetMode() {
        return true;
    }



    @Override
    public void handleResult(String result) {
        String[] strs= result.split(",");

        CheckStatusEntity checkStatusEntity=null;
        if(strs.length==2){
            String strtemp="";
            strtemp=strs[1];
            checkStatusEntity= CheckStatusEntity.fromString(strtemp,strs[0]);
        }

        if (null != myCallback) {
            myCallback.onSuccess(0, getMsg(), checkStatusEntity);
        }

    }

}

package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.UploadEntity;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * 上传ppt.word 资源
 */
public class UploadPptWordProtocol extends AsyncBaseOkHttpProtocol {


    File imagFile;

    public UploadPptWordProtocol(File imagFile) {
        this.imagFile = imagFile;
    }


    @Override
    protected String getUrl() {

        return EplayerSetting.uploadhost + "ppt/getFile.ashx";
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
        return false;
    }

    @Override
    protected void execute() {
        try {
            if (isCancel) return;
            if (!CheckUtil.isEmpty(myCallback)) {
                myCallback.onStart();
            }

            BaseRequest request= null;
            ArrayList<String>urllist=new ArrayList<String>();
            urllist.add( imagFile.getName());

            ArrayList<File> files=new ArrayList<File>();
            files.add(imagFile);

            request = OkHttpUtils.post(getUrl())
                       .addUrlParams("Filename",urllist)
                       .addFileParams("Filedata",files);



            request.execute(new MyStringCallback());

        } catch (Exception e) {
            LogUtil.e(TAG, "Action failed: " + e.getMessage());
            if (!CheckUtil.isEmpty(myCallback)) {
                myCallback.onFailure(null,null);
            }
        }
    }


    @Override
    public void handleResult(String result) {
        UploadEntity uploadEntity= UploadEntity.fromString(result);

        if (null != myCallback) {
            myCallback.onSuccess(0, getMsg(), uploadEntity);
        }

    }

}

package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONObject;

/**
 * 上传ppt.word 资源
 */
public class SaveAssetProtocol extends AsyncBaseOkHttpProtocol {

    String fileName;
    String liveRoomId;
    String name;
    String type;
    int pptPageCount = 0;

    public SaveAssetProtocol(String fileName, String liveRoomId, String name, String type) {
        this.fileName = fileName;
        this.liveRoomId = liveRoomId;
        this.name = name;
        this.type = type;
    }


    @Override
    protected String getUrl() {

        return  EplayerSetting.getInstance().host + "asset/saveAssetInfo";
    }

    @Override
    protected HttpParams getParams() throws Exception {

        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName",fileName);
        jsonObject.put("liveRoomId",liveRoomId);
        jsonObject.put("name",name);
        jsonObject.put("type",type);
        jsonObject.put("pptPageCount",pptPageCount);
        return jsonObject;
    }

    @Override
    protected Object handleJSON(String objResult) throws Exception {
        Asset asset=null;
        try {
            JSONObject jsonObject=new JSONObject(objResult);
            JSONObject js= jsonObject.optJSONObject("asset");
            asset=Asset.fromJson(js);

        } catch (Exception e) {
            e.printStackTrace();

        }finally {
            return asset;

        }
    }

    @Override
    protected boolean isGetMode() {
        return false;
    }





}

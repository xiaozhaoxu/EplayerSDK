package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONObject;

/**
 * Created by daocren on 14-1-6.
 */
public class DeleteCourseWareProtocol extends AsyncBaseOkHttpProtocol {

    String liveRoomId;
    int assetId;

    public DeleteCourseWareProtocol(String liveRoomId, int assetId) {
        this.liveRoomId = liveRoomId;
        this.assetId = assetId;
    }

    @Override
    protected String getUrl() {
        return EplayerSetting.getInstance().host+"asset/deleteAsset";
    }

    @Override
    protected HttpParams getParams() throws Exception {
        return null;
    }


    @Override
    protected JSONObject getJsonParams() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("liveRoomId", liveRoomId);
        jsonObject.put("assetId", assetId + "");
        return jsonObject;
    }


    @Override
    protected Object handleJSON(String result) throws Exception {
        return null;
    }

    @Override
    protected boolean isGetMode() {
        return false;
    }


}

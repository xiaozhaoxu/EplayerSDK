package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daocren on 14-1-6.
 */
public class GetAssetListProtocol extends SyncBaseOkHttpProtocol {

    String liveRoomId;
    String type;
    Map<String, Asset> assetMap = new HashMap<String, Asset>();

    public GetAssetListProtocol(String liveRoomId, String type) {
        this.liveRoomId = liveRoomId;
        this.type = type;
    }
    public Map<String, Asset> getAssetMap() {
        return assetMap;
    }

    @Override
    protected String getUrl() {
        return EplayerSetting.getInstance().host + "asset/getAssetList";
    }

    @Override
    protected HttpParams getParams() throws Exception {

        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("liveRoomId",liveRoomId);
        jsonObject.put("type",type);
        jsonObject.put("filterDel", "false");

        return jsonObject;
    }

    @Override
    protected Object handleJSON(String result) throws Exception {
        try {
            JSONObject jsonObject=new JSONObject(result);
            JSONArray dataJsonArray = jsonObject.optJSONArray("data");
            for (int i = 0; i < dataJsonArray.length(); i++) {

                JSONObject assetJSONObject = (JSONObject) dataJsonArray.get(i);
                Asset asset = Asset.fromJson(assetJSONObject);

                assetMap.put(asset.id, asset);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return assetMap;
    }

    @Override
    protected boolean isGetMode() {
        return false;
    }
}

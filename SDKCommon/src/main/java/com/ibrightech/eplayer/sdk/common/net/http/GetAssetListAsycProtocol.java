package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.Asset;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by daocren on 14-1-6.
 */
public class GetAssetListAsycProtocol extends AsyncBaseOkHttpProtocol {

    String liveRoomId;
    String type;
    boolean getAll=false;
    TreeMap<String, Asset> assetMap = new TreeMap<String, Asset>(new Comparator<String>() {
        @Override
        public int compare(String s, String s2) {
            try{
                int intger_s=Integer.valueOf(s);
                int intger_s2=Integer.valueOf(s2);
                if(intger_s<intger_s2){
                    return -1;
                }else if(intger_s==intger_s2){
                    return 0;
                }else{
                    return 1;
                }
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }

        }
    });

    public GetAssetListAsycProtocol(String liveRoomId, String type) {
        this.liveRoomId = liveRoomId;
        this.type = type;
    }

    public GetAssetListAsycProtocol(String liveRoomId, String type, boolean getAll) {
        this.liveRoomId = liveRoomId;
        this.type = type;
        this.getAll = getAll;
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
        if(getAll) {
            jsonObject.put("filterDel", "false");
        }
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

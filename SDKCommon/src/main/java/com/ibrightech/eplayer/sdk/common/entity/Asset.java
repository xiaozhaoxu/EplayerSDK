package com.ibrightech.eplayer.sdk.common.entity;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by daocren on 14-1-7.
 */
public class Asset {

//    "name": "Caffeine+Nicotine.pptx",
//            "fileName": "http://cache2.ovp.tv380.com/Courseware/b6e58f7d72ca4b128d11ddc6ed34874e/Config.xml",
//            "liveRoomId": "52c62b18a8fc0f6c13000002",
//            "pptPageCount": 1,
//            "type": "ppt",
//            "createTime": "2014-01-06 19:13:39",
//            "state": 1,
//            "_id": 202


    public String id;
    public String name;
    public String fileName;
    public String liveRoomId;
    public String pptPageCount;
    public String type;
    public String createTime;
    public String state;
    public String data;


    public static Asset fromJson(JSONObject jsonObject) {

            Asset asset = new Asset();
            asset.id = jsonObject.optString("_id");
            asset.name = jsonObject.optString("name");
            asset.fileName = jsonObject.optString("fileName");

            if (asset.fileName != null && EplayerSetting.getInstance().asset_url != null && EplayerSetting.getInstance().asset_url.length() > 0) {
                int index = asset.fileName.indexOf("/Courseware");
                if (index != -1) {
                    String uri = asset.fileName.substring(index);
                    asset.fileName = EplayerSetting.getInstance().asset_url + uri;
                }
            }

            asset.liveRoomId = jsonObject.optString("liveRoomId");
            asset.pptPageCount = jsonObject.optString("pptPageCount");
            asset.type = jsonObject.optString("type");
            asset.createTime = jsonObject.optString("createTime");
            asset.state = jsonObject.optString("state");
            asset.data=jsonObject.toString();
            return asset;

    }

    public static Asset fromJson(JSONArray jsonArray) {
        try {
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            Asset asset = new Asset();
            asset.id = jsonObject.optString("_id");
            asset.name = jsonObject.optString("name");
            asset.fileName = jsonObject.optString("fileName");

            if (asset.fileName != null && EplayerSetting.getInstance().asset_url != null && EplayerSetting.getInstance().asset_url.length() > 0) {
                int index = asset.fileName.indexOf("/Courseware");
                if (index != -1) {
                    String uri = asset.fileName.substring(index);
                    asset.fileName = EplayerSetting.getInstance().asset_url + uri;
                }
            }

            asset.liveRoomId = jsonObject.optString("liveRoomId");
            asset.pptPageCount = jsonObject.optString("pptPageCount");
            asset.type = jsonObject.optString("type");
            asset.createTime = jsonObject.optString("createTime");
            asset.state = jsonObject.optString("state");
            return asset;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

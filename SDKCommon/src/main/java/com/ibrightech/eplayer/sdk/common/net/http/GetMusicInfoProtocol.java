package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.MusicInfo;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONObject;

/**
 * Created by SlotMonkey on 14-1-9.
 */
public class GetMusicInfoProtocol extends AsyncBaseOkHttpProtocol {

    private String liveRoomId;
    private String musicType;

    public GetMusicInfoProtocol(String liveRoomId, String musicType) {
        this.liveRoomId = liveRoomId;
        this.musicType = musicType;
    }


    @Override
    protected String getUrl() {
        return EplayerSetting.getInstance().host + "getMusicInfo";
    }

    @Override
    protected HttpParams getParams() throws Exception {
        HttpParams map=new HttpParams();
        map.put("liveRoomId",liveRoomId+"");
        map.put("musicType",musicType);
        return map;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        return null;
    }

    @Override
    protected Object handleJSON(String result) throws Exception {
        MusicInfo musicInfo=null;
        try {
            JSONObject jsonObject=new JSONObject(result);
             musicInfo = MusicInfo.fromJson(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return musicInfo;

        }
    }



    @Override
    protected boolean isGetMode() {
        return false;
    }
}

package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.playback.PackbackData;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junhai on 14-11-27.
 */
public class PlaybackListProtocol extends SyncBaseOkHttpProtocol {

    private static final String LIVE_CLASSROOM_ID = "liveId";


    private static final String DATA = "data";

    private String liveClassroomId;

    public List<PackbackData> datas;

    public PlaybackListProtocol(String liveClassroomId) {
        this.liveClassroomId = liveClassroomId;
        datas = new ArrayList<PackbackData>();
    }


    @Override
    protected String getUrl() {
        String url = EplayerSetting.getInstance().host + "playback/list";
        return url;
    }

    @Override
    protected HttpParams getParams() throws Exception {
        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        JSONObject js=new JSONObject();
        js.put(LIVE_CLASSROOM_ID, this.liveClassroomId);
        return js;
    }

    @Override
    protected Object handleJSON(String backresult) throws Exception {
        LogUtil.d("PlaybackInfoProtocol",backresult);
        try {
            JSONObject result=new JSONObject(backresult);
            JSONArray jsonArray = result.getJSONArray(DATA);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PackbackData data = PackbackData.fromJson(jsonObject);
                    datas.add(data);
                }catch (Exception e){

                }
            }


        } catch (Exception e) {
            LogUtil.e("Parse LiveRoom data Exception! ", e.getMessage());
        }

        return datas;

    }

    @Override
    protected boolean isGetMode() {
        return false;
    }
}
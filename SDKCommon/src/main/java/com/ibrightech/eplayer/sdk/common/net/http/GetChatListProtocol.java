package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.LiveInfoChar;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daocren on 14-1-6.
 */
public class GetChatListProtocol extends SyncBaseOkHttpProtocol {

    public enum  TIMETYPE{
        TimeTypeNone("none"),TimeTypeNew("new"),TimeTypeOld("old");

        private String _value;

        private TIMETYPE(String value) {
            _value = value;
        }

        public String value() {
            return _value;
        }
    }
    TIMETYPE time=TIMETYPE.TimeTypeNone;

    List<LiveInfoChar> list=new ArrayList<LiveInfoChar>();


    public GetChatListProtocol(String liveRoomId, int pageSize, String beginId, TIMETYPE time) {
        this.liveRoomId = liveRoomId;
        this.pageSize = pageSize;
        this.beginId = beginId;
        this.time = time;
    }

    String liveRoomId;
    int  pageSize=5;
    String beginId;

    @Override
    protected String getUrl() {
        return EplayerSetting.getInstance().host + "chat/list";
    }

    @Override
    protected HttpParams getParams() throws Exception {
        HttpParams map=new HttpParams();
        map.put("liveId",liveRoomId);
        map.put("pageSize",pageSize+"");
        if(StringUtils.isValid(beginId)&&time!=TIMETYPE.TimeTypeNone){
            map.put("beginId",beginId+"");
            map.put("time",time.value()+"");
        }

        return map;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        return null;
    }

    @Override
    protected Object handleJSON(String backresult) throws Exception {
        LogUtil.d("GetLiveRoomInfoProtocol", backresult);

        try {
            JSONObject result=new JSONObject(backresult);
            //解析Json数据
            if (result.isNull("data")) {
                return null;
            }

            JSONArray jsonArray=result.getJSONArray("data");

            if(null!=jsonArray&&jsonArray.length()>0){
                for(int i=0 ; i<jsonArray.length();i++){
                    JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                    LiveInfoChar liveInfo= LiveInfoChar.fromJson(jsonObject);
                    list.add(liveInfo);
                }
            }

        } catch (Exception e) {
            LogUtil.e("Parse LiveRoom data Exception! ",e.getMessage());
        }
        return list;

    }

    @Override
    protected boolean isGetMode() {
        return true;
    }




}

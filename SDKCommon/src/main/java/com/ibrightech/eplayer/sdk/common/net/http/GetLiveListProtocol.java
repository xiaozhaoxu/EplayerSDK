package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.LiveInfo;
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
public class GetLiveListProtocol extends AsyncBaseOkHttpProtocol {

    /*
          **参数**

                  - date {String} [optional] 时间，可选值`today(今日直播)`、`before(历史直播)`、`future(将要直播)`,  不传则返回所有
          - isFree {Number} [optional] 是否免费，可选值` 1(免费)`、`2(不免费)`，不传则返回所有
          - pageSize {Number}  [optional] 分页大小，默认为`6`
                  - pageNum {Number} [optional] 当前页码，默认为`1`
                  */
    public enum  DateEnum{
        DateEnumALL("all"),DATEENUMTODAY("today"),DATEENUMBEFORE("before"),DATEENUMFUTURE("future");
        private String _value;
        private DateEnum(String vlaue){
            _value=vlaue;
        }
        public String value(){
            return _value;
        }

    }


    public DateEnum dateEnum=DateEnum.DateEnumALL;
    public String isFree;
    public int pageSize = 6;
    public int pageNum = 1;

    List<LiveInfo> list=new ArrayList<LiveInfo>();

    @Override
    protected String getUrl() {
        return EplayerSetting.getInstance().host + "live/list";
    }

    @Override
    protected HttpParams getParams() throws Exception {
        HttpParams data=new HttpParams();
        if (dateEnum != DateEnum.DateEnumALL) {
            data.put("date", dateEnum.value());
        }
        if (StringUtils.isValid(isFree)) {
            data.put("isFree", isFree);
        }
        data.put("pageSize", ""+pageSize);
        data.put("pageNum", ""+pageNum);
        return data;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        return null;
    }

    @Override
    protected Object handleJSON(String objResult) throws Exception {
        try {
            JSONObject result=new JSONObject(objResult);
            //解析Json数据
            if (result.isNull("page")) {
                return null;
            }

            JSONObject js= (JSONObject) result.get("page");
            JSONArray jsonArray=js.getJSONArray("data");

            if(null!=jsonArray&&jsonArray.length()>0){
                for(int i=0 ; i<jsonArray.length();i++){
                    JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                    LiveInfo liveInfo= LiveInfo.fromJson(jsonObject);
                    list.add(liveInfo);
                }
            }

        } catch (Exception e) {
            LogUtil.e("Parse LiveRoom data Exception! ", e.getMessage());
        }finally {
            return list;
        }

    }

    @Override
    protected boolean isGetMode() {
        return true;
    }





}

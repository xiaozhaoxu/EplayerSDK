package com.ibrightech.eplayer.sdk.common.net.http;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONObject;

/**
 * Created by daocren on 14-1-6.
 */
public class GetWayProtocol extends SyncBaseOkHttpProtocol {

    private static final String RAND = "rand";
    private static final String RESP_TYPE = "respType";

    private static final String SHORT_SERVER= "sserver";
    private static final String SHORT_PORT= "sport";

    private static final String LONG_SERVER= "lserver";
    private static final String LONG_PORT= "lport";

    private static final String PLAY_URL= "playRtmpUrl";
    private static final String ASSET_URL= "assetUrl";

    private static final String CONFIG= "config";


    @Override
    protected String getUrl() {
        return "http://59.151.15.96/ipd/getLetvLiveConfig";
    }

    @Override
    protected HttpParams getParams() throws Exception {
        HttpParams map=new HttpParams();
        map.put("respType","1");
        map.put("rand",""+System.currentTimeMillis());
        return map;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        return null;
    }

    @Override
    protected Object handleJSON(String backresult) throws Exception {
        LogUtil.d("GetWayProtocol", backresult);


        try {
            JSONObject result=new JSONObject(backresult);
            //解析Json数据
            JSONObject config = result.getJSONObject(CONFIG);


            String sserver =  config.optString(SHORT_SERVER);
            String sport =  config.optString(SHORT_PORT);

            String lserver =  config.optString(LONG_SERVER);
            String lport =  config.optString(LONG_PORT);

            String playRtmpUrl =  config.optString(PLAY_URL);
            String asset_url =  config.optString(ASSET_URL);

            if(sserver!=null&&sport!=null){
                EplayerSetting.getInstance().host="http://"+sserver+":"+sport+"/";
            }

            if(lserver!=null&&lport!=null){
                EplayerSetting.getInstance().socket_host="http://"+lserver+":"+lport+"/";
            }

            if(playRtmpUrl!=null){
                EplayerSetting.getInstance().asset_url=asset_url;
                if(!playRtmpUrl.endsWith("/")){
                    playRtmpUrl+="/";
                }
                EplayerSetting.getInstance().playRtmpUrl=playRtmpUrl;
            }


        } catch (Exception e) {
            LogUtil.e("Parse LiveRoom data Exception! ", e.getMessage());
        }finally {
            return null;
        }
    }

    @Override
    protected boolean isGetMode() {
        return true;
    }
}

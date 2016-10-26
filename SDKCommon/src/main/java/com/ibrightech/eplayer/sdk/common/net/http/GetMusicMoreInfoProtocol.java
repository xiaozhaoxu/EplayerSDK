package com.ibrightech.eplayer.sdk.common.net.http;

import android.util.Xml;

import com.ibrightech.eplayer.sdk.common.entity.SliderPPT;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;
import com.lzy.okhttputils.model.HttpParams;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SlotMonkey on 14-1-9.
 */
public class GetMusicMoreInfoProtocol extends AsyncBaseOkHttpProtocol {


    private String dataurl;
    public GetMusicMoreInfoProtocol(String dataurl) {
        this.dataurl = dataurl;
    }


    @Override
    protected String getUrl() {
        return dataurl;
    }

    @Override
    protected HttpParams getParams() throws Exception {

        return null;
    }

    @Override
    protected JSONObject getJsonParams() throws Exception {
        return null;
    }


    @Override
    public void handleResult(String result) {
        Object be = null;
        try {
            be = handleJSON(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != myCallback) {
                myCallback.onSuccess(errorCode, getMsg(), be);
            }
        }
    }

    @Override
    protected Object handleJSON(String result) throws Exception {

        List<SliderPPT> musicList = new ArrayList<SliderPPT>();

        try {

            InputStream is = StringTOInputStream(result);
            musicList = parseXML(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            return musicList;
        }


    }

    /**
     * 将String转换成InputStream
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream StringTOInputStream(String in) throws Exception{

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("UTF-8"));
        return is;
    }
    private List<SliderPPT> parseXML(InputStream is) throws Exception {


        SliderPPT sliderPPT = null;
        List<SliderPPT> sliderPPTList = null;
        Map<String, String> sliderPPTMap = new HashMap<String, String>();

        XmlPullParser parser = Xml.newPullParser();    //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");                //设置输入流 并指明编码方式

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    LogUtil.d("START_DOCUMENT");
                    sliderPPTList = new ArrayList<SliderPPT>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("item")) {
                        sliderPPT = new SliderPPT();
                        sliderPPT.page = NumberUtil.parseInt(parser.getAttributeValue(0), -1);
                        sliderPPT.artwork = parser.getAttributeValue(1);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("item")) {
                        sliderPPTList.add(sliderPPT);
                        sliderPPT = null;
                    }
                    break;
            }
            eventType = parser.next();
        }

        for (SliderPPT ppt : sliderPPTList) {
            LogUtil.d("Music name:" + ppt.page + "--url:" + ppt.artwork);
            sliderPPTMap.put(String.valueOf(ppt.page), ppt.artwork);
        }


        return sliderPPTList;
    }


    @Override
    protected boolean isGetMode() {
        return true;
    }
}

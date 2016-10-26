package com.ibrightech.eplayer.sdk.common.entity.playback;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by junhai on 14-11-20.
 */
public class PlayList  implements Comparable<PlayList>{
    public long playTime;
    public long startTime;
    public long endTime;

    protected String domain;
    public EplayerLiveRoomStreamType type;
    protected String filePath;
    protected String suffix;
    public String endfiletime;


    public static PlayList fromJson(JSONObject jsonObject, String domain) {
        PlayList msg = new PlayList();

        if ("upudays.soooner.com".equals(domain)) {
            domain = "upuday.soooner.com";
        }
        msg.domain = domain;


        String beginFile = jsonObject.optString("beginFile");
        String endFile = jsonObject.optString("endFile");

        String startTimeStr = PlayList.cutTime(beginFile);

        msg.suffix = PlayList.cutSuffix(beginFile);
        msg.filePath = endFile;

        String endTimeStr = PlayList.cutTime(endFile);
        msg.endfiletime =endTimeStr;

        long startTime = NumberUtil.parseLong(startTimeStr, 0);
        long endTime = NumberUtil.parseLong(endTimeStr, 0);

        if (startTime == 0 || endTime == 0)
            return null;


        Date startDate = DateUtil.getSimpleDate(startTimeStr);
        Date endDate = DateUtil.getSimpleDate(endTimeStr);

        msg.startTime = startDate.getTime();

        msg.endTime = endDate.getTime();

        //播放结束时间矫正
        msg.playTime = jsonObject.optInt("playTime")*1000;
        if(msg.playTime==0) {
            msg.playTime = endTime - startTime;
        }else{
            msg.endTime = msg.startTime + msg.playTime;
        }


        return msg;
    }

    private static String cutTime(String string) {
        int startIndex = string.indexOf("-");
        int endIndex = string.indexOf(".flv");

        startIndex = startIndex + 1;

        return string.substring(startIndex, endIndex);
    }

    private static String cutSuffix(String string) {
        int startIndex = string.indexOf("-");

        return string.substring(0, startIndex);
    }

    public String playUrl() {
        return "http://" + this.domain + "/" + this.suffix + "/" + this.filePath;
    }

    public String playUrl(long start, long end) {
        return "http://" + this.domain + "/" + start + "-" + end + "/" + this.suffix + "/" + this.filePath;
    }

    public String getDownSpliceUrl(){
        return suffix+"-"+ endfiletime;

    }


    public String getDownLocationParentPath(){
        String documentsDirectory = StorageUtil.getCacheDir() ;
        return documentsDirectory+getDownSpliceUrl() +"/";
    }
    public String getDownLocationFileName(){
       return "index.json";
    }

    //下载的片段文件本地保存的地方
    public String getDownLocationPath(){
        return getDownLocationParentPath()+getDownLocationFileName();
    }

    public boolean isexists(){
      return   FileUtils.isExist(getDownLocationPath());
    }

    public String getSuffix() {
        return suffix;
    }

    public String getEndfiletime() {
        return endfiletime;
    }

    @Override
    public int compareTo(PlayList playList) {
        Long st=this.startTime;
        Long st2=playList.startTime;
        return st.compareTo(st2);
    }


}

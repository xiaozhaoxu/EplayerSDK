package com.ibrightech.eplayer.sdk.common.entity.session;

import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackDriver;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackSegment;
import com.ibrightech.eplayer.sdk.common.util.CollectionsUtil;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junhai on 14-11-20.
 */
public class EPlaybackSessionInfo extends EplayerSessionInfo implements ISessionInfo{

    public long playbackBeginTime;  //回看开始时间，以秒为单位
    public long playbackEndTime;  //回看结束时间，以秒为单位
    public long totalPlaybackTime;  //回看总时间，以秒为单位

    public List<PlaybackSegment> segmentArrays;    //片段数据，整个引擎依赖此数据才能运行


    public Map<String, DrawPadInfo> pageInfoValues;       //翻页数据
    public Map<String, String> pageTimeValues;             //每页初始时间


    public Map<String, PlaybackMsgInfo> drawInfoTimeValues;             //每页画笔信息

    public List<String> pageInfoKeys;            //翻页时间排序
    public List<String> drawInfoKeys;            //画笔时间排序
    public List<String> pageTimeKeys;            //每页第一次画笔排序

    public  boolean loadingError;
    public  boolean onlyMedia;


//    private static EPlaybackSessionInfo sharedInstance = null;

    public EPlaybackSessionInfo() {
        super();

        segmentArrays = new ArrayList<PlaybackSegment>();

        pageInfoValues = new HashMap<String, DrawPadInfo>();
        pageTimeValues = new HashMap<String, String>();

        drawInfoKeys = new ArrayList<String>();
        drawInfoTimeValues = new HashMap<String, PlaybackMsgInfo>();


        LogUtil.d("----EPlaybackSessionInfo----", "version:"+ EplayerSetting.getInstance().version);
    }

//    public static EPlaybackSessionInfo sharedSessionInfo() {
//        if (sharedInstance == null)
//            sharedInstance = new EPlaybackSessionInfo();
//
//        return sharedInstance;
//
//    }
//
//    public static void releaseALL() {
//        if (sharedInstance != null) {
//            sharedInstance.clearALL();
//            sharedInstance = null;
//        }
//    }

    public void clearALL() {
        if(null!=segmentArrays)
        segmentArrays.clear();

        if(null!=pageInfoValues)
        pageInfoValues.clear();

        if(null!=pageTimeValues)
        pageTimeValues.clear();


        if(null!=drawInfoTimeValues)
        drawInfoTimeValues.clear();

        if(null!=pageInfoKeys)
        pageInfoKeys.clear();

        if(null!=drawInfoKeys)
        drawInfoKeys.clear();

        if(null!=pageTimeKeys)
        pageTimeKeys.clear();
    }


    public PlaybackSegment loadSegmentWithIndex(int index) {
        if (this.segmentArrays.size() > index&&index>=0) {
            return this.segmentArrays.get(index);
        }
        return null;
    }

    public long loadTimeSegmentWithIndex(int index) {
        long alltime = 0;

        for (int i = 0; i < this.segmentArrays.size(); i++) {

            if (i == index)
                break;

            PlaybackSegment segment = this.segmentArrays.get(i);

            alltime = segment.playTime + alltime;
        }

        return alltime;
    }

    public PlaybackSegment loadSegmentWithTime(long time) {
        for (PlaybackSegment segment : this.segmentArrays) {
            if (segment.startTime <= time && segment.endTime >= time) {
                return segment;
            }
        }
        return null;
    }

    public long loadLastSegmentEndTime() {
        if (this.segmentArrays.size() > 0) {
            int lastIndex = this.segmentArrays.size() - 1;


            PlaybackSegment segment = this.segmentArrays.get(lastIndex);

            return segment.endTime;
        }
        return -1;
    }


    public long progressCorrection(long progress) {

        for (int i = 0; i < (int) this.segmentArrays.size(); i++) {
            PlaybackSegment segment = this.segmentArrays.get(i);
            if(progress<=(segment.endTime-this.playbackBeginTime)){
                if(progress<(segment.startTime-this.playbackBeginTime)){
                    return  segment.startTime -this.playbackBeginTime;
                }else{
                    return  progress;
                }
            }
        }
        return 0;
    }
    public int loadIndexSegmentWithProgressTime(long time) {

        for (int i = 0; i < (int) this.segmentArrays.size(); i++) {
            PlaybackSegment segment = this.segmentArrays.get(i);


            if (segment.playTime >= time)
                return i;

            time = time - segment.playTime;
        }
        return -1;
    }

    public long loadTimeWithProgressTime(long time) {

        for (int i = 0; i < (int) this.segmentArrays.size(); i++) {
            PlaybackSegment segment = this.segmentArrays.get(i);

            if (segment.playTime >= time)
                return time;

            time = time - segment.playTime;
        }

        return 0;
    }

    public int loadIndexSegmentWithTime(long time) {

        for (int i = (int) this.segmentArrays.size() - 1; i >= 0; i--) {
            PlaybackSegment segment = this.segmentArrays.get(i);
            if (segment.startTime <= time && segment.endTime >= time) {
                return i;
            }
        }
        return -1;
    }

    public long loadTimeSegmentWithTime(long time) {
        long alltime = 0;
        for (PlaybackSegment segment : this.segmentArrays) {

            if (segment.startTime >= time && segment.endTime <= time) {
                break;
            }
            alltime = segment.playTime + alltime;
        }

        return alltime;
    }

    public boolean checkTimeWithSegment(long time){
        for (PlaybackSegment segment:this.segmentArrays) {

            if(segment.startTime<=time&&segment.endTime>=time){
                return true;
            }
        }

        return false;
    }

    public DrawPadInfo loadDrawPadInfoWithTime(long time,long beginTime) {
        if (this.pageInfoKeys == null||this.pageInfoKeys.size()==0) {

            this.pageInfoKeys = CollectionsUtil.coverToList(this.pageInfoValues.keySet());

            Collections.sort(this.pageInfoKeys);
        }
        DrawPadInfo info = null;

        for (int i = (int) this.pageInfoKeys.size() - 1; i >= 0; i--) {

            String key = this.pageInfoKeys.get(i);
            long _time = NumberUtil.parseLong(key, 0);

            if (_time <= time) {
                DrawPadInfo _info = this.pageInfoValues.get(key);

                if(_time>beginTime){
                    return _info;
                }else{
                    if(info==null)
                        return null;
                    if(_info.pptId!=info.pptId){
                       return info;
                    }else {
                        return _info;
                    }
                }

            }
            info = this.pageInfoValues.get(key);

        }

        return info;
    }

    public List<DrawMsgInfo> loadDrawMsgInfoWithStartTime(long startTime, long endTime) {


        List<String> dataKay = new ArrayList<String>();

        for (int i = 0; i < this.drawInfoKeys.size(); i++) {

            String key = this.drawInfoKeys.get(i);

            long _time = NumberUtil.parseLong(key, 0);

            if (_time > endTime)
                break;

            if (_time >= startTime && _time <= endTime) {
                dataKay.add(key);
            }


        }

        return DrawMsgInfo.loadByKeys(dataKay);
    }

    public PlaybackMsgInfo loadPlaybackMsgInfoWithTime(long time) {


        for (PlaybackMsgInfo info : this.drawInfoTimeValues.values()) {

            if (info.startTime <= time && info.endTime >= time) {
                return info;
            }

        }

        return null;
    }

    public long loadTimeWithPage(String key) {

        String value = this.pageTimeValues.get(key);
        long _time = NumberUtil.parseLong(value, 0);

        return _time;
    }

    public String loadTimeWithProgress(long progress){

//        EPlaybackSessionInfo  session =  EPlaybackSessionInfo.sharedSessionInfo();

        int currentSegmentIndex =  this.loadIndexSegmentWithProgressTime(progress);

        if(currentSegmentIndex==-1){
            return null;
        }

        PlaybackSegment  segment =  this.loadSegmentWithIndex(currentSegmentIndex);

        long   seq = this.loadTimeWithProgressTime(progress);

        long    currentPlaybackTime  = segment.startTime+seq;

        return DateUtil.getHms(currentPlaybackTime);
    }

    public   String getShowTimeWithProgress(long progressTime){
        try {
//            EPlaybackSessionInfo session = EPlaybackSessionInfo.sharedSessionInfo();
            //根据进度时间查找片段
            int currentSegmentIndex = this.loadIndexSegmentWithProgressTime(progressTime);

            if (progressTime > totalPlaybackTime) {
                currentSegmentIndex = PlaybackDriver.MAX_SEGMENT_END;
            }

            PlaybackSegment segment = this.loadSegmentWithIndex(currentSegmentIndex);


            long seq = 0;


            if (segment != null) {

                seq = this.loadTimeWithProgressTime(progressTime);
                //生成真实时间
                long currentPlaybackTime = (segment.startTime + seq);
                 LogUtil.d("currentPlaybackTime:" + currentPlaybackTime + ";playbackEndTime:" + this.playbackEndTime);
                long time = currentPlaybackTime - this.playbackBeginTime;
                String showtime = DateUtil.getHmsFromMilliSecond(time);
                return showtime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public int loadNextPageWithPage(String key) {
        if (this.pageTimeKeys == null) {


            this.pageTimeKeys = CollectionsUtil.coverToList(this.pageTimeValues.keySet());

            Collections.sort(this.pageTimeKeys);
        }

        int currentIndex = CollectionsUtil.keyIndex(this.pageTimeKeys,key);

        if ((currentIndex + 1) < this.pageTimeKeys.size()) {
            String value = this.pageTimeKeys.get(currentIndex + 1);

            int start = value.indexOf("-");

            String substr = value.substring(start + 1);
            long _time = NumberUtil.parseLong(substr, 0);

            return (int) _time;
        }


        return -1;
    }

    public int loadPrePageWithPage(String key) {
        if (this.pageTimeKeys == null) {

            this.pageTimeKeys = CollectionsUtil.coverToList(this.pageTimeValues.keySet());

            Collections.sort(this.pageTimeKeys);

        }

        int currentIndex = CollectionsUtil.keyIndex(this.pageTimeKeys,key);

        if ((currentIndex - 1) >= 0) {
            String value = this.pageTimeKeys.get(currentIndex - 1);

            int start = value.indexOf("-");
            String substr = value.substring(start + 1);
            long _time = NumberUtil.parseLong(substr, 0);

            return (int) _time;
        }


        return -1;
    }

    public boolean checkLoadTimeWithPage(String key) {
        return this.pageTimeValues.get(key) != null;
    }

    public List<DrawMsgInfo> loadDrawMsgInfoWithKeys(List<String> keys) {


        return DrawMsgInfo.loadByKeys(keys);
    }


}


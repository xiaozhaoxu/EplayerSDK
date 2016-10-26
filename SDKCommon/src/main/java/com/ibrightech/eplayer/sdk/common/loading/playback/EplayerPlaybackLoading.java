package com.ibrightech.eplayer.sdk.common.loading.playback;

import android.content.Context;

import com.alibaba.fastjson.JSONReader;
import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.db.IbrightechDatabase;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerInitType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.other.EplayerNetErrorEntity;
import com.ibrightech.eplayer.sdk.common.entity.playback.LiveRoomStatus;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlayList;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackSegment;
import com.ibrightech.eplayer.sdk.common.entity.session.EPlaybackSessionInfo;
import com.ibrightech.eplayer.sdk.common.loading.EplayerBaseLoading;
import com.ibrightech.eplayer.sdk.common.net.http.BaseOkHttpProtocol;
import com.ibrightech.eplayer.sdk.common.net.http.PlaybackInfoProtocol;
import com.ibrightech.eplayer.sdk.common.net.ws.event.EplayerInitEvent;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.common.util.CollectionsUtil;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.FileUtils;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;
import com.ibrightech.eplayer.sdk.common.util.NumberUtil;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by junhai on 16/8/25.
 */
public class EplayerPlaybackLoading extends EplayerBaseLoading {



    private String playlistUrl = null;
    private String docRecordUrl = null;
    private String statusUrl = null;
    private String whiteboardUrl = null;

    long playbackBeginTime;  //回看开始时间，以秒为单位
    long playbackEndTime;    //回看结束时间，以秒为单位

    long drawPadBeginTime;  //换页开始时间，以秒为单位

    private HashMap<String, PlayList> playListValues;

    public EplayerPlaybackLoading(Context context,EPlayerData ePlayerData,EPlaybackSessionInfo sessionInfo){
        this.context=context;
        this.data=ePlayerData;
        this.sessionInfo=sessionInfo;
    }


    private String getTempPath() {

        String documentsDirectory = StorageUtil.getCacheDir();

        String fileName = documentsDirectory + "/" + System.currentTimeMillis() + "-" + System.currentTimeMillis();

        return fileName;
    }



    @Override
    protected void getLiveRoomInfoSuccess() {
        toGetPlaybackInfo();
    }

    void toGetPlaybackInfo(){
        if(executeCancel()){
            return;
        }

        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeGetPlaybackInfo);
            EventBus.getDefault().post(msg);
        }
        try {
            getPlaybackInfo();
        }catch (Exception e){
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeGetPlaybackInfoError);
            EventBus.getDefault().post(msg);
            this.loadError();
        }
    }

    private void getPlaybackInfo() throws Exception {
        if(executeCancel()){
            return;
        }

        //获取回看地址，存在盗链问题
        PlaybackInfoProtocol liveRequest = new PlaybackInfoProtocol(data.liveClassroomId,data.playbackid,sessionInfo);

        liveRequest.execute(context, new BaseOkHttpProtocol.CallBack() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int errorCode, String msg, Object object) {
                LiveRoomInfoData liveRoomInfo = sessionInfo.infoData;
                if (CheckUtil.isEmpty(liveRoomInfo)||CheckUtil.isEmpty(liveRoomInfo.playbackBeginTime)||CheckUtil.isEmpty(liveRoomInfo.playbackEndTime)) {
                    //todo 回看还没有生成
                    loadError();
                    return;
                }

                String jsonPath = liveRoomInfo.jsonPath;

                if (null != liveRoomInfo && liveRoomInfo.canSplice) {
                    playlistUrl = EplayerSetting.getInstance().playback_url + "/" + jsonPath + "/playlist2.json";
                } else {
                    playlistUrl = EplayerSetting.getInstance().playback_url + "/" + jsonPath + "/playlist.json";
                }

                statusUrl = EplayerSetting.getInstance().playback_url + "/" + jsonPath + "/status.json";
                whiteboardUrl = EplayerSetting.getInstance().playback_url + "/" + jsonPath + "/whiteboard.json";
                docRecordUrl = EplayerSetting.getInstance().playback_url + "/" + jsonPath + "/pptlog.json";

                try {
                    loadLiveRoomInfoFinished();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public boolean onFailure(Call call, EplayerNetErrorEntity errorEntity) {
                EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeGetPlaybackInfoError);
                EventBus.getDefault().post(msg);
                return false;
            }

            @Override
            public void onUpProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

            }
        });



    }

    private void loadLiveRoomInfoFinished() throws Exception {
        if(executeCancel()){
            return;
        }

        //获取原始的回看时间
        LiveRoomInfoData infoData = sessionInfo.infoData;
        infoData.liveStatus = EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay;
        infoData.playMusic = false;

        Date playbackBeginDate = DateUtil.getDate(infoData.playbackBeginTime);
        Date playbackEndDate = DateUtil.getDate(infoData.playbackEndTime);


         LogUtil.d("课堂直播时间：" + infoData.playbackBeginTime + " -- " + infoData.playbackEndTime);


        if(playbackBeginDate!=null)
            playbackBeginTime = playbackBeginDate.getTime();
        if(playbackEndDate!=null)
            playbackEndTime = playbackEndDate.getTime();


        this.loadPlaylist();

    }

    private void loadPlaylist() throws Exception {
        if(executeCancel()){
            return;
        }
        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypePlaybackPlaylist);
            EventBus.getDefault().post(msg);
        }

        Response response= OkHttpUtils.get(playlistUrl).execute();
        if(response.isSuccessful()){
            String content= response.body().string();
            loadPlaylistRcordFinished(new JSONObject(content));
        }else{
            {
                EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypePlaybackPlaylistError);
                EventBus.getDefault().post(msg);
            }
        }

    }

    //解析视频列表数据
    private void loadPlaylistRcordFinished( JSONObject jsonObject) throws Exception {
        if(executeCancel()){
            return;
        }

        LogUtil.d("FileDownLoadUtil loadPlaylistRcordFinished");


        playListValues = new HashMap<String, PlayList>();

        //解析播放列表

        if(jsonObject!=null) {

            String videoDomain = jsonObject.optString("videoDomain");
            String audioDomain = jsonObject.optString("audioDomain");
            EplayerSetting.getInstance().setSpliceVideoPlayBaseUrl(videoDomain);
            EplayerSetting.getInstance().setSpliceAudioPlayBaseUrl(audioDomain);
            {
                JSONObject video1 = jsonObject.optJSONObject("video1");
                if (video1 != null) {
                    long currentTime = 0;
                    JSONArray playList = video1.optJSONArray("playList");
                    List<PlayList> playListlist=new ArrayList<PlayList>();

                    for (int i = 0; i < playList.length(); i++) {
                        JSONObject json = playList.getJSONObject(i);
                        //根据文件名字分析出视频的开始时间和结束时间
                        PlayList info = PlayList.fromJson(json, videoDomain);
                        info.type = EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo;

                        //TODO:特殊注明,小于3秒的视频扔掉
                        if (info != null && info.playTime > 3) {
                            playListlist.add(info);
                        }
                    }
                    Collections.sort(playListlist);
                    for(int i=0;i<playListlist.size();i++){
                        PlayList info=playListlist.get(i);
                        if ( currentTime <= info.startTime) {
                            playListValues.put(info.startTime + "", info);
                            currentTime = info.endTime;
                        }
                    }
                }
            }

            {
                JSONObject audio1 = jsonObject.optJSONObject("audio1");
                if (audio1 != null) {
                    long currentTime = 0;
                    JSONArray playList = audio1.optJSONArray("playList");


                    for (int i = 0; i < playList.length(); i++) {
                        JSONObject json = playList.getJSONObject(i);
                        //根据文件名字分析出音频的开始时间和结束时间
                        PlayList info = PlayList.fromJson(json, audioDomain);
                        info.type = EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio;

                        //TODO:特殊注明,小于3秒的音频扔掉
                        if (info != null && info.playTime > 3 && currentTime <= info.startTime) {

                            playListValues.put(info.startTime + "", info);
                            currentTime = info.endTime;
                        }

                    }
                }
            }
        }



            LogUtil.d("分析出音视频信息数据：" + playListValues.size() + "条");

            List<String> sortPlayListValues = CollectionsUtil.coverToList(playListValues.keySet());
            Collections.sort(sortPlayListValues);

            int i = 0;
            for (String key : sortPlayListValues) {
                PlayList info = playListValues.get(key);
                String start = DateUtil.getString(new Date(info.startTime));
                String end = DateUtil.getString(new Date(info.endTime));


                if (info.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio) {
                    LogUtil.d(String.format("第%03d条：%s -- %s  音频  %s", i + 1, start, end, info.playUrl()));
                } else {
                    LogUtil.d(String.format("第%03d条：%s -- %s  视频  %s", i + 1, start, end, info.playUrl()));
                }
                i++;
            }


        System.gc();
        this.loadStatusRecord();

    }

    private void loadStatusRecord() throws Exception {
        if(executeCancel()){
            return;
        }
        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypePlaybackStatus);
            EventBus.getDefault().post(msg);
        }

        Response response= OkHttpUtils.get(statusUrl).execute();
        if(response.isSuccessful()){
            String content= response.body().string();
            loadStatusRcordFinished(new JSONArray(content));
        }else{
            {
                EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypePlaybackStatusError);
                EventBus.getDefault().post(msg);
            }
        }


    }


    //解析直播状态数据
    private void loadStatusRcordFinished(JSONArray jsonArray) throws Exception {
        LogUtil.d("FileDownLoadUtil loadStatusRcordFinished");

        if(executeCancel()){
            return;
        }


        Map<String, Long> statusValues = new HashMap<String, Long>();

        //解析出直播状态变化数据
        if(jsonArray!=null) {

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject json = jsonArray.getJSONObject(i);

                long seq = json.optLong("seq") / 1000 * 1000;

                Long status = new Long(json.optLong("status"));
                //todo 源头忽略了直播LiveRoomLiveStatusStop的数据
                if(status.intValue()== EplayerLiveRoomLiveStatus.LiveRoomLiveStatusStop.value()){
                    continue;
                }

                statusValues.put(seq + "", status);

            }
        }

        jsonArray = null;

        //将直播状态数据重新组装，从开始到暂停或者停止为一个完整的状态，分析出多条连续的状态数据
        List<LiveRoomStatus> statusArrays = new ArrayList<LiveRoomStatus>();
        {
            List<String> sortStatusValues = CollectionsUtil.coverToList(statusValues.keySet());
            Collections.sort(sortStatusValues);

            LiveRoomStatus liveRoomStatus = null;

            for (int statusIndex = 0; statusIndex < sortStatusValues.size(); statusIndex++) {
                String key = sortStatusValues.get(statusIndex);

                long seq = NumberUtil.parseLong(key, 0);
                Long status = statusValues.get(key);


                if (status.intValue() == EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay.value()) {
                    if (liveRoomStatus == null) {
                        liveRoomStatus = new LiveRoomStatus();
                        if (statusIndex == 0) {
                            if (seq < playbackBeginTime) {
                                liveRoomStatus.startTime = playbackBeginTime;
                            } else {

                                liveRoomStatus.startTime = seq;
                            }
                        } else {
                            liveRoomStatus.startTime = seq;
                        }

                    }
                } else {
                    if (liveRoomStatus == null)
                        continue;

                    liveRoomStatus.endTime = seq;

                    if (liveRoomStatus.endTime < playbackBeginTime) {

                        liveRoomStatus = null;

                    } else {
                        statusArrays.add(liveRoomStatus);
                        liveRoomStatus = null;
                    }
                }

            }

            if (liveRoomStatus != null) {

                liveRoomStatus.endTime = playbackEndTime;
                statusArrays.add(liveRoomStatus);

            }

        }
        //
        {
            if (statusArrays.size() > 0) {
                {
                    //最后一条状态数据的结束时间不能大于直播结束时间
                    LiveRoomStatus lastLiveRoomStatus = CollectionsUtil.lastObject(statusArrays);
                    if (lastLiveRoomStatus.endTime > playbackEndTime) {
                        lastLiveRoomStatus.endTime = playbackEndTime;
                    }

                }
                {
                    //第一条状态数据的结束时间不能小于直播开始时间
                    LiveRoomStatus firstRoomStatus = CollectionsUtil.firstObject(statusArrays);
                    if (firstRoomStatus.startTime < playbackBeginTime) {
                        firstRoomStatus.startTime = playbackBeginTime;
                    }

                }

            }
        }

        ((EPlaybackSessionInfo)sessionInfo).playbackBeginTime = playbackBeginTime;
        ((EPlaybackSessionInfo)sessionInfo).playbackEndTime = playbackEndTime;


            LogUtil.d(String.format("分析出上课信息数据：%d条", (int) statusArrays.size()));

            for (int i = 0; i < statusArrays.size(); i++) {
                LiveRoomStatus statusData = statusArrays.get(i);

                String start = DateUtil.getString(new Date(statusData.startTime));
                String end = DateUtil.getString(new Date(statusData.endTime));


                LogUtil.d(String.format("第%03d条：%s -- %s", i + 1, start, end));


            }



        //合并视频片段信息和状态信息，取数据的交集部分，只保留上课中并且有视频的部分，生成回看片段
        {
            List<String> sortPlayListValues = CollectionsUtil.coverToList(playListValues.keySet());
            Collections.sort(sortPlayListValues);

            List<PlaybackSegment> segmentArrays = new ArrayList<PlaybackSegment>();

            PlaybackSegment playbackSegment = null;

            //视频文件索引位置
            int playlistIndex = 0;



            for (int statusIndex = 0; statusIndex < statusArrays.size(); statusIndex++) {

                LiveRoomStatus liveRoomStatus = statusArrays.get(statusIndex);

                //去掉直播结束后的状态数据
                if (liveRoomStatus.startTime > playbackEndTime) {
                    continue;
                }

                for (int currentIndex = playlistIndex; currentIndex < sortPlayListValues.size(); currentIndex++) {

                    String playlistKey = sortPlayListValues.get(currentIndex);
                    PlayList info = playListValues.get(playlistKey);

                    //如果文件结束时间小于状态开始时间，舍弃掉该部分文件
                    if (info.endTime < liveRoomStatus.startTime) {
                        continue;
                    }

                    //如果视频文件开始时间大于状态结束时间，进入下一条状态数据
                    if (info.startTime > liveRoomStatus.endTime) {
                        // playlistIndex = currentIndex;
                        break;
                    }


                    //视频开始时间大于状态开始时间，从视频开始时间取片段
                    if (info.startTime > liveRoomStatus.startTime) {
                        if (playbackSegment == null) {
                            playbackSegment = new PlaybackSegment();
                            playbackSegment.startTime = info.startTime / 1000 * 1000;
                            playbackSegment.allSegmentStartTime=info.startTime;
                            playbackSegment.type = info.type;
                            playbackSegment.info = info;
                        }
                    }

                    //视频开始时间小于状态开始时间，从状态开始时间取片段
                    if (info.startTime <= liveRoomStatus.startTime) {

                        if (playbackSegment == null) {
                            playbackSegment = new PlaybackSegment();
                            playbackSegment.startTime = liveRoomStatus.startTime / 1000 * 1000;
                            playbackSegment.allSegmentStartTime=info.startTime;
                            playbackSegment.type = info.type;
                            playbackSegment.info = info;
                        }

                    }

                    //视频结束时间大于状态结束时间，片段结束，使用状态结束时间
                    if (info.endTime > liveRoomStatus.endTime) {

                        playbackSegment.endTime = liveRoomStatus.endTime / 1000 * 1000;
                        playbackSegment.loadDone();
                        segmentArrays.add(playbackSegment);
                        playbackSegment = null;

                    }



                    //视频结束时间大于状态结束时间，片段结束，使用视频结束时间
                    if (info.endTime <= liveRoomStatus.endTime) {

                        playbackSegment.endTime = info.endTime / 1000 * 1000;
                        playbackSegment.loadDone();
                        segmentArrays.add(playbackSegment);
                        playbackSegment = null;
                    }

                }

                //状态结束，完整片段
                if (playbackSegment != null) {
                    playbackSegment.endTime = liveRoomStatus.endTime / 1000 * 1000;
                    playbackSegment.loadDone();
                    if (playbackSegment.type != EplayerLiveRoomStreamType.LiveRoomStreamTypeNone && playbackSegment.info != null)
                        segmentArrays.add(playbackSegment);

                    playbackSegment = null;

                }


            }
            {
                //根据片段数据修改回看开始和结束时间
                if (segmentArrays.size() > 0) {
                    {
                        {
                            PlaybackSegment segment = CollectionsUtil.firstObject(segmentArrays);
                            playbackBeginTime = segment.startTime;

                        }

                        {
                            PlaybackSegment segment = CollectionsUtil.lastObject(segmentArrays);
                            playbackEndTime = segment.endTime;

                        }




                            String start = DateUtil.getString(new Date(playbackBeginTime));
                            String end = DateUtil.getString(new Date(playbackEndTime));

                            LogUtil.d(String.format("修正课堂直播时间：%s -- %s", start, end));



                    }


                }
            }

            ((EPlaybackSessionInfo)sessionInfo).playbackBeginTime = playbackBeginTime;
            ((EPlaybackSessionInfo)sessionInfo).playbackEndTime = playbackEndTime;



                LogUtil.d(String.format("分析出片段信息数据：%d条", (int) segmentArrays.size()));


                for (int i = 0; i < segmentArrays.size(); i++) {
                    PlaybackSegment segment = segmentArrays.get(i);
                    switch (segment.type) {
                        case LiveRoomStreamTypeVideo: {

                            String start = DateUtil.getString(new Date(segment.startTime));
                            String end = DateUtil.getString(new Date(segment.endTime));

                            LogUtil.d(String.format("第%03d条：%s -- %s  视频  %s", i + 1, start, end, segment.info.playUrl()));
                            break;
                        }
                        case LiveRoomStreamTypeAudio: {

                            String start = DateUtil.getString(new Date(segment.startTime));
                            String end = DateUtil.getString(new Date(segment.endTime));

                            LogUtil.d(String.format("第%03d条：%s -- %s  音频  %s", i + 1, start, end, segment.info.playUrl()));

                            break;
                        }

                        default: {

                            String start = DateUtil.getString(new Date(segment.startTime));
                            String end = DateUtil.getString(new Date(segment.endTime));

                            LogUtil.d(String.format("第%03d条：%s -- %s  无任何音视频  %s", i + 1, start, end, segment.info.playUrl()));


                            break;
                        }

                    }


            }
            ((EPlaybackSessionInfo)sessionInfo).segmentArrays.clear();
            long totalPlaybackTime = 0;
            for (PlaybackSegment playbackSegment1 : segmentArrays) {
                totalPlaybackTime += playbackSegment1.playTime;
                ((EPlaybackSessionInfo)sessionInfo).segmentArrays.add(playbackSegment1);
            }
            segmentArrays.clear();
            segmentArrays= null;

            ((EPlaybackSessionInfo)sessionInfo).totalPlaybackTime = totalPlaybackTime;


        }

        System.gc();

        if(  ((EPlaybackSessionInfo)sessionInfo).segmentArrays.size()==0){
            this.loadSuccess();
            return;
        }
        this.loadDocRcord();

    }


    private void loadDocRcord() throws Exception {
        if(executeCancel()){
            return;
        }
        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypePlaybackDocRcord);
            EventBus.getDefault().post(msg);
        }

        Response response= OkHttpUtils.get(docRecordUrl).execute();
        if(response.isSuccessful()){
            String content= response.body().string();
            loadDocRcordFinished(new JSONArray(content));
        }else{
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypePlaybackDocRcordError);
            EventBus.getDefault().post(msg);
        }


    }


    //解析直播状态数据
    private void loadDocRcordFinished(JSONArray jsonArray) throws Exception {
        LogUtil.d("FileDownLoadUtil loadDocRcordFinished");

        if(executeCancel()){
            return;
        }

        Map<String, DrawPadInfo> pageInfoValues = new HashMap<String, DrawPadInfo>();
        Map<String, String> pageTimeValues = new HashMap<String, String>();

        //解析画板变化数据
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DrawPadInfo info = DrawPadInfo.fromJson(jsonObject);


                if (info != null) {
                    pageInfoValues.put(info.seq + "", info);
                }

            }
        }

        jsonArray = null;



        //画板开始时间，用来过滤画笔使用
        drawPadBeginTime = 0;

        {

            //数据通过时间排序
            List<String> sortPageInfoValues = CollectionsUtil.coverToList(pageInfoValues.keySet());
            Collections.sort(sortPageInfoValues);

            //上一个DrawPadInfo 对应的片段，当 值为空时其实已经查询到两条数据不再片段内了
            PlaybackSegment playbackSegment = null;

            //数据从最后一条开始读取，大于回看结束时间的数据抛弃，读取到时间小于回看开始时间时停止
            //片段之外的数据也扔掉，不过在片段开始之前那条数据要保留

            //要移除的数据
            List<String> invalidPageInfoValues =  new ArrayList<String>();
            for(int i= sortPageInfoValues.size()-1;i>=0;i--){
                String key = sortPageInfoValues.get(i);
                DrawPadInfo  info =  pageInfoValues.get(key) ;

                //回看结束之后的数据，扔掉
                if(info.seq>playbackEndTime){
                    invalidPageInfoValues.add(info.seq + "");
                    continue;
                }

                //查询是否在回看片段内，返回null表示不再，保留之前的一条
                PlaybackSegment segment =  ((EPlaybackSessionInfo)sessionInfo).loadSegmentWithTime(info.seq);

                //数据部再片段内
                if(segment==null){
                    //之前一条的数据还在片段内，数据保留
                    if(playbackSegment!=null){
                        info.seq =playbackSegment.startTime;
                    } else{
                        //之前一条的数据不在片段内，数据扔掉
                        invalidPageInfoValues.add(info.seq + "");
                    }
                }


                playbackSegment =segment;

                //数据时间小于回看开始时间了，
                if(info.seq<playbackBeginTime){
                    //将第一条不在回看范围内的数据留下来，作为开始回看的开始画板
                    if(drawPadBeginTime==0){
                        drawPadBeginTime = info.seq;
                        continue;
                    }
                    //移除掉之前的所有数据
                    invalidPageInfoValues.add(info.seq + "");

                }

            }
            //没有获取到画板开始时间，使用回看开始时间
            if(drawPadBeginTime==0){
                drawPadBeginTime = playbackBeginTime;
            }
            //去掉不要的画板变化数据
            for (String key:invalidPageInfoValues){
                pageInfoValues.remove(key);
            }
        }

        //找出每页的第一次操作时间,key规则  %010d-%010d ==> pptId-page
        List<String> sortPageInfoValues = CollectionsUtil.coverToList(pageInfoValues.keySet());
        Collections.sort(sortPageInfoValues);
        {
            for(int i= sortPageInfoValues.size()-1;i>=0;i--){
                String key = sortPageInfoValues.get(i);
                DrawPadInfo  info =  pageInfoValues.get(key) ;

                String  infoKey = info.infoKey();

                if (info.padType == EplayerDrawPadType.DrawPadTypeDocument) {
                    pageTimeValues.put(infoKey,info.seq+"");
                }

            }

        }




            LogUtil.d(String.format("翻页信息数据：%d条", (int) sortPageInfoValues.size()));


            for (int i = 0; i < sortPageInfoValues.size(); i++) {
                String key = sortPageInfoValues.get(i);
                DrawPadInfo info = pageInfoValues.get(key);


                String time = DateUtil.getString(new Date(info.seq));


                if (info.padType == EplayerDrawPadType.DrawPadTypeWhiteBoard) {
                    LogUtil.d(String.format("第%03d条：时间【%s】 --编号【%d】 --页码【%d】   白板", i + 1, time, info.pptId, info.page));
                } else {
                    LogUtil.d(String.format("第%03d条：时间【%s】 --编号【%d】 --页码【%d】   文档", i + 1, time, info.pptId, info.page));
                }


            }

            LogUtil.d(String.format("页码标记信息数据：%d条", (int) pageTimeValues.size()));

            List<String> sortPageTimeValues = CollectionsUtil.coverToList(pageTimeValues.keySet());
            Collections.sort(sortPageTimeValues);


            for (int i = 0; i < sortPageTimeValues.size(); i++) {
                String key = sortPageTimeValues.get(i);
                String seqStr = pageTimeValues.get(key);

                long seq = NumberUtil.parseLong(seqStr, 0);

                String time = DateUtil.getString(new Date(seq));

                LogUtil.d(String.format("第%03d条：编号【%s】 --时间【%s】", i + 1, key, time));

            }


        ((EPlaybackSessionInfo)sessionInfo).pageInfoValues.clear();
        for (String key:pageInfoValues.keySet()){
            DrawPadInfo drawPadInfo = pageInfoValues.get(key);
            ((EPlaybackSessionInfo)sessionInfo).pageInfoValues.put(key,drawPadInfo);
        }
        pageInfoValues.clear();
        pageInfoValues = null;

        ((EPlaybackSessionInfo)sessionInfo).pageTimeValues.clear();

        for (String key:pageTimeValues.keySet()){
            String seqStr = pageTimeValues.get(key);
            ((EPlaybackSessionInfo)sessionInfo).pageTimeValues.put(key,seqStr);
        }
        pageTimeValues.clear();
        pageTimeValues = null;

        System.gc();

        if(  ((EPlaybackSessionInfo)sessionInfo).pageInfoValues.size()==0){
            this.loadSuccess();
            return;
        }

        this.loadWhiteboard();

    }

    private void loadWhiteboard() throws Exception {
        if(executeCancel()){
            return;
        }

        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypePlaybackWhiteboard);
            EventBus.getDefault().post(msg);
        }
       String whiteboardPath = this.getTempPath()+".json";

        Response response= OkHttpUtils.get(whiteboardUrl).execute();
        if(response.isSuccessful()){
            byte[] bytes= response.body().bytes();
            InputStream sbs = new ByteArrayInputStream(bytes);
            try{
                File childfile= new File(whiteboardPath);
                if(null!=whiteboardPath&&whiteboardPath.length()>0){
                    File file=new File(whiteboardPath);
                    File parentFile=file.getParentFile();
                    parentFile.mkdirs();
                }

                FileUtils.copyInputStreamToFile(sbs, childfile);

            }catch (Exception e){

            }finally {
                 this.loadWhiteboardRcordFinished(whiteboardPath);
            }
        }else{
            LogUtil.d("FileDownLoadUtil onFailure strMsg:");
            this.loadWhiteboardRcordFinished(whiteboardPath);
        }

    }
    private DrawMsgInfo getMsg(Map map){

        DrawMsgInfo msg = new DrawMsgInfo();
        msg.setCreateTime((String) map.get("createTime"));
        msg.setLiveClassroomId((String) map.get("liveClassroomId"));
        msg.setResType((String) map.get("resType"));

        msg.setDrawMsg((String) map.get("drawMsg"));

        if(map.get("isBlank").equals("true")){
            msg.setPadType(EplayerDrawPadType.DrawPadTypeWhiteBoard);
        }else{
            msg.setPadType(EplayerDrawPadType.DrawPadTypeDocument);
        }

        msg.setPage((Integer) map.get("page"));
        msg.setSeq((Long) map.get("seq"));
        msg.setPptPageId((Integer) map.get("pptPageId"));
        msg.setPptId((Integer) map.get("pptId"));
        return msg;
    }


    //解析直播状态数据
    private void loadWhiteboardRcordFinished(String result) {
        LogUtil.d("FileDownLoadUtil loadWhiteboardRcordFinished");
        if(executeCancel()){
            return;
        }

        Map<String, PlaybackMsgInfo> drawInfoTimeValues = new HashMap<String, PlaybackMsgInfo>();

        List<String> sortDrawInfoValues = new ArrayList<String>();

        PlaybackMsgInfo playbackMsgInfo = null;

        long pageEndTime = 0;


        long time = System.currentTimeMillis();
        int dacount =0;
        try {


            JSONReader reader = new JSONReader(new FileReader(result));
            reader.startArray();

            long pretime =0;
            List<DrawMsgInfo>drawInfoList=new ArrayList<DrawMsgInfo>();
            while (reader.hasNext()){
                LogUtil.d("FileDownLoadUtil  dacount:"+dacount);
                Map map = new HashMap();
                reader.readObject(map);

                DrawMsgInfo info = this.getMsg(map);

                //理论上服务器给的画笔信息是按照seq排序的，这儿加上一个逻辑，防止服务器数据上出现问题，出现后面一条数据时间小余前面一条时，忽略
                if(info.getSeq()<=pretime)
                    continue;

                pretime = info.getSeq();

                if(info.getSeq() >=drawPadBeginTime&& info.getSeq() <=playbackEndTime) {

                    sortDrawInfoValues.add(info.getSeq() + "");

                    info.loadInfoType();

                    if (playbackMsgInfo == null) {
                        playbackMsgInfo = new PlaybackMsgInfo();
                        playbackMsgInfo.startTime = info.getSeq();
                        playbackMsgInfo.page = info.getPage();
                    }

                    if (playbackMsgInfo.page != info.getPage()) {
                        playbackMsgInfo.endTime = info.getSeq();
                        playbackMsgInfo.loadTime();
                        drawInfoTimeValues.put(playbackMsgInfo.startTime + "", playbackMsgInfo);

                        playbackMsgInfo = null;

                        playbackMsgInfo = new PlaybackMsgInfo();
                        playbackMsgInfo.startTime = info.getSeq();
                        playbackMsgInfo.page = info.getPage();
                    }
                    pageEndTime = info.getSeq();
                    playbackMsgInfo.addDrawMsgInfo(info);

                    drawInfoList.add(info);

                    dacount++;

                    if(dacount%1000==0){


                        FlowManager.getDatabase(IbrightechDatabase.class)
                                .executeTransaction(FastStoreModelTransaction.insertBuilder(FlowManager.getModelAdapter(DrawMsgInfo.class))
                                        .addAll(drawInfoList)
                                        .build());

//                        DrawMsgInfo.saveInTx(drawInfoList);
                        drawInfoList.clear();
                    }

                }
            }

            FlowManager.getDatabase(IbrightechDatabase.class)
                    .executeTransaction(FastStoreModelTransaction.insertBuilder(FlowManager.getModelAdapter(DrawMsgInfo.class))
                            .addAll(drawInfoList)
                            .build());
//            new SaveModelTransaction<>(ProcessModelInfo.withModels(drawInfoList)).onExecute();
//            DrawMsgInfo.saveInTx(drawInfoList);
            drawInfoList.clear();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            LogUtil.d("----insert db time----", System.currentTimeMillis() - time + " ms dacount:" + dacount);
        }

        if (playbackMsgInfo != null) {
            if(pageEndTime<playbackBeginTime)
                pageEndTime = playbackBeginTime;

            if(pageEndTime>playbackEndTime)
                pageEndTime = playbackEndTime;

            playbackMsgInfo.endTime = pageEndTime;
            playbackMsgInfo.loadTime();
            drawInfoTimeValues.put(playbackMsgInfo.startTime + "", playbackMsgInfo);

            playbackMsgInfo = null;
        }


        LogUtil.d(String.format("画笔信息汇总数据：%d条 页码数:%d", (int) sortDrawInfoValues.size(), (int) drawInfoTimeValues.size()));


        ((EPlaybackSessionInfo)sessionInfo).drawInfoKeys.clear();

        for (String key : sortDrawInfoValues) {
            ((EPlaybackSessionInfo)sessionInfo).drawInfoKeys.add(key);
        }

        ((EPlaybackSessionInfo)sessionInfo).drawInfoTimeValues.clear();

        for (String key : drawInfoTimeValues.keySet()) {
            PlaybackMsgInfo msgInfo = drawInfoTimeValues.get(key);
            ((EPlaybackSessionInfo)sessionInfo).drawInfoTimeValues.put(key,msgInfo);
        }
        drawInfoTimeValues.clear();
        drawInfoTimeValues = null;


        LogUtil.d(sessionInfo.toString());

        System.gc();
        //todo 临时加的
        loadSuccess();
    }

    private void loadError() {
        LogUtil.d("FileDownLoadUtil loadError");

        System.gc();
        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeInitError);
            EventBus.getDefault().post(msg);
        }
    }

    private void loadSuccess() {


        if(  ((EPlaybackSessionInfo)sessionInfo).pageInfoValues.size()==0&&  ((EPlaybackSessionInfo)sessionInfo).drawInfoKeys.size()==0&&!  ((EPlaybackSessionInfo)sessionInfo).loadingError){
            ((EPlaybackSessionInfo)sessionInfo).onlyMedia = true;
        }
        System.gc();
        {
            EplayerInitEvent msg = new EplayerInitEvent(EplayerInitType.EplayerInitTypeInitFinished);
            EventBus.getDefault().post(msg);
        }

    }

}

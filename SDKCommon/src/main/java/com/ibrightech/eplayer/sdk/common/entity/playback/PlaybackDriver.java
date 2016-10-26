package com.ibrightech.eplayer.sdk.common.entity.playback;


import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerDrawPadType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamIndex;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.entity.DrawMsgInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.DrawPadInfo;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomAudio;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomVideo;
import com.ibrightech.eplayer.sdk.common.entity.session.EPlaybackSessionInfo;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawMsgInfoEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoChangeEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.DrawPadInfoInitEvent;
import com.ibrightech.eplayer.sdk.common.net.ws.event.VideoAudioStatusEvent;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by junhai on 14-11-20.
 */
public class PlaybackDriver {

    public static int MAX_SEGMENT_END = -999999;

    Object lock;

    public long playbackTime;

    Timer timer;
    TimerTask task;
    long currentTimerToken;

    long currentPlaybackBeginTime;       //已经播放的时间，以秒为单位

    long playbackBeginTime;       //回看开始时间，以秒为单位

    long playbackEndTime;         //回看结束时间，以秒为单位

    long totalPlaybackTime;       //回看总时间，所有片段时间的总和，不是回看开始时间减回看结束时间

    long progressPlaybackTime;     //累加之前片段的播放时间

    boolean isPlayingMedia;

    boolean isPause;

    long token;

    int currentSegmentIndex;    //当前正在处理的片段
    long currentSegmentTime;    //当前正在处理的片段数据的片段长度

    long currentMsgTime;    //当前正在处理的片段位置

    DrawPadInfo currentPadInfo;  //缓存一下当前时间对应的画板

    long previousTime;

    EventBus bus;


    private EPlaybackSessionInfo eplayerSessionInfo;

    private OnEnginListener listener;

    public OnEnginListener getListener() {
        return listener;
    }

    public void setListener(OnEnginListener listener) {
        this.listener = listener;
    }


    public   interface OnEnginListener {

        boolean playDoneIfHasMedia();    //判断视频是否播放完毕

        long playTimeIfHasMedia();            //获取资源的播放时间，用来处理定时处理

        void stopPlaybackEngin();            //停止引擎

        void currentAbsTime(long time);      //当前进度时间

        //通知当前时间状态：progress 片段总时间长度下的播放进度，totalTime 片段总时间长度，playbackTime 当前对应的播放时间，playbackBeginTime 回看开始时间，playbackEndTime 回看结束时间
        void showProgressTime(long progress, long totalTime, long playbackTime, long playbackBeginTime, long playbackEndTime);
    }

    public PlaybackDriver(EPlaybackSessionInfo eplayerSessionInfo) {
        super();
        this.eplayerSessionInfo=eplayerSessionInfo;
        bus = EventBus.getDefault();
//        bus.register(this);


        lock = new Object();

    }


    class EnginThread extends Thread{

        public void run() {

        }

    }





    public void resetNextSegment(){


        PlaybackSegment segment = eplayerSessionInfo.loadSegmentWithIndex(currentSegmentIndex+1);
        if (segment == null)
            return;

        this.resetPlaybackWithTime(segment.startTime);

    }

    //从某片段开始
    public boolean resetSegmentWithIndex(int index) {

        //缓存当前片段
        currentSegmentIndex = index;

        //查询片段，片段不存在就返回 false
        PlaybackSegment segment = eplayerSessionInfo.loadSegmentWithIndex(currentSegmentIndex);
        if (segment == null)
            return false;

        progressPlaybackTime = eplayerSessionInfo.loadTimeSegmentWithIndex(currentSegmentIndex);

        //获取片段开始时间
        currentPlaybackBeginTime = segment.startTime / 1000;

        //获取片段长度
        currentSegmentTime = segment.playTime / 1000;

        LiveRoomInfoData data = eplayerSessionInfo.infoData;
        segment.seq=0;
        data.currentPlaybackSegment=segment;
        data.liveStatus = EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay;
        //发送音视频播放通知
        if (segment.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo) {
            //发送播放视频通知
            data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo;
            data.isStreamPush = true;

            LiveRoomVideo video = new LiveRoomVideo();
            video.isPushStream = true;
            video.playUrl = segment.playUrl();
            data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;
            data.video1 = video;

        } else if (segment.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio) {
            //发送播放音频通知

            data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio;
            data.isStreamPush = true;

            LiveRoomAudio audio = new LiveRoomAudio();
            audio.playUrl = segment.playUrl();
            audio.isPushStream = true;
            data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;
            data.audio1 = audio;

        } else {
            //发送停止通知
            data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeNone;
            data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexNone;
            data.isStreamPush = false;
            data.audio1.isPushStream = false;
            data.video1.isPushStream = false;
        }

        bus.post(new VideoAudioStatusEvent(data));

        return true;
    }

    public void resetSegmentWithTime(long time) {

        currentSegmentIndex = eplayerSessionInfo.loadIndexSegmentWithTime(time);

        PlaybackSegment segment = eplayerSessionInfo.loadSegmentWithIndex(currentSegmentIndex);


        currentPlaybackBeginTime = segment.startTime / 1000;

        currentSegmentTime = segment.playTime / 1000;
    }


    public void startPlayback() {

        //没有片段，就别开始啦
        if( eplayerSessionInfo.segmentArrays.size()==0)
            return;


        totalPlaybackTime = eplayerSessionInfo.totalPlaybackTime;

        playbackBeginTime = eplayerSessionInfo.playbackBeginTime;
        playbackEndTime = eplayerSessionInfo.playbackEndTime;

        //从第一个片段开始
        this.resetSegmentWithIndex(0);

        currentPadInfo = new DrawPadInfo();

        currentPadInfo.padType = EplayerDrawPadType.DrawPadTypeDocument;

        currentPadInfo.page = 0;


        //检查页面信息,默认获取第一个 DrawPadInfo，所以传递时间为0就行
        DrawPadInfo drawPadInfo = eplayerSessionInfo.loadDrawPadInfoWithTime(0, 0);


        if (drawPadInfo != null) {

            currentPadInfo = drawPadInfo;

            eplayerSessionInfo.drawPadInfo = drawPadInfo;



                String bank = "文档";
                if (currentPadInfo.padType == EplayerDrawPadType.DrawPadTypeWhiteBoard) {
                    bank = "白板";
                }
                String scroll = "";
                if (currentPadInfo.isWordScroll) {
                    scroll = "滚动";
                }
                LogUtil.d(String.format("------页面初始化------页码:%d---%d--%s--%s", drawPadInfo.page, drawPadInfo.seq, bank, scroll));

            eplayerSessionInfo.drawPadInfo = drawPadInfo;
            bus.post(new DrawPadInfoInitEvent(drawPadInfo));
        }else{
            LogUtil.d("------无翻页信息------");

        }

        LogUtil.d("------setFireDate:[NSDate distantPast]------");

        this.startTimer();
    }

    private void stopTimer() {

        //停止定时器


        PlaybackDriver.this.currentTimerToken = System.currentTimeMillis();
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    private void startTimer() {

        //开始定时器
        timer = new Timer();
        PlaybackDriver.this.currentTimerToken = System.currentTimeMillis();
        task = new TimerTaskMethod(PlaybackDriver.this.currentTimerToken);
        timer.schedule(task, 0, 1000);


    }

    public void pausePlayback() {

        //暂停回看
        synchronized (lock) {

            isPause = true;

            this.stopTimer();

            LogUtil.d("------setFireDate:[NSDate distantFuture]------");
        }

    }

    public void resumePlayback() {

        //恢复回看
        synchronized (lock) {

            isPause = false;

            this.stopTimer();

            this.startTimer();

            LogUtil.d("------setFireDate:[NSDate distantPast]------");
        }
    }

    //查找下一页
    public int nextPagePPtId(int pptId, int currentPage) {

        String key = String.format("%010d-%010d", pptId, currentPage);


        return eplayerSessionInfo.loadNextPageWithPage(key);

    }

    //查找上一页
    public int prePagePPtId(int pptId, int currentPage) {

        String key = String.format("%010d-%010d", pptId, currentPage);


        return eplayerSessionInfo.loadPrePageWithPage(key);
    }

    //关闭回看
    public void closePlayback() {
        synchronized (lock) {

            isPause = true;

            LogUtil.d("------setFireDate:[NSDate distantFuture]------");
            this.stopTimer();


            LiveRoomInfoData data = eplayerSessionInfo.infoData;

            if(data!=null) {

                //发送停止通知
                data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeNone;
                data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexNone;
                data.isStreamPush = false;
                data.audio1.isPushStream = false;
                data.video1.isPushStream = false;


                currentSegmentIndex = MAX_SEGMENT_END;
                data.liveStatus = EplayerLiveRoomLiveStatus.LiveRoomLiveStatusClose;

                if(bus!=null)
                    bus.post(new VideoAudioStatusEvent(data));
            }

        }
    }

    //从某一进度开始回看
    public void resumePlayback(long progressTime) {

        synchronized (lock) {
            if (!isPause)
                return;

            isPause = false;


            long progress = -1;
            if (progress == progressTime) return;
            progress = progressTime;


            progressTime = eplayerSessionInfo.progressCorrection(progressTime);

           //根据进度时间查找片段
            currentSegmentIndex = eplayerSessionInfo.loadIndexSegmentWithProgressTime(progressTime);

            if (progressTime >= totalPlaybackTime) {
                currentSegmentIndex = MAX_SEGMENT_END;
            }

            PlaybackSegment segment = eplayerSessionInfo.loadSegmentWithIndex(currentSegmentIndex);


            DrawPadInfo drawPadInfo = null;
            long seq =0;
            long time =0;

            if(segment!=null) {
                //片段存在，查询进度时间，开始时间，以及相对于片段开始时间的偏移时间
                progressPlaybackTime = eplayerSessionInfo.loadTimeSegmentWithIndex(currentSegmentIndex);

                currentSegmentTime = segment.playTime / 1000;

                  seq = eplayerSessionInfo.loadTimeWithProgressTime(progressTime);
                segment.seq=seq;
                //生成开始的真实时间
                currentPlaybackBeginTime = (segment.startTime + seq) / 1000;


                  time = segment.startTime + seq;


                //检查页面信息
                drawPadInfo = eplayerSessionInfo.loadDrawPadInfoWithTime(time, playbackBeginTime);

            }


            if (drawPadInfo != null) {

                if (!currentPadInfo.isAllSamePadInfo(drawPadInfo)) {
                    currentPadInfo = drawPadInfo;




                        String bank = "文档";
                        if (currentPadInfo.padType == EplayerDrawPadType.DrawPadTypeWhiteBoard) {
                            bank = "白板";
                        }
                        String scroll = "";
                        if (currentPadInfo.isWordScroll) {
                            scroll = "滚动";
                        }
                        LogUtil.d(String.format("------页面初始化------页码:%d---%d--%s--%s", drawPadInfo.page, drawPadInfo.seq, bank, scroll));

                    bus.post(new DrawPadInfoChangeEvent(drawPadInfo));
                }


                //查询出这个时间再这个页面上已经显示了的画笔信息
                PlaybackMsgInfo info = eplayerSessionInfo.loadPlaybackMsgInfoWithTime(time);

                if(info!=null) {
                    //查询画笔集合
                    List<String> keys = info.findMsg(time);

                    List<DrawMsgInfo> msgs = eplayerSessionInfo.loadDrawMsgInfoWithKeys(keys);
                    if (msgs.size() > 0) {
                        LogUtil.d(String.format("------新画笔信息------条数:%d", (int) msgs.size()));


                        bus.post(new DrawMsgInfoEvent(msgs));

                    }
                }
            }

            //重置画笔标记位置
            currentMsgTime = time;

            LiveRoomInfoData data = eplayerSessionInfo.infoData;
            data.currentPlaybackSegment=segment;
            data.liveStatus = EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay;


            if (segment!=null&&segment.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo) {
                //发送播放视频通知
                data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo;
                data.isStreamPush = true;

                LiveRoomVideo video = new LiveRoomVideo();
                video.isPushStream = true;
                video.playUrl = segment.playUrlWithStart(seq);
                data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;
                data.video1 = video;

            } else if (segment!=null&&segment.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio) {
                //发送播放音频通知

                data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio;
                data.isStreamPush = true;

                LiveRoomAudio audio = new LiveRoomAudio();
                audio.playUrl = segment.playUrlWithStart(seq);
                audio.isPushStream = true;
                data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;
                data.audio1 = audio;

            } else {
                //发送停止通知
                data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeNone;
                data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexNone;
                data.isStreamPush = false;
                data.audio1.isPushStream = false;
                data.video1.isPushStream = false;
                if (currentSegmentIndex == MAX_SEGMENT_END) {
                    data.liveStatus = EplayerLiveRoomLiveStatus.LiveRoomLiveStatusClose;
                }
            }

            bus.post(new VideoAudioStatusEvent(data));

            //恢复定时器
            this.startTimer();

            LogUtil.d("------setFireDate:[NSDate distantPast]------");

        }
    }

    public void resetPlayback(long progressTime) {

        synchronized (lock) {
            if (!isPause)
                return;

            isPause = false;


            this.resetPlaybackWithTime(progressTime);
        }

    }

    public boolean checkPlaybackPPtId(int pptId, int page) {


        String key = String.format("%010d-%010d", pptId, page);


        return eplayerSessionInfo.checkLoadTimeWithPage(key);
    }

    //切换页码
    public void resetPlaybackPPtId(int pptId, int page) {

        synchronized(lock) {
            if(!isPause)
                return;

            isPause =  false;
            //查找页码第一次显示对应的时间

            String key = String.format("%010d-%010d", pptId, page);

            //根据页码找到第一次操作时间
            long seq = eplayerSessionInfo.loadTimeWithPage(key);
            //根据时间重置回看
            this.resetPlaybackWithTime(seq);

            if(this.listener!=null){

                PlaybackSegment segment = eplayerSessionInfo.loadSegmentWithIndex(currentSegmentIndex);
                long   progressTime =   progressPlaybackTime+currentPlaybackBeginTime *1000- segment.startTime;
                this.listener.showProgressTime(progressTime,totalPlaybackTime,seq,playbackBeginTime,playbackEndTime);

            }

            previousTime = 0;
        }
    }

    //根据时间重置回看
    private void resetPlaybackWithTime(long time) {


        //检查页面信息
        DrawPadInfo drawPadInfo = eplayerSessionInfo.loadDrawPadInfoWithTime(time,playbackBeginTime);

        if (drawPadInfo!=null&&!currentPadInfo.isAllSamePadInfo(drawPadInfo)) {
            currentPadInfo = drawPadInfo;



                String bank = "文档";
                if (currentPadInfo.padType == EplayerDrawPadType.DrawPadTypeWhiteBoard) {
                    bank = "白板";
                }
                String scroll = "";
                if (currentPadInfo.isWordScroll) {
                    scroll = "滚动";
                }
                LogUtil.d(String.format("------页面初始化------页码:%d---%d--%s--%s", drawPadInfo.page, drawPadInfo.seq, bank, scroll));

            bus.post(new DrawPadInfoChangeEvent(drawPadInfo));
        }


        //查询出这个时间再这个页面上已经显示了的画笔信息
        PlaybackMsgInfo info = eplayerSessionInfo.loadPlaybackMsgInfoWithTime(time);

        if(info!=null) {
            List<String> keys = info.findMsg(time);

            List<DrawMsgInfo> msgs = eplayerSessionInfo.loadDrawMsgInfoWithKeys(keys);
            if (msgs.size() > 0) {
                LogUtil.d(String.format("------新画笔信息------条数:%d", (int) msgs.size()));


                bus.post(new DrawMsgInfoEvent(msgs));

            }
        }

        //重置画笔标记位置
        currentMsgTime = time;

        //重置片段位置
        if (time<playbackBeginTime) {
            currentSegmentIndex = 0;
        }else{
            currentSegmentIndex = eplayerSessionInfo.loadIndexSegmentWithTime(time);
        }

        PlaybackSegment segment = eplayerSessionInfo.loadSegmentWithIndex(currentSegmentIndex);

       //获取片段开始时间
        currentPlaybackBeginTime = segment.startTime/1000;

        //获取片段播放时间
        currentSegmentTime = segment.playTime/1000;
        long seq = time  - segment.startTime;

        LiveRoomInfoData data = eplayerSessionInfo.infoData;
        segment.seq=seq;
        data.currentPlaybackSegment=segment;
        data.liveStatus = EplayerLiveRoomLiveStatus.LiveRoomLiveStatusPlay;



        //累加之前片段的播放时间
        progressPlaybackTime = eplayerSessionInfo.loadTimeSegmentWithIndex(currentSegmentIndex);
        //开始播放的真实时间
        currentPlaybackBeginTime = (segment.startTime + seq) / 1000;


        if (segment.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo) {
            //发送播放视频通知
            data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo;
            data.isStreamPush = true;

            LiveRoomVideo video = new LiveRoomVideo();
            video.isPushStream = true;
            video.playUrl = segment.playUrlWithStart(seq);
            data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;
            data.video1 = video;

        } else if (segment.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio) {
            //发送播放音频通知

            data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio;
            data.isStreamPush = true;

            LiveRoomAudio audio = new LiveRoomAudio();
            audio.playUrl = segment.playUrlWithStart(seq);
            audio.isPushStream = true;
            data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexOne;
            data.audio1 = audio;

        } else {
            //发送停止通知
            data.streamType = EplayerLiveRoomStreamType.LiveRoomStreamTypeNone;
            data.streamIndex = EplayerLiveRoomStreamIndex.LiveRoomStreamIndexNone;
            data.isStreamPush = false;
            data.audio1.isPushStream = false;
            data.video1.isPushStream = false;
            if (currentSegmentIndex == MAX_SEGMENT_END) {
                data.liveStatus = EplayerLiveRoomLiveStatus.LiveRoomLiveStatusClose;
            }
        }

        bus.post(new VideoAudioStatusEvent(data));

        previousTime = 0;

        //恢复定时器
        this.startTimer();

    }

    //四色五入
    private long fixedNum(double num) {
        return (long) (num + 0.5f);
    }


    //TODO timerMethod
    public class TimerTaskMethod extends TimerTask {
        private long currentTimerToken;

        public TimerTaskMethod(long currentTimerToken) {
            this.currentTimerToken = currentTimerToken;
        }

        @Override
        public void run() {

            synchronized (lock) {
                try {

                    //定时器单例模式
                    if (PlaybackDriver.this.currentTimerToken != this.currentTimerToken) {
                        return;
                    }
                    //暂停立即停止
                    if (isPause)
                        return;

                    //最后一个片段，停止
                    if (currentSegmentIndex == MAX_SEGMENT_END) {
                        PlaybackDriver.this.getListener().stopPlaybackEngin();

                        PlaybackDriver.this.closePlayback();

                        return;
                    }

                    PlaybackSegment segment = eplayerSessionInfo.loadSegmentWithIndex(currentSegmentIndex);

                    // 生成实际的进度 （时间）
                    double playTime = 0;

                    //判断视频是否结束
                    boolean playerDone = PlaybackDriver.this.getListener().playDoneIfHasMedia();

                    if (playerDone) {
                        //将当前时间直接置为片段长度，表示片段结束
                        playTime =  segment.playTime/1000.0;
                    } else {
                        //获取视频已播放时长
                        playTime = PlaybackDriver.this.getListener().playTimeIfHasMedia()/1000;
                    }


                    if (playTime < 0)
                        playTime = 0;
                    //貌似此算法可以删除，不过为了保持跟ios的逻辑完全一致，暂时保留
                    long fixedTime = PlaybackDriver.this.fixedNum(playTime);

                    // 生成实际的进度 （时间）
                    // 生成实际的进度 （时间）
                    long   progressTime = fixedTime* 1000 + currentPlaybackBeginTime * 1000;
                    // 生成实际的进度 （值）
                    long  progressValue = progressTime - playbackBeginTime;


                    // 是不是进入下一个片段
                    int segmentIndex = eplayerSessionInfo.loadIndexSegmentWithProgressTime(progressValue+1000);


                    LogUtil.d("playerDone:"+playerDone+";progressTime:"+progressTime+";progressValue:"+progressValue+";fixedTime:"+fixedTime+";currentSegmentTime:"+currentSegmentTime);


                    //片段已播放时间大于等于片段时长
                    if(segmentIndex != -1 && segmentIndex != currentSegmentIndex){
                        //查找下一个片段，找不到就结束
                        if (!PlaybackDriver.this.resetSegmentWithIndex(currentSegmentIndex + 1)) {
                            PlaybackDriver.this.getListener().stopPlaybackEngin();

                            PlaybackDriver.this.getListener().showProgressTime(totalPlaybackTime, totalPlaybackTime, playbackEndTime, playbackBeginTime, playbackEndTime);

                            PlaybackDriver.this.closePlayback();

                            return;

                        }

                    }

                   //定时器两次处理的播放时间一样，不再后续处理
                    if (previousTime == fixedTime)
                        return;

                    previousTime = fixedTime;

                    //生成实际时间
                    long showTime = fixedTime + currentPlaybackBeginTime;





                        String type = "无音视频";

                        if(segment!=null) {

                            if (segment.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeVideo) {
                                type = "视频";
                            } else if (segment.type == EplayerLiveRoomStreamType.LiveRoomStreamTypeAudio) {
                                type = "音频";
                            }

                        }

                        String infoTime = "";
                        if (segment!=null&&segment.info != null) {
                            PlayList info = segment.info;

                            String infoStartTime = DateUtil.getString(new Date(info.startTime));
                            String infoEndTime = DateUtil.getString(new Date(info.endTime));

                            infoTime = String.format("%s--%s", infoStartTime, infoEndTime);
                        }
                        String showTimeStr = DateUtil.getString(new Date(showTime * 1000));

                        LogUtil.d(String.format("------时间驱动------时间:%s---片段序:%d--%s--%s", showTimeStr, currentSegmentIndex, type, infoTime));


                    long datausetime = showTime * 1000;

                    //检查页面信息
                    DrawPadInfo drawPadInfo = eplayerSessionInfo.loadDrawPadInfoWithTime(datausetime,playbackBeginTime);

                    //页面变化，发通知
                    if (drawPadInfo!=null&&!currentPadInfo.isAllSamePadInfo(drawPadInfo)) {
                        currentPadInfo = drawPadInfo;



                            String bank = "文档";
                            if (currentPadInfo.padType == EplayerDrawPadType.DrawPadTypeWhiteBoard) {
                                bank = "白板";
                            }
                            String scroll = "";
                            if (currentPadInfo.isWordScroll) {
                                scroll = "滚动";
                            }
                            LogUtil.d(String.format("------页面初始化------页码:%d---%d--%s--%s", drawPadInfo.page, drawPadInfo.seq, bank, scroll));

                        bus.post(new DrawPadInfoChangeEvent(drawPadInfo));
                    }


                    //检查画笔信息变化

                    List<DrawMsgInfo> msgs = eplayerSessionInfo.loadDrawMsgInfoWithStartTime(currentMsgTime, datausetime);
                    if (msgs.size() > 0) {

                        LogUtil.d(String.format("------新画笔信息------条数:%d", (int) msgs.size()));

                        bus.post(new DrawMsgInfoEvent(msgs));

                    }

                    currentMsgTime = datausetime;

                    PlaybackDriver.this.getListener().showProgressTime(progressValue, totalPlaybackTime, datausetime, playbackBeginTime, playbackEndTime);

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d("TimerTaskMethod 信息异常", e.toString());
                }

            }
        }
    }
}

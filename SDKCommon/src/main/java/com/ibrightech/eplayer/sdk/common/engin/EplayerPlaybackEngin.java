package com.ibrightech.eplayer.sdk.common.engin;

import com.ibrightech.eplayer.sdk.common.entity.playback.PlaybackDriver;
import com.ibrightech.eplayer.sdk.common.entity.session.EPlaybackSessionInfo;
import com.ibrightech.eplayer.sdk.common.loading.playback.EplayerPlaybackLoading;

public class EplayerPlaybackEngin extends EplayerEngin {

    private PlaybackDriver playbackDriver;


    private PlaybackDriver.OnEnginListener listener;

    public PlaybackDriver.OnEnginListener getListener() {
        return listener;
    }

    public void setListener(PlaybackDriver.OnEnginListener listener) {
        this.listener = listener;
    }




    public EPlaybackSessionInfo getSessionInfo() {
        return (EPlaybackSessionInfo) sessionInfo;
    }

    @Override
    public void init() {
        if (!bus.isRegistered(this)) {
            bus.register(this);
        }
        sessionInfo = new EPlaybackSessionInfo();
        eplayerLoading = new EplayerPlaybackLoading(context, ePlayerData, getSessionInfo());

    }

    @Override
    public void distory() {
        if (bus.isRegistered(this)) {
            bus.unregister(this);
        }
        playbackDriver.closePlayback();
        sessionInfo.clearALL();
        sessionInfo = null;
        playbackDriver = null;

    }


    @Override
    public void startLoading() {
        eplayerLoading.startLoading();
    }

    @Override
    public void cancelLoading() {
        eplayerLoading.cancelLoading();
    }

    //开启回看的定时器回调
    @Override
    public void startClassEngin() {
        if (playbackDriver != null) {
            playbackDriver.closePlayback();
            playbackDriver.setListener(null);
            playbackDriver = null;
        }
        playbackDriver = new PlaybackDriver((EPlaybackSessionInfo) sessionInfo);
        playbackDriver.setListener(listener);
        playbackDriver.startPlayback();
    }

    //关闭回看的定时器回调
    @Override
    public void finishClassEngin() {
        if (playbackDriver != null) {
            playbackDriver.closePlayback();
            playbackDriver.setListener(null);
            playbackDriver = null;
        }
    }

    //******************
    //下面的方法是重调用的playbackDriver




    public void resetNextSegment() {
        playbackDriver.resetNextSegment();


    }

    //从某片段开始
    public boolean resetSegmentWithIndex(int index) {
        return playbackDriver.resetSegmentWithIndex(index);
    }

    public void resetSegmentWithTime(long time) {
        playbackDriver.resetSegmentWithTime(time);
    }


    public void startPlayback() {
        playbackDriver.startPlayback();
    }


    public void pausePlayback() {
        playbackDriver.pausePlayback();
    }

    public void resumePlayback() {
        playbackDriver.resumePlayback();
    }

    //查找下一页
    public int nextPagePPtId(int pptId, int currentPage) {
        return playbackDriver.nextPagePPtId(pptId, currentPage);

    }

    //查找上一页
    public int prePagePPtId(int pptId, int currentPage) {
        return playbackDriver.prePagePPtId(pptId, currentPage);
    }

    //关闭回看
    public void closePlayback() {
        playbackDriver.closePlayback();
    }

    //从某一进度开始回看
    public void resumePlayback(long progressTime) {
        playbackDriver.resumePlayback(progressTime);
    }

    public void resetPlayback(long progressTime) {
        playbackDriver.resetPlayback(progressTime);

    }

    public boolean checkPlaybackPPtId(int pptId, int page) {
        return playbackDriver.checkPlaybackPPtId(pptId, page);

    }

    //切换页码
    public void resetPlaybackPPtId(int pptId, int page) {
        playbackDriver.resetPlaybackPPtId(pptId, page);
    }
}

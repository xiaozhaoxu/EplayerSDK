package com.ibrightech.eplayer.sdk.common.entity.playback;


import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.util.DateUtil;
import com.ibrightech.eplayer.sdk.common.util.LogUtil;

/**
 * Created by junhai on 14-11-20.
 */
public class PlaybackSegment {

    public EplayerLiveRoomStreamType type;

    public long startTime;
    public long endTime;

    public PlayList info;

    public long playTime;

    public long seq=0;

    public long allSegmentStartTime=0;//有可能当段视频是由一整个视频切成多个的，此处记录下整个视频的开始时间

    public void loadDone() {
        this.playTime = this.endTime - this.startTime;
    }

    public String playUrl() {
        long start = this.startTime - this.info.startTime;
        if (start < 0)
            start = 0;


            String start1 = DateUtil.getString(this.startTime);
            String end1 = DateUtil.getString(this.endTime);

            String start2 = DateUtil.getString(this.info.startTime);
            String end2 = DateUtil.getString(this.info.endTime);
            LogUtil.d(start1 + " -- " + end1 + "  |||  " + start2 + " -- " + end2);



        return this.info.playUrl(start / 1000, (this.playTime + start) / 1000);
    }

    public String playUrlWithStart(long start) {
        if (start < 0)
            start = 0;

        long realstart = this.startTime - this.info.startTime + start;
        if (realstart < 0)
            realstart = 0;


            String start1 = DateUtil.getString(this.startTime);
            String end1 = DateUtil.getString(this.endTime);

            String start2 = DateUtil.getString(this.info.startTime);
            String end2 = DateUtil.getString(this.info.endTime);
            LogUtil.d(start1 + " -- " + end1 + "  |||  " + start2 + " -- " + end2);


        return this.info.playUrl(realstart / 1000, (this.playTime + this.startTime - this.info.startTime) / 1000);
    }

}

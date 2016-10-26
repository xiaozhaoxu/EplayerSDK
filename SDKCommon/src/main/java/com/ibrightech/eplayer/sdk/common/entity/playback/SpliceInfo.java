package com.ibrightech.eplayer.sdk.common.entity.playback;


import com.ibrightech.eplayer.sdk.common.config.EplayerSetting;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomStreamType;
import com.ibrightech.eplayer.sdk.common.util.HeaderUtil;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;

/**
 * Created by zhaoxu2014 on 15-1-29.
 */
public class SpliceInfo {
    public long interval;
    public long timestamp;
    public long length;
    public boolean zero4Thirteenth;
    public long endTime;
    public long seq;
    public EplayerLiveRoomStreamType type;

    public int indexSize;

    public int startIndex;   //初始播放index位置

    public String suffix;
    public String endfiletime;

    public String palyurl="";//这个是自己创建的，为的是给播放器传一个无用的播放地址

    public PlaySplice getPlaySplice(int index){
        String baseUrl= EplayerSetting.getInstance().spliceVideoPlayBaseUrl;
        switch (type){
            case LiveRoomStreamTypeVideo:
                baseUrl=EplayerSetting.getInstance().spliceVideoPlayBaseUrl;
                break;
            case LiveRoomStreamTypeAudio:
                baseUrl=EplayerSetting.getInstance().spliceAudioPlayBaseUrl;
                break;
        }

        if(index==0){
            if(this.zero4Thirteenth) {
                PlaySplice playSplice = new PlaySplice();
                playSplice.start = -1;
                playSplice.end = -1;
                String pathTemp = this.suffix + "/" + this.suffix + "-" + this.endfiletime + "/" + 0 + "-" + this.timestamp + ".flv";
                String locationPathTemp = this.suffix + "/" + this.suffix + "-" + this.endfiletime + "/" + 0 + "-" + this.timestamp + "_" + System.currentTimeMillis() + ".flv";
                playSplice.url = baseUrl + pathTemp;
                playSplice.path = StorageUtil.getStorageDirectory() + "/" + locationPathTemp;
                return playSplice;
            }else{
                PlaySplice playSplice = new PlaySplice();
                playSplice.needdownload=false;
                playSplice.path= HeaderUtil.getHeaderPath();
                return playSplice;
            }
        }else{
            PlaySplice playSplice = new PlaySplice();
            String pathTemp=this.suffix + "/" + this.suffix + "-" + this.endfiletime + "/" + (startIndex+index+1)+ "-" + this.timestamp + ".flv";
            String locationPathTemp=this.suffix + "/" + this.suffix + "-" + this.endfiletime + "/" + (startIndex+index+1)+ "-" + this.timestamp +"_"+System.currentTimeMillis() + ".flv";
            playSplice.url=baseUrl +pathTemp;
            playSplice.path= StorageUtil.getStorageDirectory() + "/" + locationPathTemp;
            return playSplice;
        }
    }
}

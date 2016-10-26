package com.ibrightech.eplayer.sdk.common.net.ws.event;


import com.ibrightech.eplayer.sdk.common.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;

/**
 * Created by junhai on 14-8-13.
 */
public class VideoAudioStatusEvent {
    private LiveRoomInfoData infoData;

    public LiveRoomInfoData getInfoData() {
        return infoData;
    }

    public void setInfoData(LiveRoomInfoData infoData) {
        this.infoData = infoData;
    }

    public VideoAudioStatusEvent(LiveRoomInfoData infoData) {
        this.infoData = infoData;
    }
}

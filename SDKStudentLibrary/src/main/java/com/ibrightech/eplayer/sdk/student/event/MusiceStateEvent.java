package com.ibrightech.eplayer.sdk.student.event;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerLiveRoomLiveStatus;

/**
 * Created by zhaoxu2014 on 16/9/2.
 */
public class MusiceStateEvent {
    EplayerLiveRoomLiveStatus status;

    public MusiceStateEvent(EplayerLiveRoomLiveStatus status) {
        this.status = status;
    }

    public EplayerLiveRoomLiveStatus getStatus() {
        return status;
    }
}

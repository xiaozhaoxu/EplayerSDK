package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerLiveRoomStreamType {
    LiveRoomStreamTypeNone(0), LiveRoomStreamTypeVideo(1), LiveRoomStreamTypeAudio(2);
    private int _value;

    private EplayerLiveRoomStreamType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
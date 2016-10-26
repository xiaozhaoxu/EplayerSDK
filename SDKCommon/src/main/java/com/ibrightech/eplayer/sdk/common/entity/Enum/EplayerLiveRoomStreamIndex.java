package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerLiveRoomStreamIndex {
    LiveRoomStreamIndexNone(0), LiveRoomStreamIndexOne(1), LiveRoomStreamIndexTwo(2), LiveRoomStreamIndexThree(3);
    private int _value;

    private EplayerLiveRoomStreamIndex(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
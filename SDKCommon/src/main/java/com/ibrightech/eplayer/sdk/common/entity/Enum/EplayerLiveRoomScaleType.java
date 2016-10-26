package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerLiveRoomScaleType {
    LiveRoomScaleTypeSmall(1),  LiveRoomScaleTypeBig(3);
    private int _value;

    private EplayerLiveRoomScaleType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }

    public static EplayerLiveRoomScaleType getEnumBykey(int key){
        if(key==LiveRoomScaleTypeSmall._value){
            return LiveRoomScaleTypeSmall;
        }else  if(key==LiveRoomScaleTypeBig._value){
            return LiveRoomScaleTypeBig;
        }else {
            return LiveRoomScaleTypeBig;
        }
    }
}
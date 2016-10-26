package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerLiveRoomLiveModeType {
    LiveRoomLiveModeTypeNormal(1), LiveRoomLiveModeTypeBig(2), LiveRoomLiveModeTypeOneToOne(3);
    private int _value;

    private EplayerLiveRoomLiveModeType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }

    public static EplayerLiveRoomLiveModeType getEnumBykey(int key){
        if(key==LiveRoomLiveModeTypeNormal._value){
            return LiveRoomLiveModeTypeNormal;
        }else  if(key==LiveRoomLiveModeTypeBig._value){
            return LiveRoomLiveModeTypeBig;
        }else  if(key==LiveRoomLiveModeTypeOneToOne._value){
            return LiveRoomLiveModeTypeOneToOne;
        }else {
            return LiveRoomLiveModeTypeOneToOne;
        }
    }
}

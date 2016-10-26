package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerLiveRoomLayoutType {
    LiveRoomLayoutTypePPT(1), LiveRoomLayoutTypeVideo(2);
    private int _value;

    private EplayerLiveRoomLayoutType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }

    public static EplayerLiveRoomLayoutType getEnumBykey(int key){
        if(key==LiveRoomLayoutTypePPT._value){
            return LiveRoomLayoutTypePPT;
        }else  if(key==LiveRoomLayoutTypeVideo._value){
            return LiveRoomLayoutTypeVideo;
        }else {
            return LiveRoomLayoutTypeVideo;
        }
    }
}

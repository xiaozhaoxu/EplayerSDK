package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerLiveRoomMainVideoType {
    LiveRoomMainVideoTypeTeacher(1), LiveRoomMainVideoTypeAssistant(2), LiveRoomMainVideoTypeStudent(3);
    private int _value;

    private EplayerLiveRoomMainVideoType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }

    public static EplayerLiveRoomMainVideoType getEnumBykey(int key){
        if(key==LiveRoomMainVideoTypeTeacher._value){
            return LiveRoomMainVideoTypeTeacher;
        }else  if(key==LiveRoomMainVideoTypeAssistant._value){
            return LiveRoomMainVideoTypeAssistant;
        }else {
            return LiveRoomMainVideoTypeStudent;
        }
    }
}


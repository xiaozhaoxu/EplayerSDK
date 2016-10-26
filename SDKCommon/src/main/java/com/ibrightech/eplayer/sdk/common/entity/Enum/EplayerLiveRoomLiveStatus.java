package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerLiveRoomLiveStatus {
    LiveRoomLiveStatusStop(0, "未开始"), LiveRoomLiveStatusPlay(1, "直播中"), LiveRoomLiveStatusPause(2, "暂停中"), LiveRoomLiveStatusClose(3,"已关闭");
    private int _value;
    public String desc;
    private EplayerLiveRoomLiveStatus(int value,String desc) {
        _value = value;
        this.desc=desc;
    }

    public int value() {
        return _value;
    }

    public static EplayerLiveRoomLiveStatus getEnumByKey(int userType){
        if(userType==LiveRoomLiveStatusStop._value){
            return LiveRoomLiveStatusStop;
        }else if(userType==LiveRoomLiveStatusPlay._value){
            return LiveRoomLiveStatusPlay;
        }else if(userType==LiveRoomLiveStatusPause._value){
            return LiveRoomLiveStatusPause;
        }else if(userType==LiveRoomLiveStatusClose._value){
            return LiveRoomLiveStatusClose;
        }else{
            return LiveRoomLiveStatusStop;
        }


    }


}

package com.ibrightech.eplayer.sdk.common.entity.Enum;


public enum EplayerMessageChatType {
    MessageChatTypeSys(1), MessageChatTypeMsg(2), MessageChatTypeAsk(3),
    MessageChatTypeReward(4)//赏
    ;
    private int _value;

    private EplayerMessageChatType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }

    public static EplayerMessageChatType getEnumByKey(int userType){
        if(userType==MessageChatTypeSys._value){
            return MessageChatTypeSys;
        }else if(userType==MessageChatTypeMsg._value){
            return MessageChatTypeMsg;
        }else if(userType==MessageChatTypeAsk._value){
            return MessageChatTypeAsk;
        }else if(userType==MessageChatTypeReward._value){
            return MessageChatTypeReward;
        }else{
            return MessageChatTypeSys;
        }


    }
}
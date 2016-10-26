package com.ibrightech.eplayer.sdk.common.entity.Enum;


public enum EplayerVoteType {
    //对于voteType，类型1对应的选择题选项为A/B/C/D,类型2对应的选择题题型为1/2/3/4，
    // 类型3对应的选择题题型为对/错，类型4对应的选择题题型为YES/NO，类型5对应的选择题题型为听明白了/没听明白

    VoteType1(1), VoteType2(2), VoteType3(3), VoteType4(4), VoteType5(5);
    private int _value;

    private EplayerVoteType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }

    public static EplayerVoteType getEnumBykey(int key){
        if(key==VoteType1._value){
            return VoteType1;
        }else  if(key==VoteType2._value){
            return VoteType2;
        }else  if(key==VoteType3._value){
            return VoteType3;
        }else  if(key==VoteType4._value){
            return VoteType4;
        }else  if(key==VoteType5._value){
            return VoteType5;
        }else {
            return VoteType5;
        }
    }
}


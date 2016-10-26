package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EplayerUserInfoValidateType {
    UserInfoValidateTypeMonitor(-1), UserInfoValidateTypeFree(0), UserInfoValidateTypeThirdStudent(1), UserInfoValidateTypeThirdTeacher(2), UserInfoValidateTypeUserPasswordStudent(3), UserInfoValidateTypeUserPasswordTeacher(4), UserInfoValidateTypeToken(5);
    private int _value;

    private EplayerUserInfoValidateType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }


    public static EplayerUserInfoValidateType getEnumByKey(int userType){
        if(userType==UserInfoValidateTypeMonitor._value){
            return UserInfoValidateTypeMonitor;
        }else if(userType==UserInfoValidateTypeFree._value){
            return UserInfoValidateTypeFree;
        }else if(userType==UserInfoValidateTypeThirdStudent._value){
            return UserInfoValidateTypeThirdStudent;
        }else if(userType==UserInfoValidateTypeThirdTeacher._value){
            return UserInfoValidateTypeThirdTeacher;
        }else if(userType==UserInfoValidateTypeUserPasswordStudent._value){
            return UserInfoValidateTypeUserPasswordStudent;
        }
        else if(userType==UserInfoValidateTypeUserPasswordTeacher._value){
            return UserInfoValidateTypeUserPasswordTeacher;
        }else if(userType==UserInfoValidateTypeToken._value){
            return UserInfoValidateTypeToken;
        }else{
            return UserInfoValidateTypeToken;
        }


    }
}

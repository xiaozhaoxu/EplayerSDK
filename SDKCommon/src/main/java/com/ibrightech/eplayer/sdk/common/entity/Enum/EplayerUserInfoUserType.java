package com.ibrightech.eplayer.sdk.common.entity.Enum;


public enum EplayerUserInfoUserType {
    UserInfoUserTypeStudent(1), UserInfoUserTypeTeacher(2), UserInfoUserTypeAssist(3), UserInfoUserTypeSuperAdmin(4), UserInfoUserTypeCustomAdmin(5), UserInfoUserTypeVisitor(6);
    private int _value;

    private EplayerUserInfoUserType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }


    public static EplayerUserInfoUserType getEnumByKey(int userType){
        if(userType==UserInfoUserTypeStudent._value){
            return UserInfoUserTypeStudent;
        }else if(userType==UserInfoUserTypeTeacher._value){
            return UserInfoUserTypeTeacher;
        }else if(userType==UserInfoUserTypeAssist._value){
            return UserInfoUserTypeAssist;
        }else if(userType==UserInfoUserTypeSuperAdmin._value){
            return UserInfoUserTypeSuperAdmin;
        }else if(userType==UserInfoUserTypeVisitor._value){
            return UserInfoUserTypeVisitor;
        }else{
            return UserInfoUserTypeVisitor;
        }


    }
}
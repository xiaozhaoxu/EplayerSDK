package com.ibrightech.eplayer.sdk.common.entity.Enum;



public enum EPlayerLoginType {
    EPlayerLoginTypeNone(0), EPlayerLoginTypeUserPwd(1), EPlayerLoginTypeAuthReverse(2), EPlayerLoginTypeAuthForward(3);
    private int _value;

    private EPlayerLoginType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
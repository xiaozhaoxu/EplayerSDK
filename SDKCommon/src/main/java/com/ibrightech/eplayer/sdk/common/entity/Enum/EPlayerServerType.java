package com.ibrightech.eplayer.sdk.common.entity.Enum;



public enum EPlayerServerType {
    EPlayerServerTypePublish(0), //正式服务器
    EPlayerServerTypeTest(1);   //测试服务器
    private int _value;

    private EPlayerServerType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
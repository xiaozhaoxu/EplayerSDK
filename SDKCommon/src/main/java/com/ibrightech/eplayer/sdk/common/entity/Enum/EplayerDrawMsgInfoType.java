package com.ibrightech.eplayer.sdk.common.entity.Enum;


public enum EplayerDrawMsgInfoType {
    DrawMsgInfoTypeLine(1), DrawMsgInfoTypeClear(2), DrawMsgInfoTypeEraser(6), DrawMsgInfoTypeText(7);
    private int _value;

    private EplayerDrawMsgInfoType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
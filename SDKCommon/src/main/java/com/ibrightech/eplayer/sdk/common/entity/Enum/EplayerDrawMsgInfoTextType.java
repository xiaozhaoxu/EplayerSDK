package com.ibrightech.eplayer.sdk.common.entity.Enum;


public enum EplayerDrawMsgInfoTextType {
    DrawMsgInfoTextTypeShow(1), DrawMsgInfoTextTypeDelete(2), DrawMsgInfoTextTypePoint(3), DrawMsgInfoTextTypeSize(4), DrawMsgInfoTextTypeColor(5);
    private int _value;

    private EplayerDrawMsgInfoTextType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
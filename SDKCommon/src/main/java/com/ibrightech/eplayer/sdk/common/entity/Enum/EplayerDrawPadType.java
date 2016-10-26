package com.ibrightech.eplayer.sdk.common.entity.Enum;


public enum EplayerDrawPadType {
    DrawPadTypeDocument(0), DrawPadTypeWhiteBoard(1);
    private int _value;

    private EplayerDrawPadType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
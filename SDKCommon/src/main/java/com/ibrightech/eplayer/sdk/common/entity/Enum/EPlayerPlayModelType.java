package com.ibrightech.eplayer.sdk.common.entity.Enum;

public enum EPlayerPlayModelType {
    EPlayerPlayModelTypelLive(0), EPlayerPlayModelTypePlayback(1);
    private int _value;

    private EPlayerPlayModelType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
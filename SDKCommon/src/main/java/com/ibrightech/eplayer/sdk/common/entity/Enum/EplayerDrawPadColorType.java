package com.ibrightech.eplayer.sdk.common.entity.Enum;


public enum EplayerDrawPadColorType {
    DrawPadColorTypeWhite(1), DrawPadColorTypeGreen(2), DrawPadColorTypeBlack(3);
    private int _value;

    private EplayerDrawPadColorType(int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }

    public static EplayerDrawPadColorType getEnumBykey(int key){
        if(key==DrawPadColorTypeWhite._value){
            return DrawPadColorTypeWhite;
        }else  if(key==DrawPadColorTypeGreen._value){
            return DrawPadColorTypeGreen;
        }else  if(key==DrawPadColorTypeBlack._value){
            return DrawPadColorTypeBlack;
        }else {
            return DrawPadColorTypeBlack;
        }
    }

}


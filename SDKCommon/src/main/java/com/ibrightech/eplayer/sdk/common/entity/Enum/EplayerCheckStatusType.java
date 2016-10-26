package com.ibrightech.eplayer.sdk.common.entity.Enum;


public enum EplayerCheckStatusType {
    TYPE_SAVE("0","保存成功"),
    TYPE_FAIL("5","转换失败"),
    TYPE_COPYFAIL("7","复制文件失败"),
    TYPE_SUCESS("8","转换完毕"),
    TYPE_PROCESSING("9","正在转换");

    private String _key;
    private String _value;

    private EplayerCheckStatusType(String key,
                                   String value) {
        this._key=key;
        this._value = value;
    }

    public String value() {
        return _value;
    }

    public String get_key() {
        return _key;
    }

    public  static EplayerCheckStatusType getCheckStatusTypeByKey(String key){

        if(TYPE_SAVE._key.equalsIgnoreCase(key)){
            return TYPE_SAVE;
        }else if(TYPE_COPYFAIL._key.equalsIgnoreCase(key)){
            return TYPE_COPYFAIL;
        }else if(TYPE_SUCESS._key.equalsIgnoreCase(key)){
            return TYPE_SUCESS;
        }else if(TYPE_PROCESSING._key.equalsIgnoreCase(key)){
            return TYPE_PROCESSING;
        }else{
            return TYPE_FAIL;
        }
    }
}
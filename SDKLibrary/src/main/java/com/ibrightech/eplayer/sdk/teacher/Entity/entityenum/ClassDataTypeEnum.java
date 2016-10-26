package com.ibrightech.eplayer.sdk.teacher.Entity.entityenum;

/**
 * Created by zhaoxu2014 on 16/5/23.
 */
public enum ClassDataTypeEnum {
    COURSE_Type("COURSE", "线下课"),
    ZHIBO_Type("ZHIBO", "直播课"),
    DIANBO_Type("DIANBO", "点播课"),
    COUPON_Type("COUPON", "优惠券"),
    YYTYK_Type("YYTYK", "一元体验课");

    private String _key;
    private String _value;

    private ClassDataTypeEnum(String key, String value) {
        this._key = key;
        this._value = value;
    }

    public String get_key() {
        return _key;
    }

    public String get_value() {
        return _value;
    }

    public static ClassDataTypeEnum getClassDataNameByKey(String key) {
        if (COURSE_Type._key.equalsIgnoreCase(key)) {
            return COURSE_Type;
        } else if (ZHIBO_Type._key.equalsIgnoreCase(key)) {
            return ZHIBO_Type;
        } else if (DIANBO_Type._key.equalsIgnoreCase(key)) {
            return DIANBO_Type;
        } else if (COUPON_Type._key.equalsIgnoreCase(key)) {
            return COUPON_Type;
        } else if (YYTYK_Type._key.equalsIgnoreCase(key)) {
            return YYTYK_Type;
        } else {
            return COURSE_Type;
        }


    }
}

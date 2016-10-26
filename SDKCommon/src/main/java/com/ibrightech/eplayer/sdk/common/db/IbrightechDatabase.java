package com.ibrightech.eplayer.sdk.common.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = IbrightechDatabase.NAME, version = IbrightechDatabase.VERSION)
public class IbrightechDatabase {
    //数据库名称
    public static final String NAME = "ibrightech_db";
    //数据库版本号
    public static final int VERSION = 1;
}
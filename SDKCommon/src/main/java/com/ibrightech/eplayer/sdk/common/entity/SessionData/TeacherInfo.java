package com.ibrightech.eplayer.sdk.common.entity.SessionData;

/**
 * Created by zhaoxu2014 on 14-12-8.
 */
public class TeacherInfo {
    public String name;
    public String headImg;
    public String _id;

    public TeacherInfo( String _id,String name, String headImg) {
        this._id=_id;
        this.name = name;
        this.headImg = headImg;
    }
}

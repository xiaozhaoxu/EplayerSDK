package com.ibrightech.eplayer.sdk.teacher.net;


/**
 * Created by yrhr on 2015/12/3.
 */
public class UrlConstant {
    //------------------------------地址信息----------------------------------------------------------------------

    //-----正式环境-------
    public static String businessHost = "http://apiv1.unixue.com";// "http://www.unixue.com";//业务系统地址
    public static String studyHost = "http://cas.unixue.com";//用户子系统地址


    //-----测试环境-------
//    public static String businessHost = "http://test1.upuday.com:9887";//测试环境业务系统地址
//    public static String studyHost = "http://cas.upuday.com";//用户子系统地址



    //------------------------------接口地址----------------------------------------------------------------------

    public static String LOGIN = "/pub/login";
    public static String LOGOUT =  "/api/user/logout";//退出登录


    public static String LOGIN_DOMAIN =  "/api/teacher/domain";

    public static String USERINFO =  "/api/user/info";//获取用户信息 这个是显示系统用的
    public static String TEACHER_ZHIBO = "/api/teacher/zhibo";//直播列表
    public static String TEACHER_SECTION = "/api/teacher/section";//
    public static String UPDATE_PWD = studyHost + "/api/user/modifyPwd";

    public static String FEED_BACK = businessHost+"/api/comment/feedback";//用户反馈
    public static String UPLOAD = "http://img.unixue.com/doPut?";
    //切换到测试地址上
    public  static  void isTest(){
        businessHost = "http://test1.upuday.com:9887";//测试环境业务系统地址
        studyHost = "http://cas.upuday.com";//用户子系统地址
    }

    public static String getUrlInStudyHost(String suffixUrl){

        return studyHost+suffixUrl;
    }
    public static String getUrlInBusinessHost(String suffixUrl){

        return businessHost+suffixUrl;
    }
}

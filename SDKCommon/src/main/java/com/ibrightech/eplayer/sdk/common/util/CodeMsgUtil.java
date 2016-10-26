package com.ibrightech.eplayer.sdk.common.util;

/**
 * Created by zhaoxu2014 on 16/8/25.
 */
public class CodeMsgUtil {
    public static final int UnixueStatusCodeSuccess = 200;// 处理成功
    public static final int UnixueStatusCodeFail = 1001; // 处理失败
    public static final int UnixueStatusCodeParamInComplete = 1002; // 参数不完整
    public static final int UnixueStatusCodeWrongPass = 2001;// 用户密码错误
    public static final int UnixueStatusCodeNotExist = 2002; // 用户不存在
    public static final int UnixueStatusCodeExist = 2003;// 用户已存在
    public static final int UnixueStatusCodeNoPermission = 2004;// 用户无权限
    public static final int UnixueStatusCodeVerifCodeError = 2005;// 验证码错误
    public static final int UnixueStatusCodeInsufficientBalance = 3001;// 余额不足
    public static final int UnixueStatusCodeDoubleCharge = 3002;// 该交易重复扣款

    public static final int UnixueStatusCodeUserOtherLogin = 999;// 用户在另一个设备上登录
    public static final int UnixueStatusCodeUserTokenFailure = 9999;// 用户系统token失效
    public static final int UnixueStatusCodeBSGetTokenFailure = 302;// 业务系统get状态 token失效
    public static final int UnixueStatusCodeBSPostTokenFailure = 403;// 业务系统post状态 token失效
    public static final int UnixueStatusCodeTokenFailure = 401;// token失效
    public static final int UNIXUEPLAYVIDEOFAILURE = 402;// 没有购买课程


    public static final int UnixueStatusCodeRepeatCreate = 400;// 重复创建订单

    /*
      判断token是否有效
     */
    public static boolean tokenInvalid(int code){
        if(code==UnixueStatusCodeUserOtherLogin
                ||code==UnixueStatusCodeUserTokenFailure
                ||code==UnixueStatusCodeBSGetTokenFailure
                || code == UnixueStatusCodeBSPostTokenFailure
                || code == UnixueStatusCodeTokenFailure) {
            return true;
        }
        return false;
    }

    public static boolean tokenInvalidByString(String msg){
        if(msg.contains(UnixueStatusCodeUserOtherLogin+"")
                ||msg.contains(UnixueStatusCodeUserTokenFailure+"")
                ||msg.contains(UnixueStatusCodeBSGetTokenFailure+"")
                ||msg.contains(UnixueStatusCodeBSPostTokenFailure+"")
                ||msg.contains(UnixueStatusCodeTokenFailure+"")){
            return true;
        }
        return false;

    }

    public static String getMsgByVideoCode(int code) {
        String msg = "";
        switch (code) {
            case UnixueStatusCodeTokenFailure: {
                msg = "请登陆后观看";
                break;
            }
            case UNIXUEPLAYVIDEOFAILURE: {
                msg = "请购买后观看";
                break;
            }
        }
        return msg;
    }

    public static String getMsgByCode(int code) {
        String msg = "";
        switch (code) {
            case UnixueStatusCodeSuccess:{
                msg = "处理成功";
                break;
            }
            case UnixueStatusCodeFail:{
                msg = "处理失败";
                break;
            }
            case UnixueStatusCodeParamInComplete:{
                msg = "参数不完整";
                break;
            }
            case UnixueStatusCodeWrongPass:{
                msg = "用户密码错误";
                break;
            }
            case UnixueStatusCodeNotExist:{
                msg = "用户不存在";
                break;
            }
            case UnixueStatusCodeExist:{
                msg = "用户已存在";
                break;
            }
            case UnixueStatusCodeNoPermission:{
                msg = "用户无权限";
                break;
            }
            case UnixueStatusCodeVerifCodeError:{
                msg = "验证码错误";
                break;
            }
            case UnixueStatusCodeInsufficientBalance:{
                msg = "余额不足";
                break;
            }
            case UnixueStatusCodeDoubleCharge:{
                msg = "该交易重复扣款";
                break;
            }
            case UnixueStatusCodeUserOtherLogin:{
                msg = "您的帐号在其他设备上登录，\r如非本人操作请立即修改密码";
                break;
            }
            case UnixueStatusCodeUserTokenFailure:
            case UnixueStatusCodeBSGetTokenFailure:
            case UnixueStatusCodeBSPostTokenFailure:
                msg = "token已过期，请重新登录";
                break;
            default:{
                msg = "请求出错";
                break;
            }
        }

        return msg;
    }
}

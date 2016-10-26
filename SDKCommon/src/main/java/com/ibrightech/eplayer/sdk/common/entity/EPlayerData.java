package com.ibrightech.eplayer.sdk.common.entity;

import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerLoginType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerPlayModelType;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EPlayerServerType;

import com.ibrightech.eplayer.sdk.common.util.StringUtils;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerUserInfoUserType;

import java.io.Serializable;

public class EPlayerData implements Serializable {

    public String liveClassroomId;  //直播房间
    public String customer;         //接入客户编号

    public EPlayerLoginType loginType; //登录方式

    public String user;             //帐号
    public String pwd;              //密码

    public String validateStr;      //验证字符串，

    public EPlayerPlayModelType playModel;  //播放模式

    public EPlayerServerType serverType = EPlayerServerType.EPlayerServerTypePublish;  //服务器模式：正式或者测试，默认为正式服务器

    public String playbackid;       //房间中的某一次回看，默认为nil表示最新的回看
    public EplayerUserInfoUserType usertype=EplayerUserInfoUserType.UserInfoUserTypeStudent; //用户身份 ，老师或学生

    public String name;             // 老师名字
    public String imgHead;          // 老师头像
    public String imgUrl;           // 音频默认图


    public String validate(){
        //TODO 数据校验，暂时可以不做
        if (StringUtils.isValid(this.liveClassroomId)
                && StringUtils.isValid(this.customer)){

            //todo 这个地方,对于有你学免费课是没有ValidateStr
            // checkupPlayerDataValidateStr(playerData);


            return "";
        }
        return "参数不完整";
    }
}

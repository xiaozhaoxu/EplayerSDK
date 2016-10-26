package com.ibrightech.eplayer.sdk.teacher.util;

import android.content.Context;
import android.content.Intent;

import com.ibrightech.eplayer.sdk.common.engin.EplayerEngin;
import com.ibrightech.eplayer.sdk.common.entity.EPlayerData;
import com.ibrightech.eplayer.sdk.common.entity.Enum.EplayerUserInfoUserType;
import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.teacher.activity.LivePushActivity;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-4
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public class SDKTeacherUtil {
    /*
     * 启动新的Activity
	 */
    public static void initPushPlayer(Context context,EPlayerData playerData) {
        initPushPlayer(context,playerData,null);
    }

    /*
    * 启动新的Activity
    */
    public static void initPushPlayer(Context context,EPlayerData playerData,File uploadFile) {
        if(!CheckUtil.isEmpty(playerData)){
            playerData.usertype= EplayerUserInfoUserType.UserInfoUserTypeTeacher;
        }
        EplayerEngin.initInstance(context,playerData);

        Intent intent = new Intent(context, LivePushActivity.class);
        intent.putExtra(LivePushActivity.KEY_EPLAY_DATA, playerData);
        if(!CheckUtil.isEmpty(uploadFile)) {
            intent.putExtra(LivePushActivity.KEY_FILE, uploadFile);
        }

        context.startActivity(intent);
    }

}

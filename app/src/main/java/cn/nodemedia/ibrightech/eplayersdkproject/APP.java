package cn.nodemedia.ibrightech.eplayersdkproject;


import android.app.Application;

import com.ibrightech.eplayer.sdk.common.config.EplayerConfigBuilder;

/**
 * Created by zhaoxu2014 on 16/8/24.
 */
public class APP extends Application {
    @Override
    public void onCreate() {
        EplayerConfigBuilder.getInstance().init(this);
        super.onCreate();

    }

    @Override
    public void onTerminate() {
        EplayerConfigBuilder.getInstance().onTerminate();
        super.onTerminate();
    }
}

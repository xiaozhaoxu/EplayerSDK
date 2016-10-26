package com.ibrightech.eplayer.sdk.common.down;

import android.support.annotation.Nullable;
import android.util.Log;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.FileCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhaoxu2014 on 16/5/23.
 */
public class EplayerDownFileUtil {
    private static EplayerDownFileUtil instance;

    public static EplayerDownFileUtil getInstance() {
        if (instance == null) {
            instance = new EplayerDownFileUtil();
        }
        return instance;
    }

    public void downLoad(String url ,String targetParent, String targetPath, final DownLoadCallBack callBack) {
        File parentFile = new File(targetParent);
        if(!parentFile.exists()) parentFile.mkdirs();
        OkHttpUtils.get(url)
                .execute(new FileCallback(targetParent,targetPath) {
                    @Override
                    public void onResponse(boolean b, File file, Request request, @Nullable Response response) {
                        callBack.onSucceed(file);
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        callBack.onFail();
                        Log.e("ddd", "onError :" + e.getMessage());
                    }


                });
    }

    public interface DownLoadCallBack {
        void onFail();

        void onSucceed(File file);
    }
}

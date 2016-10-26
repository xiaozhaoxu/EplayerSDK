package com.ibrightech.eplayer.sdk.teacher.ui.imageconfig;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.module.GlideModule;
import com.ibrightech.eplayer.sdk.common.util.StorageUtil;

import java.io.File;

/**
 * Created by zhaoxu2014 on 16/7/29.
 */
public class MyGlideModule implements GlideModule {
    @Override
    public void applyOptions( Context context, GlideBuilder glideBuilder) {
        glideBuilder.setDiskCache(new DiskCache.Factory(){

            @Override
            public DiskCache build() {
                File cacheLocation = new File(StorageUtil.getImageCacheDierctory());
                cacheLocation.mkdirs();
                return DiskLruCacheWrapper.get(cacheLocation, 100 * 1024 * 1024);
            }
        });


    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}

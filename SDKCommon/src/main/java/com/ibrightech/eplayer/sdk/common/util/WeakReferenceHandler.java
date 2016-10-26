package com.ibrightech.eplayer.sdk.common.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 软引用 Handler 抽象文件
 * @param <T>
 */
public  abstract  class WeakReferenceHandler<T> extends Handler {


    WeakReference<T> reference;

    @SuppressWarnings("unchecked")
    public WeakReferenceHandler(T t) {
        reference = new WeakReference(t);
    }


    @Override
    public void handleMessage(Message msg) {
        T t = (T)reference.get();
        handleMessage(msg,t);
    }
    protected abstract void handleMessage(Message msg, T t);
}

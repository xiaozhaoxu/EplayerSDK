package com.ibrightech.eplayer.sdk.teacher.ui.refreshlayout;

import android.view.View;
import android.view.ViewGroup;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/5/28 上午7:28
 * 描述:AdapterView和RecyclerView的item中子控件点击事件监听器
 */
public interface BGAOnItemChildClickListener {
    void onItemChildClick(ViewGroup parent, View childView, int position, Object object);
}
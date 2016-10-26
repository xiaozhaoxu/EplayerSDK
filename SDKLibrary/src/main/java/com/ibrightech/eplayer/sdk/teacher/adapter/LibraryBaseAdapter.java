package com.ibrightech.eplayer.sdk.teacher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ibrightech.eplayer.sdk.common.util.CheckUtil;
import com.ibrightech.eplayer.sdk.teacher.ui.refreshlayout.BGAOnItemChildClickListener;

import java.util.ArrayList;
import java.util.List;


public abstract class LibraryBaseAdapter<T> extends BaseAdapter {
    public AdapterCallback callback;
    protected Context ctx;
    protected List<T> libraryAdapterList = new ArrayList<T>();

    protected BGAOnItemChildClickListener mOnItemChildClickListener;
    public void setCallback(AdapterCallback callback) {
        this.callback = callback;
    }

    public void init(){

    }

    public LibraryBaseAdapter(Context ctx) {

        this.ctx = ctx;
        init();
    }

    public LibraryBaseAdapter(Context ctx, List<T> list) {
        this.ctx = ctx;
        init();
        setDatas(list);
    }

    public LibraryBaseAdapter(Context ctx, AdapterCallback callback) {
        this.ctx = ctx;
        init();
        this.callback = callback;
    }

    public LibraryBaseAdapter( Context ctx, List<T> libraryAdapterList,AdapterCallback callback) {
        this.callback = callback;
        this.ctx = ctx;
        this.libraryAdapterList = libraryAdapterList;
        init();
    }

    public List<T> getLibraryAdapterList() {
        return libraryAdapterList;
    }

    @Override
    public int getCount() {
        return libraryAdapterList.size();
    }

    @Override
    public T getItem(int position) {
        return libraryAdapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getItemView();

        }
        initItemView(position, convertView, parent);
        return convertView;
    }

    public abstract View getItemView();

    public abstract void initItemView(int position, View convertView, ViewGroup parent);

    public void initLibraryAdapterList(){
        if(CheckUtil.isEmpty(libraryAdapterList)){
            libraryAdapterList=new ArrayList<T>();
        }
    }
    /**
     * 获取数据集合
     *
     * @return
     */
    public List<T> getDatas() {
        initLibraryAdapterList();
        return libraryAdapterList;
    }
    /**
     * 在集合头部添加新的数据集合（下拉从服务器获取最新的数据集合，例如新浪微博加载最新的几条微博数据）
     *
     * @param datas
     */
    public void addNewDatas(List<T> datas) {
        initLibraryAdapterList();
        if (!CheckUtil.isEmpty(datas)) {
            libraryAdapterList.addAll(0, datas);
            notifyDataSetChanged();
        }
    }

    /**
     * 在集合尾部添加更多数据集合（上拉从服务器获取更多的数据集合，例如新浪微博列表上拉加载更晚时间发布的微博数据）
     *
     * @param datas
     */
    public void addMoreDatas(List<T> datas) {
        initLibraryAdapterList();
        if (!CheckUtil.isEmpty(datas)) {
            libraryAdapterList.addAll(libraryAdapterList.size(), datas);
            notifyDataSetChanged();
        }
    }
    /**
     * 设置全新的数据集合，如果传入null，则清空数据列表（第一次从服务器加载数据，或者下拉刷新当前界面数据表）
     *
     * @param datas
     */
    public void setDatas(List<T> datas) {
        initLibraryAdapterList();
        if (!CheckUtil.isEmpty(datas)) {
            libraryAdapterList = datas;
        } else {
            libraryAdapterList.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 清空数据列表
     */
    public void clear() {
        initLibraryAdapterList();
        libraryAdapterList.clear();
        notifyDataSetChanged();
    }

    /**
     * 删除指定索引数据条目
     *
     * @param position
     */
    public void removeItem(int position) {
        initLibraryAdapterList();
        if(position>getCount()-1){
            return;
        }
        libraryAdapterList.remove(position);
        notifyDataSetChanged();
    }
    /**
     * 删除指定数据条目
     *
     * @param model
     */
    public void removeItem(T model) {
        initLibraryAdapterList();
        removeItem(libraryAdapterList.indexOf(model));
    }

    /**
     * 在指定位置添加数据条目
     *
     * @param position
     * @param model
     */
    public void addItem(int position, T model) {
        initLibraryAdapterList();
        libraryAdapterList.add(position, model);
        notifyDataSetChanged();
    }

    /**
     * 在集合头部添加数据条目
     *
     * @param model
     */
    public void addFirstItem(T model) {
        initLibraryAdapterList();
        addItem(0, model);

    }
    /**
     * 在集合末尾添加数据条目
     *
     * @param model
     */
    public void addLastItem(T model) {
        initLibraryAdapterList();
        addItem(getCount(), model);
    }

    /**
     * 替换指定索引的数据条目
     *
     * @param location
     * @param newModel
     */
    public void setItem(int location, T newModel) {
        initLibraryAdapterList();
        libraryAdapterList.set(location, newModel);
        notifyDataSetChanged();
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(T oldModel, T newModel) {
        initLibraryAdapterList();
        setItem(libraryAdapterList.indexOf(oldModel), newModel);
    }

    /**
     * 设置item中的子控件点击事件监听器
     *
     */
    public void setOnItemChildClickListener(BGAOnItemChildClickListener onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }

    /**
     * 为id为viewId的item子控件设置点击事件监听器
     *

     */
    protected void setItemChildClickListener(final ViewGroup parent, final View clickview, final int postion,final  Object object) {
        if(null!=mOnItemChildClickListener){
            clickview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemChildClickListener.onItemChildClick(parent,clickview,postion ,object);
                }
            });
        }

    }

}

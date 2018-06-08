package com.mvpretrofit.mvp.houder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    View itemView;
    SparseArray<View> views;//存放Item中的控件

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        views = new SparseArray<View>();
    }

    //供adapter调用，返回holder
    public static <T extends BaseViewHolder> T getHolder(Context cxt, ViewGroup parent, int layoutId) {
        return (T) new BaseViewHolder(LayoutInflater.from(cxt).inflate(layoutId, parent, false));
    }

    //根据Item中的控件Id获取控件
    public <T extends View> T getView(int viewId) {
        View childreView = views.get(viewId);
        if (childreView == null) {
            childreView = itemView.findViewById(viewId);
            views.put(viewId, childreView);
        }
        return (T) childreView;
    }

    //根据Item中的控件Id向控件添加事件监听
    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }
}

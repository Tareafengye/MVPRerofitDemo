package com.mvpretrofit.mvp.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mvpretrofit.mvp.houder.BaseViewHolder;

import java.util.List;

/***
 * @author 描述 封装recycleViewAdapter适配器
 * @param <T>
 *
 */

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    Context context;
    List<T> data;//引入泛型
    int layoutId;
    public BaseRecycleAdapter(Context context, List<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }
    public void setData(List<T> data) {
        this.data = data;
    }
    public List<T> getData() {
        return this.data;
    }
    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //从holder基类中获取holder
        return BaseViewHolder.getHolder(context, parent, layoutId);
    }
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        //调用由子类实现的抽象方法，将操作下放到子类中
        onBind(holder, data.get(position), position);
    }
    //抽象方法，让子类继承实现
    public abstract void onBind(BaseViewHolder holder, T t, int position);
}

package com.mvpretrofit.mvp.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mvpretrofit.R;
import com.mvpretrofit.model.GankUrlEntity;
import com.mvpretrofit.mvp.base.BaseRecycleAdapter;
import com.mvpretrofit.mvp.houder.BaseViewHolder;

import java.util.List;

public class RecycleAdapterTest extends BaseRecycleAdapter<GankUrlEntity.ResultsBean> {
    private Context mContext;

    public RecycleAdapterTest(Context context, List<GankUrlEntity.ResultsBean> data, int layoutId) {
        super(context, data, layoutId);
        this.mContext = context;
    }

    @Override
    public void onBind(BaseViewHolder holder, final GankUrlEntity.ResultsBean bean, int position) {
        //根据控件Id获取Item内部的控件
        TextView tvName = holder.getView(R.id.text);
        ImageView imageView = holder.getView(R.id.image);
        Glide.with(mContext).load(bean.getUrl()).into(imageView);
        tvName.setText(bean.getDesc());
        //根据Item中的控件Id向控件添加事件监听
//        holder.setOnClickListener(R.id.ivPhone, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //自定义的代码
//            }
//        });
    }
}

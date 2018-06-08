package com.mvpretrofit.mvp.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mvpretrofit.R;
import com.mvpretrofit.contact.HomeContact;
import com.mvpretrofit.model.GankUrlEntity;
import com.mvpretrofit.mvp.adapter.RecycleAdapterTest;
import com.mvpretrofit.mvp.base.BaseActivity;
import com.mvpretrofit.presenter.HomePresenter;


public class MainActivity extends BaseActivity<HomeContact.presenter> implements HomeContact.view {
    private RecycleAdapterTest adapterTest;
    private RecyclerView recycy_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showLoadingDialog("数据加载中...");
        presenter.getData("福利", 10, 1);
    }

    @Override
    public HomeContact.presenter initPresenter() {
        return new HomePresenter(this);
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void setData(GankUrlEntity dataList) {
        dismissLoadingDialog();
        recycy_view.setLayoutManager(new LinearLayoutManager(this));
        adapterTest = new RecycleAdapterTest(this, dataList.getResults(), R.layout.item_recycy_view);
        recycy_view.setAdapter(adapterTest);
    }
}

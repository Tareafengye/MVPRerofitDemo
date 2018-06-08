package com.mvpretrofit.presenter;


import com.mvpretrofit.api.Api;
import com.mvpretrofit.base.BasePresenterImpl;
import com.mvpretrofit.base.BaseView;
import com.mvpretrofit.contact.HomeContact;
import com.mvpretrofit.model.GankUrlEntity;
import com.mvpretrofit.retroft.DefaultObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomePresenter extends BasePresenterImpl<HomeContact.view> implements HomeContact.presenter {
    public HomePresenter(HomeContact.view view) {
        super(view);
    }


    @Override
    public void getData(String type, int page, int size) {
        Api.getInstance().onGankUrl(type, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<GankUrlEntity>() {
                    @Override
                    public void onSuccess(GankUrlEntity response) {
                        view.setData(response);
                    }

                });
    }

}

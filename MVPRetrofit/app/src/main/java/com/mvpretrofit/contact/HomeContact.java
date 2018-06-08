package com.mvpretrofit.contact;


import com.mvpretrofit.base.BasePresenter;
import com.mvpretrofit.base.BaseView;
import com.mvpretrofit.model.GankUrlEntity;

public interface HomeContact {
    interface view extends BaseView {
        /**
         * 设置数据
         *
         * @param dataList
         */
        void setData(GankUrlEntity dataList);
    }

    interface presenter extends BasePresenter {
        /**
         * 获取数据
         */
        void getData(String type, int page, int size);
    }
}

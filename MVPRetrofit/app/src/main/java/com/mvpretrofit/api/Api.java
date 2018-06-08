package com.mvpretrofit.api;


import com.mvpretrofit.retroft.BaseApiImpl;

public class Api extends BaseApiImpl {
    private static Api api = new Api(RetrofitService.BASE_URL);

    public Api(String baseUrl) {
        super(baseUrl);
    }

    public static RetrofitService getInstance() {
        return api.getRetrofit().create(RetrofitService.class);
    }
}

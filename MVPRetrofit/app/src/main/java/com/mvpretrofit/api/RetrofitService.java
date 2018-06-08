package com.mvpretrofit.api;



import com.mvpretrofit.model.GankUrlEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/*
 * 项目名:    playstock
 * 包名       com.hjtech.playstock.Api
 * 文件名:    RetrofitService
 * 创建者:    ZJB
 * 创建时间:  2017/7/6 on 14:30 
 * 描述:     TODO
 */
public interface RetrofitService {


    String BASE_URL = "http://gank.io/api/data/";


    /**
     * 测试接口
     *
     * @return
     */
    @GET("{type}/{page}/{size}")
    Observable<GankUrlEntity> onGankUrl(@Path("type") String type, @Path("page") int page, @Path("size") int size);


}
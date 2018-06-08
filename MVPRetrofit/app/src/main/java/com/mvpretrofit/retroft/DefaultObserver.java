package com.mvpretrofit.retroft;

import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.mvpretrofit.base.BaseView;
import com.mvpretrofit.model.BasicResponse;
import com.mvpretrofit.utils.ByAlert;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

import static com.mvpretrofit.retroft.DefaultObserver.ExceptionReason.CONNECT_ERROR;
import static com.mvpretrofit.retroft.DefaultObserver.ExceptionReason.CONNECT_TIMEOUT;
import static com.mvpretrofit.retroft.DefaultObserver.ExceptionReason.PARSE_ERROR;
import static com.mvpretrofit.retroft.DefaultObserver.ExceptionReason.UNKNOWN_ERROR;

public abstract class DefaultObserver<T extends BasicResponse> implements Observer<T> {
    //  Activity 是否在执行onStop()时取消订阅
    private boolean isAddInStop = false;
    //将所有正在处理的Subscription都添加到CompositeSubscription中。统一退出的时候注销观察
    private CompositeDisposable mCompositeDisposable;
    @Override
    public void onSubscribe(Disposable d) {
        if (isAddInStop) {    //  在onStop中取消订阅
            //csb 如果解绑了的话添加 sb 需要新的实例否则绑定时无效的
            if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
                mCompositeDisposable = new CompositeDisposable();
            }
            mCompositeDisposable.add(d);
        } else { //  在onDestroy中取消订阅
            if (mCompositeDisposable != null) {
                mCompositeDisposable.dispose();
            }
        }
    }

    @Override
    public void onNext(T response) {
        if (response.getCode() == 1) {
            onSuccess(response);
        } else if (response.getCode() == 0) {
            onSuccess(response);
        } else {
            onFail(response);
        }

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {     //   HTTP错误
            onException(ExceptionReason.BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(PARSE_ERROR);
        } else {
            onException(UNKNOWN_ERROR);
        }
    }

    @Override
    public void onComplete() {
    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response);

    /**
     * 服务器返回数据，但响应码不为200
     *
     * @param response 服务器返回的数据
     */
    public void onFail(T response) {
        String message = response.getMessage();
        if (!TextUtils.isEmpty(message)) {
            ByAlert.alert(message);
        } else {
            ByAlert.alert("服务器返回数据失败");
        }
    }

    /**
     * 请求异常
     *
     * @param reason
     */
    public void onException(ExceptionReason reason) {
        switch (reason) {
            case CONNECT_ERROR:
                ByAlert.alert("网络连接失败,请检查网络");
                break;

            case CONNECT_TIMEOUT:
                ByAlert.alert("链接超时,请稍后重试");
                break;

            case BAD_NETWORK:
                ByAlert.alert("服务器异常");
                break;

            case PARSE_ERROR:
                ByAlert.alert("解析服务器数据失败");
                break;

            case UNKNOWN_ERROR:
            default:
                ByAlert.alert("未知错误");
                break;
        }
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}

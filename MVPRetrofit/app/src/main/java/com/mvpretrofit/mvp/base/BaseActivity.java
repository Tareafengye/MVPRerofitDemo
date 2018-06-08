package com.mvpretrofit.mvp.base;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;

import com.mvpretrofit.base.BasePresenter;
import com.mvpretrofit.base.BaseView;
import com.mvpretrofit.mvp.dialog.LoadingDailog;
import com.mvpretrofit.utils.ActivityManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;



public abstract class BaseActivity<P extends BasePresenter> extends FragmentActivity
        implements BaseView, View.OnClickListener {

    protected P presenter;
    public Context context;
    private ViewContainer container;
    public boolean isGestureOpen;
    private LoadingDailog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        container = new ViewContainer(getApplicationContext());
        container.setOrientation(LinearLayout.VERTICAL);
        ActivityManager.getAppInstance().addActivity(this);//将当前activity添加进入管理栈
        presenter = initPresenter();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (container != null) {
                LayoutInflater.from(this).inflate(layoutResID, container, true);
                setContentView(container);
            }
        } else {
            setContentView(layoutResID);
        }
        smartInject();

    }
    private void smartInject() {

        try {
            Class<? extends Activity> clz = getClass();
            while (clz != BaseActivity.class) {
                Field[] fs = clz.getDeclaredFields();
                Resources res = getResources();
                String packageName = getPackageName();
                for (Field field : fs) {
                    if (!View.class.isAssignableFrom(field.getType())) {
                        continue;
                    }
                    int viewId = res.getIdentifier(field.getName(), "id", packageName);
                    if (viewId == 0)
                        continue;
                    field.setAccessible(true);
                    try {
                        View v = findViewById(viewId);
                        field.set(this, v);
                        Class<?> c = field.getType();
                        Method m = c.getMethod("setOnClickListener", View.OnClickListener.class);
                        m.invoke(v, this);
                    } catch (Throwable e) {
                    }
                    field.setAccessible(false);

                }

                clz = (Class<? extends Activity>) clz.getSuperclass();

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    @Override
    protected void onDestroy() {
        ActivityManager.getAppInstance().removeActivity(this);//将当前activity移除管理栈
        if (presenter != null) {
            presenter.detach();//在presenter中解绑释放view
            presenter = null;
        }
        super.onDestroy();
    }
    /**
     * 屏幕左侧右划返回容器 ,
     *
     * @author Young
     */
    private class ViewContainer extends LinearLayout {

        private int leftMargin;
        private VelocityTracker tracker;
        private float startX;
        private float startY;

        public ViewContainer(Context context) {
            super(context);
//			leftMargin= DensityUtil.dip2px(35);

        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (isGestureOpen == false) {
                return super.dispatchTouchEvent(ev);
            }
            switch (ev.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    //当满足下面条件时 视为 右划关闭手势
                    //起始按压位置x坐标小与leftMargin&& 向右滑动                       &&           向右滑动距离    >   竖直方向距离
                    if (startX < leftMargin && ev.getRawX() > startX && ev.getRawX() - startX > Math.abs(ev.getRawY() - startY)) {
                        //速度大于2500时关闭activity
                        tracker.computeCurrentVelocity(1000);
                        if (tracker.getXVelocity() > 2500) {
                            finish();
                        }

                    }

                    tracker.recycle();
                    break;

                case MotionEvent.ACTION_DOWN:
                    startX = ev.getRawX();
                    startY = ev.getRawY();
                    tracker = VelocityTracker.obtain();
                    tracker.addMovement(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    tracker.addMovement(ev);
                    break;
            }


            return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (isGestureOpen == false) {
                return super.onTouchEvent(event);
            }
            return true;
        }

    }
    /**
     * 在子类中初始化对应的presenter
     *
     * @return 相应的presenter
     */
    public abstract P initPresenter();


    @Override
    public void dismissLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showLoadingDialog(String msg) {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage(msg)
                .setCancelable(true)
                .setCancelOutside(true);
        mProgressDialog = loadBuilder.create();
        mProgressDialog.show();
    }
    /**
     * View点击
     **/
    public abstract void widgetClick(View v);

    @Override
    public void onClick(View v) {
        if (fastClick())
            widgetClick(v);
    }
    /**
     * [防止快速点击]
     *
     * @return
     */
    private boolean fastClick() {
        long lastClick = 0;
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }
}

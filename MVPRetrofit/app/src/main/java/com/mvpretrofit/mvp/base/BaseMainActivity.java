package com.mvpretrofit.mvp.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class BaseMainActivity extends FragmentActivity implements
        View.OnClickListener {
    private View statusBar;
    public boolean isGestureOpen;
    private ViewContainer container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = new ViewContainer(getApplicationContext());
        container.setOrientation(LinearLayout.VERTICAL);
        other();
    }

    @SuppressLint("InlinedApi")
    void other() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            statusBar = new View(this);
            ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
            statusBar.setLayoutParams(param);
//            setStatusBarColor(R.color.colorAccent);

            container = new ViewContainer(getApplicationContext());
            container.setOrientation(LinearLayout.VERTICAL);
            container.addView(statusBar);
        }
//		smartInject();// 自动获取控件
    }
    /**
     * 设置barcolor状态栏颜色属性
     *
     * @param activity
     * @param color
     */
    public void setBarColor(Activity activity, int color) {
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

    /**
     * 设置状态栏颜色，
     *
     * @param color 颜色资源id， 如 R.color.orange</br> 0 黑色
     */
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (color == 0) {
                Log.v("hutou", "0");
                statusBar.setBackgroundColor(Color.BLACK);
            } else {
                Log.v("hutou", "1");
                statusBar.setBackgroundColor(getResources().getColor(color));
            }
        }
    }

    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void smartInject() {

        try {
            Class<? extends Activity> clz = getClass();
            while (clz != BaseMainActivity.class) {
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
     * View点击
     **/
    public abstract void widgetClick(View v);

    @Override
    public void onClick(View v) {
        if (fastClick())
            widgetClick(v);
    }

    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

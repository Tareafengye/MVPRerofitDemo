package com.mvpretrofit.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2018/3/2 0002.
 */

public class App extends Application {
    private static App app;
    private static Map<String, Activity> destoryMap = new HashMap<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */
    public static void addDestoryActivity(Activity activity, String activityName) {
        destoryMap.put(activityName, activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet = destoryMap.keySet();
        if (keySet.size() > 0) {
            for (String key : keySet) {
                if (activityName.equals(key)) {
                    destoryMap.get(key).finish();
                }
            }
        }
    }
    private  static WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();
    public static WindowManager.LayoutParams getMywmParams(){
        return wmParams;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;


    }




    public static App getInstance() {
        return app;
    }

}

package com.mvpretrofit.utils;

import android.widget.Toast;

import com.mvpretrofit.app.App;


public class ByAlert {
    public static void alert(String info) {
        if (info != null) {
            if (info.equals("用户ID缺失") || info.equals("UID不能为空")) {
                Toast.makeText(App.getInstance(), "亲,你还未登录,登录后更精彩",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(App.getInstance(), info, Toast.LENGTH_LONG).show();
            }
        }
    }
}

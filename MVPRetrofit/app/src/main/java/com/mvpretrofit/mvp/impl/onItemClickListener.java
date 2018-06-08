package com.mvpretrofit.mvp.impl;

import android.view.View;

public interface onItemClickListener<T> {
    void onClick(View v, T data);

    boolean onLongClick(View v, T data);
}

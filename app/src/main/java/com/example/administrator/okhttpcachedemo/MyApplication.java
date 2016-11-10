
package com.example.administrator.okhttpcachedemo;

import android.app.Application;

import com.example.administrator.okhttpcachedemo.api.OkhttpUtil;

/**
 * Copyright ©  bookegou.com
 * Created by Administrator on 2016/11/9.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkhttpUtil.getInstance().initOkHttpClient(this);
    }
}

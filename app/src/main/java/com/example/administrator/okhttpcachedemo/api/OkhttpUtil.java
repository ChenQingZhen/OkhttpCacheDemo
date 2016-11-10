package com.example.administrator.okhttpcachedemo.api;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.okhttpcachedemo.utils.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.net.ContentHandler;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Copyright ©  bookegou.com
 * Created by Administrator on 2016/11/9.
 */
public class OkhttpUtil {
    private static final String TAG = OkhttpUtil.class.getSimpleName();
    private static OkhttpUtil mInstance;
    private static OkHttpClient mOkHttpClient;
    //存储临时的Context作为判断网络,一定要Application的实例
    private Context mContext;

    private Handler mHandler=new Handler(Looper.getMainLooper());

    public  void initOkHttpClient(Context context) {
        if(!( context instanceof Application)){
            throw new UnsupportedOperationException("context必须是Application实例");
        }
        if (mOkHttpClient == null) {
            synchronized (OkhttpUtil.class) {
                mContext=context;
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                mOkHttpClient=builder.cache(getCache(context)).connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .addInterceptor(mLoggingInterceptor)
                        .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                        .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                        .build();
            }
        }

    }

    private OkhttpUtil() {

    }

    public static OkhttpUtil getInstance() {
        if (mInstance == null) {
            mInstance = new OkhttpUtil();
        }
        return mInstance;
    }


    private static Cache getCache(Context context) {
        File cacheFile = new File(context.getCacheDir(), "ok_cache");
        return new Cache(cacheFile, 1024 * 1024 * 10); //10Mb
    }

    public void getAsynHttp(final CallBack callBack) {
        Request.Builder requestBuilder = new Request.Builder().url("http://www.baidu.com");
        //可以省略，默认是GET请求
        requestBuilder.method("GET",null);
        Request request = requestBuilder.build();
        Call mcall= mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFail(e.getStackTrace().toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result;
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    result="cache---" + str;
                    Log.i("wangshu", result);
                } else {
                    response.body().string();
                    String str = response.networkResponse().toString();
                    result="network---" + str;
                    Log.i("wangshu",result);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(result);
                    }
                });
            }
        });
    }

    public void postAsynHttp(final CallBack callBack) {
        RequestBody formBody = new FormBody.Builder()
                .add("size", "10")
                .build();
        Request request = new Request.Builder()
                .url("http://api.1-blog.com/biz/bizserver/article/list.do")
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFail(e.getStackTrace().toString());
                        }
                    });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result;
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    result="cache---" + str;
                    Log.i("wangshu", result);
                } else {
                    response.body().string();
                    String str = response.networkResponse().toString();
                    result="network---" + str;
                    Log.i("wangshu",result);
                }
                String str = response.body().string();
                Log.i("wangshu", str);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                       callBack.onSuccess(result);
                    }
                });
            }

        });
    }

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Log.w(TAG, "no network");
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtil.isNetworkAvailable(mContext)) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

    private final Interceptor mLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.i(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            Log.i(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            return response;
        }
    };
}



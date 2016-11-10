/**
 * Copyright (C) 2015. Keegan小钢（http://keeganlee.me）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.administrator.okhttpcachedemo.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.keeganlee.kandroid.api.net.HttpEngine;
import me.keeganlee.kandroid.api.utils.EncryptUtil;
import me.keeganlee.kandroid.model.CouponBO;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Api实现类
 *
 * @author Keegan小钢
 * @date 15/6/21
 * @version 1.0
 */
public class ApiImpl implements Api {
    private final static String APP_KEY = "ANDROID_KCOUPON";
    private final static String TIME_OUT_EVENT = "CONNECT_TIME_OUT";
    private final static String TIME_OUT_EVENT_MSG = "连接服务器失败";

    private OkHttpUtils httpEngine;

    public ApiImpl() {
        httpEngine = OkHttpUtils.getInstance();
    }

    @Override
    public ApiResponse<Void> sendSmsCode4Register(String phoneNum) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appKey", APP_KEY);
        paramMap.put("method", SEND_SMS_CODE);
        paramMap.put("phoneNum", phoneNum);

        final Type type = new TypeToken<ApiResponse<Void>>(){}.getType();
        try {
             httpEngine.post().params(paramMap).build().execute(new Callback<ApiResponse<Void>>(){

                @Override
                public ApiResponse<Void> parseNetworkResponse(Response response, int id) throws Exception {
                    String str=response.body().string();
                    return new Gson().fromJson(str,type);
                }

                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(ApiResponse<Void> response, int id) {

                }
            });
        } catch (IOException e) {
            return new ApiResponse(TIME_OUT_EVENT, TIME_OUT_EVENT_MSG);
        }
    }

    @Override
    public ApiResponse<Void> registerByPhone(String phoneNum, String code, String password) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appKey", APP_KEY);
        paramMap.put("method", REGISTER);
        paramMap.put("phoneNum", phoneNum);
        paramMap.put("code", code);
        paramMap.put("password", EncryptUtil.makeMD5(password));

        Type type = new TypeToken<ApiResponse<List<CouponBO>>>(){}.getType();
        try {
            return httpEngine.postHandle(paramMap, type);
        } catch (IOException e) {
            return new ApiResponse(TIME_OUT_EVENT, TIME_OUT_EVENT_MSG);
        }
    }

    @Override
    public ApiResponse<Void> loginByApp(String loginName, String password, String imei, int loginOS) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appKey", APP_KEY);
        paramMap.put("method", LOGIN);
        paramMap.put("loginName", loginName);
        paramMap.put("password", EncryptUtil.makeMD5(password));
        paramMap.put("imei", imei);
        paramMap.put("loginOS", String.valueOf(loginOS));

        Type type = new TypeToken<ApiResponse<List<CouponBO>>>(){}.getType();
        try {
            return httpEngine.postHandle(paramMap, type);
        } catch (IOException e) {
            return new ApiResponse(TIME_OUT_EVENT, TIME_OUT_EVENT_MSG);
        }
    }

    @Override
    public ApiResponse<List<CouponBO>> listNewCoupon(int currentPage, int pageSize) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appKey", APP_KEY);
        paramMap.put("method", LIST_COUPON);
        paramMap.put("currentPage", String.valueOf(currentPage));
        paramMap.put("pageSize", String.valueOf(pageSize));

        Type type = new TypeToken<ApiResponse<List<CouponBO>>>(){}.getType();
        try {
            return httpEngine.postHandle(paramMap, type);
        } catch (IOException e) {
            return new ApiResponse(TIME_OUT_EVENT, TIME_OUT_EVENT_MSG);
        }
    }

}

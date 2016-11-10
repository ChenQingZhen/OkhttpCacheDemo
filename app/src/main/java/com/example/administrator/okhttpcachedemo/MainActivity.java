package com.example.administrator.okhttpcachedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.okhttpcachedemo.api.CallBack;
import com.example.administrator.okhttpcachedemo.api.OkhttpUtil;

public class MainActivity extends AppCompatActivity {
    private TextView mContentTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        OkhttpUtil.getInstance().getAsynHttp(new CallBack() {
            @Override
            public void onSuccess(String result) {
                mContentTv.setText(result);
            }

            @Override
            public void onFail(String error) {
                mContentTv.setText(error);
            }
        });
    }

    private void initView() {
        mContentTv= (TextView) findViewById(R.id.tv_content);
    }
}

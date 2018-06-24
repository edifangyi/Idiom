package com.Idiom.ui;

import android.content.Intent;
import android.os.Bundle;

import com.Idiom.R;
import com.Idiom.base.BaseActivity;

/**
 * ================================================
 * 作    者：FANGYI <87649669@qq.com>
 * 版    本：1.0.0
 * 日    期：2018/5/15
 * 说    明：
 * ================================================
 */
public class SplashActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        //延迟1秒跳转到MainActivity
        mHandler.postDelayed(() -> {
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }
}

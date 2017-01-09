package com.example.cmy.lecturetest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.cmy.lecturetest.application.CustomApplication;
import com.example.cmy.lecturetest.R;

public class SplashActivity extends Activity {
    private CustomApplication application;
    //延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        application = (CustomApplication) getApplication();
        // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(application.getUsername() == null && application.getPsw() == null)
                    goLogin();
                else
                    goHome();
            }
        }, SPLASH_DELAY_MILLIS);
    }

    private void goLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, LectureListActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
}

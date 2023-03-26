package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.utils.MyApplication;

public class SplashActivity extends AppCompatActivity {


    Handler handler;
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Application application = getApplication();
        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            ((MyApplication) application).showAdIfAvailable(SplashActivity.this, this::goToNext);
        };
        if (application instanceof MyApplication) {
            handler.postDelayed(runnable, 5000);
        }else {
            goToNext();
        }

    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    void goToNext() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }
}
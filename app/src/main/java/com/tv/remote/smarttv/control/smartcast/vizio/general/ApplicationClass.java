package com.tv.remote.smarttv.control.smartcast.vizio.general;

import android.app.Application;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.MobileAds;

public class ApplicationClass extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }
}

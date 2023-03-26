package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tv.remote.smarttv.control.smartcast.vizio.R;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        findViewById(R.id.ivBack).setOnClickListener(v -> {
            this.finish();
        });


        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());


    }



}
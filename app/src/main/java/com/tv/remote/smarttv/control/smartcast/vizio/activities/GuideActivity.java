package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.yodo1.mas.banner.Yodo1MasBannerAdView;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        findViewById(R.id.ivBack).setOnClickListener(v -> {
            this.finish();
        });
        Yodo1MasBannerAdView adView = findViewById(R.id.bannerView);
        adView.loadAd();



    }



}
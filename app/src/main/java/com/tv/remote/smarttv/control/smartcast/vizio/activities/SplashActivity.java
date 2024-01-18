package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.tv.remote.smarttv.control.smartcast.vizio.R;

public class SplashActivity extends AppCompatActivity {


    private InterstitialAd mInterstitial = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        InterstitialAd.load(this, getString(R.string.adMobInterstitialSplash), new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        mInterstitial = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        mInterstitial = interstitialAd;
                        mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                mInterstitial = null;
                                goToNext();
                            }
                        });

                    }
                });



        new Handler().postDelayed(() -> {
            if (mInterstitial != null) {
                mInterstitial.show(this);
            } else {
                goToNext();
            }
        }, 2000);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void goToNext() {

        startActivity(new Intent(this, MainActivity.class));
        this.finish();

    }

}
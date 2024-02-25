package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.tv.remote.smarttv.control.smartcast.vizio.R;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashActivity extends AppCompatActivity {


    private InterstitialAd mInterstitial = null;

    private ConsentInformation consentInformation;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    private CountDownTimer countDownTimer = null;


    private void loadInterstitial() {
        InterstitialAd.load(this, getString(R.string.adMobInterstitialSplash), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
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
    }


    private void loadTimer() {
        countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (!SplashActivity.this.isFinishing()) {
                    goToNext();

//                    if (mInterstitial != null) {
//
//                        Dialog progressDialog = new Dialog(SplashActivity.this);
//                        try {
//                            if (progressDialog.getWindow() != null) {
//                                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            }
//                            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            progressDialog.setContentView(R.layout.ad_loading);
//                            progressDialog.setCancelable(false);
//                        } catch (Exception exception) {
//                            exception.printStackTrace();
//                        }
//
//                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                            if (!SplashActivity.this.isFinishing()) {
//                                mInterstitial.show(SplashActivity.this);
//                            }
//                        }, 1200);
//                    } else {
//                        goToNext();
//                    }
                }
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
//                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//                .addTestDeviceHashedId("sdfs")
//                .build();

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
//                .setConsentDebugSettings(debugSettings)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
//        consentInformation.reset();
        consentInformation.requestConsentInfoUpdate(this, params, () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(this, loadAndShowError -> {
                                if (!this.isFinishing()) {
                                    if (consentInformation.canRequestAds()) {
                                        initializeMobileAdsSdk();
                                    } else {
                                        loadTimer();
                                    }
                                }
                            }
                    );
                },
                requestConsentError -> {
                    if (!this.isFinishing()) {
                        loadTimer();
                    }
                });

        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }


    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }
        MobileAds.initialize(this);
//        loadInterstitial();
        loadTimer();
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
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

}
package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivitySplashBinding;
import com.tv.remote.smarttv.control.smartcast.vizio.general.ApplicationClass;
import com.tv.remote.smarttv.control.smartcast.vizio.general.GoogleMobileAdsConsentManager;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashActivity extends AppCompatActivity {

    private InterstitialAd mInterstitial = null;
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
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
        // loadTimer() can be reached twice (immediate check + consent callback); keep a single timer.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(9000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                if (!SplashActivity.this.isFinishing()) {
                    if (((ApplicationClass) getApplication()).adAvailable()) {
                        ((ApplicationClass) getApplication()).showAdIfAvailable(SplashActivity.this, () -> {
                            if (googleMobileAdsConsentManager.getCanRequestAds()) {
                                goToNext();
                            }
                        });
                    } else {
                        goToNext();
                    }
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
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
                            | WindowInsetsCompat.Type.ime()
            );

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        initConsent();
    }

    private void initConsent() {
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.Companion.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(this, error -> {
            if (googleMobileAdsConsentManager.getCanRequestAds()) {
                initializeMobileAdsSdk();
            } else {
                loadTimer();
            }
        });

        if (googleMobileAdsConsentManager.getCanRequestAds()) {
            initializeMobileAdsSdk();
        }else {
            loadTimer();
        }
    }


    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize on a background thread, as shown in the AdMob Android quick start.
        new Thread(() -> MobileAds.initialize(getApplicationContext(), initializationStatus -> {})).start();
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
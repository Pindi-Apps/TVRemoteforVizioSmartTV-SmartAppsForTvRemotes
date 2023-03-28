package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.yodo1.mas.Yodo1Mas;
import com.yodo1.mas.error.Yodo1MasError;
import com.yodo1.mas.helper.model.Yodo1MasAdBuildConfig;
import com.yodo1.mas.interstitial.Yodo1MasInterstitialAd;
import com.yodo1.mas.interstitial.Yodo1MasInterstitialAdListener;

public class SplashActivity extends AppCompatActivity {


//    Handler handler;
//    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Yodo1MasAdBuildConfig config = new Yodo1MasAdBuildConfig.Builder().enableUserPrivacyDialog(true).build();
        Yodo1Mas.getInstance().setAdBuildConfig(config);
        Yodo1Mas.getInstance().initMas(this, getString(R.string.yodaMasAppKey), new Yodo1Mas.InitListener() {
            @Override
            public void onMasInitSuccessful() {
                Yodo1MasInterstitialAd.getInstance().loadAd(SplashActivity.this);
//                        boolean isLoaded = Yodo1MasInterstitialAd.getInstance().isLoaded();
//
//                        if (isLoaded){
//                            Yodo1MasInterstitialAd.getInstance().showAd(SplashActivity.this, getString(R.string.yodaMasInterstitial));
//                        }else{
//                            goToNext();
//                        }
                Yodo1MasInterstitialAd.getInstance().setAdListener(new Yodo1MasInterstitialAdListener() {
                    @Override
                    public void onInterstitialAdLoaded(Yodo1MasInterstitialAd ad) {
                        ad.showAd(SplashActivity.this);
                    }

                    @Override
                    public void onInterstitialAdFailedToLoad(Yodo1MasInterstitialAd ad, @NonNull Yodo1MasError error) {
//                                ad.loadAd(SplashActivity.this);
                        goToNext();
                    }

                    @Override
                    public void onInterstitialAdOpened(Yodo1MasInterstitialAd ad) {

                    }

                    @Override
                    public void onInterstitialAdFailedToOpen(Yodo1MasInterstitialAd ad, @NonNull Yodo1MasError error) {
                        goToNext();
                    }

                    @Override
                    public void onInterstitialAdClosed(Yodo1MasInterstitialAd ad) {
                        goToNext();
                    }
                });



            }

            @Override
            public void onMasInitFailed(@NonNull Yodo1MasError error) {
                goToNext();
            }
        });

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
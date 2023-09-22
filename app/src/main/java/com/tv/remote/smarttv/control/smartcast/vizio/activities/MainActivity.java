package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.tv.remote.smarttv.control.smartcast.vizio.BuildConfig;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.FindActivity;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;


    private InterstitialAd mInterstitialAd;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadInterstitial();
        binding.nav.navRate.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))));
        binding.nav.navMoreApps.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.moreAppsLink)))));
        binding.cvWifiRemote.setOnClickListener(v -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
            } else {
                startActivity(new Intent(MainActivity.this, FindActivity.class));
            }
        });

        binding.cvIrRemote.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RemoteViewActivity.class).putExtra("brand_name", "Vizio")));

        binding.cvScreenMirror.setOnClickListener(v -> startActivity(new Intent(this, ConnectionActivity.class)));

        binding.ivPrivacy.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacyPolicyLink)))));
        binding.cvRateUs.setOnClickListener(v -> startActivity(new Intent(this, GuideActivity.class)));
        binding.ivMenu.setOnClickListener(v -> binding.drawLayout.openDrawer(GravityCompat.START));
        binding.nav.navHome.setOnClickListener(v -> binding.drawLayout.closeDrawer(GravityCompat.START));

        binding.adLayout.adCallToAction.setOnClickListener(v -> openLink(getString(R.string.adLink)));

    }


    private void openLink(String link) {
        try {
            this.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private void loadInterstitial() {
        InterstitialAd.load(this, getString(R.string.adMobInterstitial), new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                mInterstitialAd = null;
                                startActivity(new Intent(MainActivity.this, FindActivity.class));

                            }
                        });
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if (binding.drawLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawLayout.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this, BackActivity.class));
        }
    }
}
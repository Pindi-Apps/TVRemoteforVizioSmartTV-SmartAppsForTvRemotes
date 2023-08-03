package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.tv.remote.smarttv.control.smartcast.vizio.BuildConfig;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.FindActivity;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityMainBinding;
import com.yodo1.mas.error.Yodo1MasError;
import com.yodo1.mas.interstitial.Yodo1MasInterstitialAd;
import com.yodo1.mas.interstitial.Yodo1MasInterstitialAdListener;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    private String currentClick = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.yodo1MasNative.loadAd();
        loadInterstitial();
        binding.nav.navRate.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))));
        binding.nav.navMoreApps.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.moreAppsLink)))));
        binding.cvWifiRemote.setOnClickListener(v -> {
            currentClick = "wifi";
            boolean isLoaded = Yodo1MasInterstitialAd.getInstance().isLoaded();
            if (isLoaded) {
                Yodo1MasInterstitialAd.getInstance().showAd(MainActivity.this, getString(R.string.yodaMasInterstitial));
            } else {
                startActivity(new Intent(MainActivity.this, FindActivity.class));
            }
        });

        binding.cvIrRemote.setOnClickListener(v -> {
            currentClick = "ir";
            boolean isLoaded = Yodo1MasInterstitialAd.getInstance().isLoaded();
            if (isLoaded) {
                Yodo1MasInterstitialAd.getInstance().showAd(MainActivity.this, getString(R.string.yodaMasInterstitial));
            } else {
                startActivity(new Intent(MainActivity.this, RemoteViewActivity.class).putExtra("brand_name", "Vizio"));
            }
        });

        binding.cvScreenMirror.setOnClickListener(v -> {
            try {
                startActivity(new Intent("android.settings.CAST_SETTINGS"));
            } catch (Exception exception1) {
                Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
            }
        });

        binding.ivPrivacy.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacyPolicyLink)))));
        binding.cvRateUs.setOnClickListener(v -> {
            startActivity(new Intent(this, GuideActivity.class));
        });
        binding.ivMenu.setOnClickListener(v -> {
            binding.drawLayout.openDrawer(GravityCompat.START);
        });
        binding.nav.navHome.setOnClickListener(v -> binding.drawLayout.closeDrawer(GravityCompat.START));
    }

    private void loadInterstitial() {
        Yodo1MasInterstitialAd.getInstance().loadAd(MainActivity.this);
        Yodo1MasInterstitialAd.getInstance().setAdListener(new Yodo1MasInterstitialAdListener() {
            @Override
            public void onInterstitialAdLoaded(Yodo1MasInterstitialAd ad) {

            }

            @Override
            public void onInterstitialAdFailedToLoad(Yodo1MasInterstitialAd ad, @NonNull Yodo1MasError error) {

            }

            @Override
            public void onInterstitialAdOpened(Yodo1MasInterstitialAd ad) {

            }

            @Override
            public void onInterstitialAdFailedToOpen(Yodo1MasInterstitialAd ad, @NonNull Yodo1MasError error) {
                if (currentClick.equalsIgnoreCase("wifi")) {
                    currentClick = "";
                    startActivity(new Intent(MainActivity.this, FindActivity.class));
                } else {
                    currentClick = "";
                    startActivity(new Intent(MainActivity.this, RemoteViewActivity.class).putExtra("brand_name", "Vizio"));
                }
            }

            @Override
            public void onInterstitialAdClosed(Yodo1MasInterstitialAd ad) {
                Yodo1MasInterstitialAd.getInstance().loadAd(MainActivity.this);
                if (currentClick.equalsIgnoreCase("wifi")) {
                    currentClick = "";
                    startActivity(new Intent(MainActivity.this, FindActivity.class));
                } else {
                    currentClick = "";
                    startActivity(new Intent(MainActivity.this, RemoteViewActivity.class).putExtra("brand_name", "Vizio"));
                }
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
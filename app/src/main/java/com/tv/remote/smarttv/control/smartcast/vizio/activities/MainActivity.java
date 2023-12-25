package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import static com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.AppExtKt.permissionsChecker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.tv.remote.smarttv.control.smartcast.vizio.BuildConfig;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.FindActivity;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityMainBinding;
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.AppExtKt;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    String[] array;


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            array = new String[]{
                    android.Manifest.permission.READ_MEDIA_AUDIO,
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_VIDEO
            };
        } else {
            array = new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
        binding.cvWifiRemote.setOnClickListener(v -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
            } else {
                startActivity(new Intent(MainActivity.this, FindActivity.class));
            }
        });

        binding.cvIrRemote.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RemoteViewActivity.class).putExtra("brand_name", "Vizio")));

        binding.cvScreenMirror.setOnClickListener(v -> startActivity(new Intent(this, ConnectionActivity.class)));

        binding.cvRateUs.setOnClickListener(v -> startActivity(new Intent(this, GuideActivity.class)));
        binding.ivMenu.setOnClickListener(v -> AppExtKt.openActivity(MainActivity.this, SideMenuActivity.class));
        binding.photosBtn.setOnClickListener(view -> checkPermission(PhotosActivity.class));
        binding.videosBtn.setOnClickListener(view -> checkPermission(VideosActivity.class));
        binding.audiosBtn.setOnClickListener(view -> checkPermission(AudiosActivity.class));
        binding.adLayout.adCallToAction.setOnClickListener(v -> openLink(getString(R.string.adLink)));

    }

    private void checkPermission(Class<? extends Activity> clazz) {
        Permissions.check(this, array, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                AppExtKt.openActivity(MainActivity.this, clazz);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                AppExtKt.showToast(MainActivity.this, "Permission Denied!");
            }
        });

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
        startActivity(new Intent(this, BackActivity.class));
    }
}
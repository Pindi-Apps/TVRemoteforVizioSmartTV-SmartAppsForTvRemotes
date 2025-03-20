package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.Window;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.FindActivity;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityMainBinding;
import com.tv.remote.smarttv.control.smartcast.vizio.general.PermissionHelper;
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.AppExtKt;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String[] array;
    ActivityMainBinding binding;
    private InterstitialAd mInterstitialAd;
    private PermissionHelper permissionHelper;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void connectWifi() {
        Dialog wifiDialog = new Dialog(this);
        try {
            if (wifiDialog.getWindow() != null) {
                wifiDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        } catch (Exception ne) {
            ne.printStackTrace();
        }
        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        wifiDialog.setContentView(R.layout.network_not_available);
        wifiDialog.findViewById(R.id.connect_wifi_button).setOnClickListener(v -> {
            try {
                wifiDialog.dismiss();
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        wifiDialog.show();
    }


    protected boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        return networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadInterstitial();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            array = new String[]{android.Manifest.permission.READ_MEDIA_AUDIO, android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO};
        } else {
            array = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }


        binding.ivCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.connectableDevice == null) {
                    if (isNetworkAvailable(MainActivity.this)) {
                        AppExtKt.openActivity(MainActivity.this, CastSearchActivity.class);
                    } else {
                        connectWifi();
                    }
                } else {

                    Dialog wifiDialog = new Dialog(MainActivity.this);
                    try {
                        if (wifiDialog.getWindow() != null) {
                            wifiDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        }
                    } catch (Exception ne) {
                        ne.printStackTrace();
                    }
                    wifiDialog.setContentView(R.layout.disconnect_cast_connection);

                    wifiDialog.findViewById(R.id.btnNo).setOnClickListener(v1 -> wifiDialog.dismiss());

                    wifiDialog.findViewById(R.id.btnDisconnect).setOnClickListener(v12 -> {
                        wifiDialog.dismiss();
                        if (AppConstant.connectableDevice != null) {
                            AppConstant.connectableDevice.disconnect();
                            AppConstant.connectableDevice = null;
                        }
                    });

                    wifiDialog.show();


                }
            }
        });

        binding.cvWifiRemote.setOnClickListener(v -> {
            if (mInterstitialAd != null) {
                Dialog progressDialog = new Dialog(MainActivity.this);
                try {
                    if (progressDialog.getWindow() != null) {
                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                    progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    progressDialog.setContentView(R.layout.ad_loading);
                    progressDialog.setCancelable(false);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (!MainActivity.this.isFinishing()){
                    progressDialog.show();
                }

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (!MainActivity.this.isFinishing()){
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        mInterstitialAd.show(MainActivity.this);
                    }
                }, 1200);
            } else {
                if (isNetworkAvailable(MainActivity.this)) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, FindActivity.class));
                } else {
                    connectWifi();
                }
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

    private final ActivityResultLauncher<String[]> resultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                permissionHelper.handlePermissionsResult(result);
            });

    PermissionHelper.Callback permissionCallback = new PermissionHelper.Callback() {
        @Override
        public void onPermissionResult(boolean isGranted) {
            if (isGranted && targetActivity != null) {
                Intent intent = new Intent(MainActivity.this, targetActivity);
                startActivity(intent);
            }
        }
    };

    private Class<? extends Activity> targetActivity;

    private void checkPermission(Class<? extends Activity> clazz) {
        targetActivity = clazz;
        permissionHelper = new PermissionHelper(this, array, resultLauncher, permissionCallback);
        permissionHelper.checkAndRequestPermissions();
    }

    private void openLink(String link) {
        try {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void loadInterstitial() {
        InterstitialAd.load(this, getString(R.string.adMobInterstitial), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
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
                        if (isNetworkAvailable(MainActivity.this)) {
                            MainActivity.this.startActivity(new Intent(MainActivity.this, FindActivity.class));
                        } else {
                            connectWifi();
                        }

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.this.startActivity(new Intent(this, BackActivity.class));
    }
}
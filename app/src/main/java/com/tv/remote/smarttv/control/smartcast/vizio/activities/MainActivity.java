package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import static com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.AppExtKt.permissionsChecker;

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
import android.provider.Settings;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.tv.remote.smarttv.control.smartcast.vizio.BuildConfig;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant;
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
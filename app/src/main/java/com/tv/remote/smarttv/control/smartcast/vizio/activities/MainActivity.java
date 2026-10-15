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

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant;
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.FindActivity;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityMainBinding;
import com.tv.remote.smarttv.control.smartcast.vizio.general.GoogleMobileAdsConsentManager;
import com.tv.remote.smarttv.control.smartcast.vizio.general.PermissionHelper;
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.AppExtKt;

public class MainActivity extends AppCompatActivity {

    String[] array;
    ActivityMainBinding binding;
    private static final String KEY_TARGET_ACTIVITY = "key_target_activity";

    private InterstitialAd mInterstitialAd;
    private PermissionHelper permissionHelper;
    private final Handler adHandler = new Handler(Looper.getMainLooper());
    private Dialog adProgressDialog;
    private Runnable showAdRunnable;

    @Override
    protected void onDestroy() {
        // The delayed ad runnable must not outlive this activity's window (e.g. on recreation).
        if (showAdRunnable != null) {
            adHandler.removeCallbacks(showAdRunnable);
            showAdRunnable = null;
        }
        dismissAdProgressDialog();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (targetActivity != null) {
            outState.putString(KEY_TARGET_ACTIVITY, targetActivity.getName());
        }
    }

    private void dismissAdProgressDialog() {
        if (adProgressDialog != null && adProgressDialog.isShowing()) {
            adProgressDialog.dismiss();
        }
        adProgressDialog = null;
    }

    private void openWifiRemote() {
        if (isNetworkAvailable(MainActivity.this)) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, FindActivity.class));
        } else {
            connectWifi();
        }
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

        loadInterstitial();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            array = new String[]{android.Manifest.permission.READ_MEDIA_AUDIO, android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO};
        } else {
            array = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        // Created in onCreate so a permission result delivered to a recreated activity always has a handler.
        permissionHelper = new PermissionHelper(this, array, resultLauncher, permissionCallback);
        if (savedInstanceState != null) {
            String targetName = savedInstanceState.getString(KEY_TARGET_ACTIVITY);
            if (targetName != null) {
                try {
                    targetActivity = Class.forName(targetName).asSubclass(Activity.class);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
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
            if (showAdRunnable != null) {
                return;
            }
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
                    adProgressDialog = progressDialog;
                }

                showAdRunnable = () -> {
                    showAdRunnable = null;
                    dismissAdProgressDialog();
                    if (MainActivity.this.isFinishing()) {
                        return;
                    }
                    InterstitialAd interstitialAd = mInterstitialAd;
                    if (interstitialAd != null) {
                        interstitialAd.show(MainActivity.this);
                    } else {
                        openWifiRemote();
                    }
                };
                adHandler.postDelayed(showAdRunnable, 1200);
            } else {
                openWifiRemote();
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MainActivity.this.startActivity(new Intent(MainActivity.this, BackActivity.class));
            }
        });
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
        if (!GoogleMobileAdsConsentManager.Companion.getInstance(getApplicationContext()).getCanRequestAds()) {
            return;
        }
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
                        openWifiRemote();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mInterstitialAd = null;
                        openWifiRemote();
                    }
                });
            }
        });
    }
}
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
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.tv.remote.smarttv.control.smartcast.vizio.BuildConfig;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private NativeAd mNativeAd;
    private InterstitialAd mInterstitialAd;



    private void loadNative() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admobNativeId));
        builder.forNativeAd( forNativeAd -> {
            boolean isDestroy = false;
            isDestroy = this.isDestroyed();
            if (isDestroy || this.isFinishing() || this.isChangingConfigurations()){
                forNativeAd.destroy();
            }else {
                if (this.mNativeAd != null){
                    this.mNativeAd.destroy();
                }
                this.mNativeAd = forNativeAd;
                binding.nativeAd.removeAllViews();
                binding.nativeAdPlaceHolder.llRoot.setVisibility(View.GONE);
                NativeAdView view = (NativeAdView) LayoutInflater.from(this).inflate(R.layout.native_ad, null);
                if (view != null){
                    populateNativeAd(forNativeAd , view);
                    binding.nativeAd.addView(view);
                }
            }
        });
        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);
        builder.build().loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAd(NativeAd nativeAd,NativeAdView adView){
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (nativeAd.getBody() == null){
            adView.getBodyView().setVisibility(View.INVISIBLE);
        }else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        adView.setNativeAd(nativeAd);
        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {
            VideoController vc = nativeAd.getMediaContent().getVideoController();
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadNative();
        loadInterstitial();

        binding.nav.navRate.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))));

        binding.nav.navMoreApps.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW ,
                Uri.parse(getString(R.string.moreAppsLink)))));

        binding.cvWifiRemote.setOnClickListener(v -> {
            if (mInterstitialAd != null){
                mInterstitialAd.show(this);
            }else {
                startActivity(new Intent(MainActivity.this, WifiRemoteActivity.class));
            }
        });

        binding.cvIrRemote.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RemoteViewActivity.class).putExtra("brand_name", "Vizio")));

        binding.cvScreenMirror.setOnClickListener(v -> {
            try {
                startActivity(new Intent("android.settings.CAST_SETTINGS"));
            } catch (Exception exception1) {
                Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
            }
        });

        binding.ivPrivacy.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(getString(R.string.privacyPolicyLink)))));
        binding.cvRateUs.setOnClickListener(v -> {
            startActivity(new Intent(this , GuideActivity.class));
//            startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
        });
        binding.ivMenu.setOnClickListener(v -> {
            binding.drawLayout.openDrawer(GravityCompat.START);
        });
        binding.nav.navHome.setOnClickListener(v -> binding.drawLayout.closeDrawer(GravityCompat.START));
    }

    private void loadInterstitial() {
        InterstitialAd.load(this, getString(R.string.admobInterstitialId), new AdRequest.Builder().build(),
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
                                startActivity(new Intent(MainActivity.this, WifiRemoteActivity.class));
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
            startActivity(new Intent(this , BackActivity.class));
        }
    }
}
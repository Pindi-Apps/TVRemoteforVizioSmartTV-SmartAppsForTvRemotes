package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.tv.remote.smarttv.control.smartcast.vizio.R;

public class ConnectionActivity extends AppCompatActivity {


    private NativeAd mNativeAd = null;


    private void populateNativeAd(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
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


    private void loadNative() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.adMobNativeId));
        builder.forNativeAd(forNativeAd -> {
            boolean isDestroy = false;
            isDestroy = isDestroyed();

            if (isDestroy || isFinishing() || isChangingConfigurations()) {
                forNativeAd.destroy();
            } else {
                if (mNativeAd != null) {
                    mNativeAd.destroy();
                }
                mNativeAd = forNativeAd;
                FrameLayout nativeAd = findViewById(R.id.nativeAd);
                nativeAd.removeAllViews();
                findViewById(R.id.nativeAdPlaceHolder).setVisibility(View.GONE);
                View view = LayoutInflater.from(ConnectionActivity.this).inflate(R.layout.native_ad, null);
                NativeAdView nativeAdView = (NativeAdView) view;
                populateNativeAd(forNativeAd, nativeAdView);
                nativeAd.addView(view);
            }
        }).build();

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);
        builder.build().loadAd(new AdRequest.Builder().build());
    }


    @Override
    protected void onDestroy() {
        if (mNativeAd != null) {
            mNativeAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        loadNative();

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());


        findViewById(R.id.cvConnect).setOnClickListener(v -> {
            try {
                startActivity(new Intent("android.settings.CAST_SETTINGS"));
            } catch (Exception exception1) {
                Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
            }
        });


    }
}
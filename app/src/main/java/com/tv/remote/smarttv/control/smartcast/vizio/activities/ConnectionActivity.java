package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());


        findViewById(R.id.cvConnect).setOnClickListener(v -> {
            try {
                startActivity(new Intent("android.settings.CAST_SETTINGS"));
            } catch (Exception exception1) {
                Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.ad_call_to_action).setOnClickListener(v -> openLink(getString(R.string.adLink)));
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


}
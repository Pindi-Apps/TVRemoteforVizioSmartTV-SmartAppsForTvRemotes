package com.viziotv.smartcast.remote.control.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.viziotv.smartcast.remote.control.R;

public class WifiRemoteActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_remote);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.cvRemote).setVisibility(View.VISIBLE);
                findViewById(R.id.progressSearch).setVisibility(View.GONE);
            }
        }, 2500);
        findViewById(R.id.cvRemote).setOnClickListener(v -> startActivity(new Intent(WifiRemoteActivity.this,WifiRemoteViewActivity.class)));
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }
}
package com.viziotv.smartcast.remote.control.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.viziotv.smartcast.remote.control.R;

public class WifiRemoteActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_remote);
        findViewById(R.id.cvRemote).setOnClickListener(v -> startActivity(new Intent(WifiRemoteActivity.this,WifiRemoteViewActivity.class)));
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }
}
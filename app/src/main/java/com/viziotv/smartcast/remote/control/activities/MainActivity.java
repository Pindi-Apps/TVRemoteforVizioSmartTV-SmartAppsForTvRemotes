package com.viziotv.smartcast.remote.control.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.viziotv.smartcast.remote.control.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.cvWifiRemote.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WifiRemoteActivity.class));
        });
        binding.cvIrRemote.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RemoteViewActivity.class));
        });
        binding.cvScreenMirror.setOnClickListener(v -> {

        });
        binding.cvShareApp.setOnClickListener(v -> {

        });
        binding.ivMenu.setOnClickListener(v -> {
            binding.drawLayout.openDrawer(GravityCompat.START);
        });
        binding.nav.navHome.setOnClickListener(v -> binding.drawLayout.closeDrawer(GravityCompat.START));
    }

    @Override
    public void onBackPressed() {
        if (binding.drawLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }
}
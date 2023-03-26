package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.tv.remote.smarttv.control.smartcast.vizio.BuildConfig;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.nav.navRate.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))));

        binding.nav.navMoreApps.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW ,
                Uri.parse("https://play.google.com/store/apps/dev?id=9133452689932095671"))));



        binding.cvWifiRemote.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WifiRemoteActivity.class));
        });
        binding.cvIrRemote.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RemoteViewActivity.class).putExtra("brand_name", "Vizio"));
        });
        binding.cvScreenMirror.setOnClickListener(v -> {

        });

        binding.ivPrivacy.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("http://www2.mouse-games.com/policy.htm"))));
        binding.cvRateUs.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
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
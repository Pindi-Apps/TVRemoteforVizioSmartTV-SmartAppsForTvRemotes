package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tv.remote.smarttv.control.smartcast.vizio.R;

public class ConnectionActivity extends AppCompatActivity {

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


    }
}
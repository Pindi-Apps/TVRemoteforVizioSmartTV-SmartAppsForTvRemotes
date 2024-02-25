package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.tv.remote.smarttv.control.smartcast.vizio.R;

public class BackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);
        findViewById(R.id.Yes).setOnClickListener(v -> finishAffinity());
        findViewById(R.id.No).setOnClickListener(v -> finish());
    }

}
package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tv.remote.smarttv.control.smartcast.vizio.R;

import java.net.DatagramSocket;


public class FindActivity extends AppCompatActivity {
    public static String e1 = "";
    static ListView listView;
    public static ArrayAdapter arrayAdapter;
    public static TextView headingTextView;
    public static TextView textView;
    public static ProgressBar progressBar;
    public static String n1;
    public static String vizioUrl;

    class adapterClick implements OnItemClickListener {

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            FindActivity.e1 = "";
            FindActivity.e1 = ((DeviceModel) adapterView.getItemAtPosition(i)).hostAddress();
            FindActivity.this.startActivity(new Intent(FindActivity.this.getApplicationContext(), TvRemoteActivity.class));
            Editor edit = FindActivity.this.getSharedPreferences("vizio_url", 0).edit();
            edit.putString("vizio_url_string", FindActivity.e1);
            edit.apply();
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wifi_remote);
        String str = "";
        n1 = getSharedPreferences("vizio_saved_device", 0).getString("vizio_device", str);
        vizioUrl = getSharedPreferences("vizio_url", 0).getString("vizio_url_string", str);
        listView = (ListView) findViewById(R.id.listdevices);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_vizio);
        FindActivity.progressBar = progressBar;
        progressBar.setIndeterminate(true);
        arrayAdapter = new ArrayAdapter(this, R.layout.list_item_vizio);
        FindingAsync.execute(this);
        listView.setOnItemClickListener(new adapterClick());

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    public void onDestroy() {
        DatagramSocket datagramSocket = FindingAsync.datagramSocket;
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        super.onDestroy();
    }

}

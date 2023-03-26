package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.remoteConfig.wifiPackage.ListViewAdapter;
import com.tv.remote.smarttv.control.smartcast.vizio.remoteConfig.wifiPackage.WifiRemoteUtils;
import com.tv.remote.smarttv.control.smartcast.vizio.remoteConfig.wifiPackage.FindVizAsyncTask;

import java.net.DatagramSocket;

public class WifiRemoteActivity extends AppCompatActivity {

    public static String vizUrl = "";

    public static ListView listView;
    public static ListViewAdapter listViewAdapter;
    public static TextView h1;
    public static TextView i1;
    public static ProgressBar progressBar;
    public static String n1;
    public static String o1;

    class e implements View.OnClickListener {
        e() {
        }

        public void onClick(View view) {
            WifiRemoteActivity.vizUrl = WifiRemoteActivity.o1;
            WifiRemoteActivity.this.startActivity(new Intent(WifiRemoteActivity.this.getApplicationContext(), WifiRemoteViewActivity.class));
            Bundle bundle = new Bundle();
            bundle.putString("content_type", "Saved_VizioUTRC");
//            SearchActivity_Vizio.k1.ListViewAdapter("UTRC_Saved_Vizio", SearchActivity_Vizio.n1);
//            SearchActivity_Vizio.k1.a("select_content", bundle);
        }
    }

//    class SetTextData implements AdapterView.OnItemClickListener {
//        SetTextData() {
//        }
//
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
//            WifiRemoteActivity.e1 = "";
//            WifiRemoteActivity.e1 = ((WifiRemoteUtils) adapterView.getItemAtPosition(i)).b();
//            WifiRemoteActivity.this.startActivity(new Intent(WifiRemoteActivity.this.getApplicationContext(), WifiRemoteViewActivity.class));
//            SharedPreferences.Editor edit = WifiRemoteActivity.this.getSharedPreferences("vizio_url", 0).edit();
//            edit.putString("vizio_url_string", WifiRemoteActivity.e1);
//            edit.apply();
//        }
//    }



//    class h implements View.OnClickListener {
//        h() {
//        }
//
//        public void onClick(View view) {
//            WifiRemoteActivity.this.V();
//            Bundle bundle = new Bundle();
//            bundle.putString("item_id", "UTRC_Email_Vizio");
//            bundle.putString("content_type", "UTRC_Email_Button_Vizio");
//            // SearchActivity_Vizio.k1.a("select_content", bundle);
//        }
//    }

//    class i implements View.OnClickListener {
//        i() {
//        }
//
//        public void onClick(View view) {
//            WifiRemoteActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/dev?id=4664062132977048345&hl=en")));
//            Bundle bundle = new Bundle();
//            String str = "UTRC_MoreApps_VIZIO";
//            bundle.putString("item_id", str);
//            bundle.putString("content_type", str);
//            //  SearchActivity_Vizio.k1.a("select_content", bundle);
//        }
//    }

//    class j implements View.OnClickListener {
//        j() {
//        }
//
//        public void onClick(View view) {
//            // SearchActivity_Vizio.this.startActivity(new Intent(SearchActivity_Vizio.this, InAppActivity.class));
//            Bundle bundle = new Bundle();
//            String str = "UTRC_InAppActivity_VIZIO";
//            bundle.putString("item_id", str);
//            bundle.putString("content_type", str);
//            // SearchActivity_Vizio.k1.a("begin_checkout", bundle);
//        }
//    }


    /* Access modifiers changed, original: protected */
//    public void V() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("mailto:support@smartapps.co?subject=");
//        stringBuilder.append(Uri.encode("VIZIO (Universal TV Remote Control)"));
//        stringBuilder.append("&body=");
//        stringBuilder.append(Uri.encode(""));
//        try {
//            startActivity(new Intent("android.intent.action.SENDTO", Uri.parse(stringBuilder.toString())));
//        } catch (ActivityNotFoundException unused) {
//            Toast.makeText(this, "There is no email App installed.", Toast.LENGTH_SHORT).show();
//        }
//    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
//        removeNativeAds = getSharedPreferences("utrc_tv_remove_native_ads", 0).getBoolean("utrc_tv_remove_native_ads_id", false);
//        m1 = getSharedPreferences("utrc_tv_remove_interstitial_ads", 0).getBoolean("utrc_tv_remove_interstitial_ads_id", false);
        String str = "";
        n1 = getSharedPreferences("vizio_saved_device", 0).getString("vizio_device", str);
        o1 = getSharedPreferences("vizio_url", 0).getString("vizio_url_string", str);
        super.onCreate(bundle);
        setContentView(R.layout.activity_wifi_remote);
        //  TextView textView = (TextView) findViewById(d.T);
        //  p1 = (LinearLayout) findViewById(d.x);
        // TextView textView2 = (TextView) findViewById(d.R);
//        String str2 = o1;
//        if (!(str2 == null || str2.equals(str))) {
//            p1.setVisibility(View.INVISIBLE);
//            textView.setText(n1);
//            textView2.setText(o1);
//            p1.setOnClickListener(new e());
//        }

        //  k1 = FirebaseAnalytics.getInstance(this);
        listView = (ListView) findViewById(R.id.listdevices);
        progressBar =  findViewById(R.id.progressBar_vizio);
//        WifiRemoteActivity.progressBar = progressBar;
        progressBar.setIndeterminate(true);
//        this.vizAndroid = 0;

        listViewAdapter = new ListViewAdapter(this, R.layout.rv_search_list);
        FindVizAsyncTask.executeWithActivity(this);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            WifiRemoteActivity.vizUrl = "";
            WifiRemoteActivity.vizUrl = ((WifiRemoteUtils) parent.getItemAtPosition(position)).b();
            WifiRemoteActivity.this.startActivity(new Intent(WifiRemoteActivity.this.getApplicationContext(), WifiRemoteViewActivity.class));
            SharedPreferences.Editor edit = WifiRemoteActivity.this.getSharedPreferences("vizio_url", 0).edit();
            edit.putString("vizio_url_string", WifiRemoteActivity.vizUrl);
            edit.apply();
        });
    }

    public void onDestroy() {
        DatagramSocket datagramSocket = FindVizAsyncTask.c;
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        super.onDestroy();
    }

    public void onResume() {
//        this.vizPreference = getSharedPreferences("Vizio_prefs", 0);
//        this.b1 = sharedPreferences;
//        this.vizAndroid = this.vizPreference.getInt("vizioAndroid", 0);
//        removeNativeAds = getSharedPreferences("utrc_tv_remove_native_ads", 0).getBoolean("utrc_tv_remove_native_ads_id", false);
//        LinearLayout linearLayout = (LinearLayout) findViewById(d.w);
//        if (removeNativeAds) {
//            linearLayout.setVisibility(View.INVISIBLE);
//            this.a1.setVisibility(View.GONE);
//        }
//        m1 = getSharedPreferences("utrc_tv_remove_interstitial_ads", 0).getBoolean("utrc_tv_remove_interstitial_ads_id", false);
        super.onResume();
    }


}
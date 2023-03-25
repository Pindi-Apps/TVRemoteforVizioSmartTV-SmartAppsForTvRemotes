package com.viziotv.smartcast.remote.control.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.viziotv.smartcast.remote.control.R;
import com.viziotv.smartcast.remote.control.activities.wifiPackage.b;
import com.viziotv.smartcast.remote.control.activities.wifiPackage.c;
import com.viziotv.smartcast.remote.control.activities.wifiPackage.d;
import com.viziotv.smartcast.remote.control.activities.wifiPackage.gsTv;

import java.net.DatagramSocket;

public class WifiRemoteActivity extends AppCompatActivity {

    public static String e1 = "";
    public static ListView f1;
    public static b g1;
    public static TextView h1;
    public static TextView i1;
    public static ProgressBar j1;
    public static boolean l1;
    public static boolean m1;
    public static String n1;
    public static String o1;
    public static LinearLayout p1;
    private Button X0;
    private Button Y0;
    private Button Z0;
    private Button a1;
    SharedPreferences b1;
    int c1;
    LinearLayout d1;

    class e implements View.OnClickListener {
        e() {
        }

        public void onClick(View view) {
            WifiRemoteActivity.e1 = WifiRemoteActivity.o1;
            WifiRemoteActivity.this.startActivity(new Intent(WifiRemoteActivity.this.getApplicationContext(), WifiRemoteViewActivity.class));
            Bundle bundle = new Bundle();
            bundle.putString("content_type", "Saved_VizioUTRC");
//            SearchActivity_Vizio.k1.b("UTRC_Saved_Vizio", SearchActivity_Vizio.n1);
//            SearchActivity_Vizio.k1.a("select_content", bundle);
        }
    }

    class f implements AdapterView.OnItemClickListener {
        f() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            WifiRemoteActivity.e1 = "";
            WifiRemoteActivity.e1 = ((c) adapterView.getItemAtPosition(i)).b();
            WifiRemoteActivity.this.startActivity(new Intent(WifiRemoteActivity.this.getApplicationContext(), WifiRemoteViewActivity.class));
            SharedPreferences.Editor edit = WifiRemoteActivity.this.getSharedPreferences("vizio_url", 0).edit();
            edit.putString("vizio_url_string", WifiRemoteActivity.e1);
            edit.apply();
        }
    }



    class h implements View.OnClickListener {
        h() {
        }

        public void onClick(View view) {
            WifiRemoteActivity.this.V();
            Bundle bundle = new Bundle();
            bundle.putString("item_id", "UTRC_Email_Vizio");
            bundle.putString("content_type", "UTRC_Email_Button_Vizio");
            // SearchActivity_Vizio.k1.a("select_content", bundle);
        }
    }

    class i implements View.OnClickListener {
        i() {
        }

        public void onClick(View view) {
            WifiRemoteActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/dev?id=4664062132977048345&hl=en")));
            Bundle bundle = new Bundle();
            String str = "UTRC_MoreApps_VIZIO";
            bundle.putString("item_id", str);
            bundle.putString("content_type", str);
            //  SearchActivity_Vizio.k1.a("select_content", bundle);
        }
    }

    class j implements View.OnClickListener {
        j() {
        }

        public void onClick(View view) {
            // SearchActivity_Vizio.this.startActivity(new Intent(SearchActivity_Vizio.this, InAppActivity.class));
            Bundle bundle = new Bundle();
            String str = "UTRC_InAppActivity_VIZIO";
            bundle.putString("item_id", str);
            bundle.putString("content_type", str);
            // SearchActivity_Vizio.k1.a("begin_checkout", bundle);
        }
    }


    /* Access modifiers changed, original: protected */
    public void V() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mailto:support@smartapps.co?subject=");
        stringBuilder.append(Uri.encode("VIZIO (Universal TV Remote Control)"));
        stringBuilder.append("&body=");
        stringBuilder.append(Uri.encode(""));
        try {
            startActivity(new Intent("android.intent.action.SENDTO", Uri.parse(stringBuilder.toString())));
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(this, "There is no email App installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        l1 = getSharedPreferences("utrc_tv_remove_native_ads", 0).getBoolean("utrc_tv_remove_native_ads_id", false);
        m1 = getSharedPreferences("utrc_tv_remove_interstitial_ads", 0).getBoolean("utrc_tv_remove_interstitial_ads_id", false);
        String str = "";
        n1 = getSharedPreferences("vizio_saved_device", 0).getString("vizio_device", str);
        o1 = getSharedPreferences("vizio_url", 0).getString("vizio_url_string", str);
        super.onCreate(bundle);
        setContentView(R.layout.activity_wifi_remote);
        //  TextView textView = (TextView) findViewById(d.T);
        //  p1 = (LinearLayout) findViewById(d.x);
        // TextView textView2 = (TextView) findViewById(d.R);
        String str2 = o1;
//        if (!(str2 == null || str2.equals(str))) {
//            p1.setVisibility(View.INVISIBLE);
//            textView.setText(n1);
//            textView2.setText(o1);
//            p1.setOnClickListener(new e());
//        }

        //  k1 = FirebaseAnalytics.getInstance(this);
        f1 = (ListView) findViewById(R.id.listdevices);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_vizio);
        j1 = progressBar;
        progressBar.setIndeterminate(true);
        this.c1 = 0;

        g1 = new b(this, R.layout.rv_search_list);
        gsTv.a(this);
        f1.setOnItemClickListener(new f());
    }

    public void onDestroy() {
        DatagramSocket datagramSocket = gsTv.c;
        if (datagramSocket != null) {
            datagramSocket.close();
        }
        super.onDestroy();
    }

    public void onResume() {
        SharedPreferences sharedPreferences = getSharedPreferences("Vizio_prefs", 0);
        this.b1 = sharedPreferences;
        int i = sharedPreferences.getInt("vizioAndroid", 0);
        this.c1 = i;
        l1 = getSharedPreferences("utrc_tv_remove_native_ads", 0).getBoolean("utrc_tv_remove_native_ads_id", false);
        LinearLayout linearLayout = (LinearLayout) findViewById(d.w);
        if (l1) {
            linearLayout.setVisibility(View.INVISIBLE);
            this.a1.setVisibility(View.GONE);
        }
        m1 = getSharedPreferences("utrc_tv_remove_interstitial_ads", 0).getBoolean("utrc_tv_remove_interstitial_ads_id", false);
        super.onResume();
    }


}
package com.tv.remote.smarttv.control.smartcast.vizio.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.tv.remote.smarttv.control.smartcast.vizio.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WifiRemoteViewActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static String L1 = "https://";
    public static SharedPreferences M1;
    public static SharedPreferences N1;
    public static String O1;
    public static String P1;
    public Button A1;
    public Button B1;
    public Button C1;
    public Button D1;
    public Button E1;
    public Button F1;
    public Button G1;
    boolean H1;
    private Vibrator vibrator;
    final HostnameVerifier K1 = new z(this);
    String X0 = "/pairing/start";
    String Y0 = "/pairing/pair";
    String Z0 = "/key_command/";
    String a1 = "/app/launch";
    String b1 = "";
    String c1 = "VizMaticsMote";
    Dialog d1;
    EditText e1;
    String f1;
    public Button g1;
    public Button h1;
    public Button i1;
    public Button j1;
    public Button k1;
    public Button l1;
    public Button m1;
    public Button n1;
    public Button o1;
    public Button p1;
    public Button powerq1;
    public Button r1;
    public CheckBox mutes1;
    public Button volups1;
    public Button u1;
    public Button v1;
    public Button w1;
    public Button x1;
    public Button y1;
    public Button z1;

    static class a0 implements X509TrustManager {
        a0() {
        }

        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
            Log.d("Host_Name__CheckClient:", "checkClientTrusted");
        }

        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
            Log.d("Host_Name_Server:", "checkServerTrusted");
        }

        public X509Certificate[] getAcceptedIssuers() {
            Log.d("Host_Name_getAccepted:", "X509Certificate getAcceptedIssuers");
            return new X509Certificate[0];
        }
    }

    class a implements View.OnClickListener {
        a() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class b implements View.OnClickListener {
        b() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class c implements View.OnClickListener {
        c() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class d implements View.OnClickListener {
        d() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class e implements View.OnClickListener {
        e() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class f implements View.OnClickListener {
        f() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class g0 implements View.OnClickListener {
        g0() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class g implements View.OnClickListener {
        g() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class h implements View.OnClickListener {
        h() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class i implements View.OnClickListener {
        i() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class j0 implements View.OnClickListener {
        j0() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.S(5, 4, "KEYPRESS");
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class j implements View.OnClickListener {
        j() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class k0 implements View.OnClickListener {
        k0() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class k implements View.OnClickListener {
        k() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class l0 implements View.OnClickListener {
        l0() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class l implements View.OnClickListener {
        l() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class m0 implements View.OnClickListener {
        m0() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class m implements View.OnClickListener {
        m() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }


    private void playVibrate() {
        if (this.vibrator == null) {
            this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            this.vibrator.vibrate(5);
        } else {
            this.vibrator.vibrate(5);
        }
    }

    class n0 implements View.OnClickListener {
        n0() {
        }

        public void onClick(View view) {
            playVibrate();
        }
    }

    class n implements View.OnClickListener {
        n() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class o0 implements View.OnClickListener {
        o0() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class o implements View.OnClickListener {
        o() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class p implements View.OnClickListener {
        p() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class q implements View.OnClickListener {
        q() {
        }

        public void onClick(View view) {
            boolean z;
            WifiRemoteViewActivity WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
            String str = "KEYPRESS";
            if (WifiRemoteViewActivity_.H1) {
                WifiRemoteViewActivity_.S(2, 3, str);
                playVibrate();
                WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
                z = false;
            } else {
                WifiRemoteViewActivity_.S(2, 2, str);
                playVibrate();
                WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
                z = true;
            }
            WifiRemoteViewActivity_.H1 = z;
        }
    }

    class r implements View.OnClickListener {
        r() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class s implements View.OnClickListener {
        s() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class t implements Runnable {
        t() {
        }

        public void run() {
            String str = "application/json";
            String str2 = "PAIRING_REQ_TOKEN";
            try {
                String format = String.format("%s%s", new Object[]{WifiRemoteViewActivity.L1, WifiRemoteViewActivity.this.X0});
                WifiRemoteViewActivity WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(WifiRemoteViewActivity.this.c1);
                stringBuilder.append("(");
                stringBuilder.append(Build.MODEL);
                stringBuilder.append(")");
                WifiRemoteViewActivity_.b1 = stringBuilder.toString();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("{\"DEVICE_ID\":\"");
                stringBuilder2.append(WifiRemoteViewActivity.this.b1);
                stringBuilder2.append(111);
                stringBuilder2.append("\",\"DEVICE_NAME\":\"");
                stringBuilder2.append(WifiRemoteViewActivity.this.b1);
                stringBuilder2.append("\"}");
                String stringBuilder3 = stringBuilder2.toString();
                URL url = new URL(format);
                WifiRemoteViewActivity.m0();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(WifiRemoteViewActivity.this.K1);
                httpsURLConnection.setRequestMethod("PUT");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.addRequestProperty("Content-Type", str);
                httpsURLConnection.setRequestProperty("Accept", str);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(stringBuilder3);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                if (httpsURLConnection.getResponseCode() != 500) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                    while (true) {
                        stringBuilder3 = bufferedReader.readLine();
                        if (stringBuilder3 == null) {
                            break;
                        }
                        JSONObject jSONObject = new JSONObject(stringBuilder3);
                        System.out.println(jSONObject);
                        if (stringBuilder3.contains(str2)) {
                            stringBuilder3 = jSONObject.getJSONObject("ITEM").getString(str2);
                            WifiRemoteViewActivity.O1 = stringBuilder3;
                            WifiRemoteViewActivity.this.R(str2, stringBuilder3);
                        }
                        WifiRemoteViewActivity.this.k0();
                    }
                }
                httpsURLConnection.disconnect();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            } catch (JSONException e4) {
                e4.printStackTrace();
            }
        }
    }

    class u implements Runnable {

        class a implements Runnable {
            a() {
            }

            public void run() {
                // l0();
            }
        }

        u() {
        }

        public void run() {
            String str = "application/json";
            String str2 = "AUTH_TOKEN";
            try {
                WifiRemoteViewActivity WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
                if (WifiRemoteViewActivity_.e1 != null)
                    WifiRemoteViewActivity_.f1 = WifiRemoteViewActivity_.e1.getText().toString();
                String format = String.format("%s%s", new Object[]{WifiRemoteViewActivity.L1, WifiRemoteViewActivity.this.Y0});
                WifiRemoteViewActivity WifiRemoteViewActivity_2 = WifiRemoteViewActivity.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(WifiRemoteViewActivity.this.c1);
                stringBuilder.append("(");
                stringBuilder.append(Build.MODEL);
                stringBuilder.append(")");
                WifiRemoteViewActivity_2.b1 = stringBuilder.toString();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("{\"DEVICE_ID\": \"");
                stringBuilder2.append(WifiRemoteViewActivity.this.b1);
                stringBuilder2.append(111);
                stringBuilder2.append("\",\"CHALLENGE_TYPE\": 1,\"RESPONSE_VALUE\": \"");
                stringBuilder2.append(WifiRemoteViewActivity.this.f1);
                stringBuilder2.append("\",\"PAIRING_REQ_TOKEN\":");
                stringBuilder2.append(WifiRemoteViewActivity.O1);
                stringBuilder2.append("}");
                String stringBuilder3 = stringBuilder2.toString();
                URL url = new URL(format);
                WifiRemoteViewActivity.m0();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(WifiRemoteViewActivity.this.K1);
                httpsURLConnection.setRequestMethod("PUT");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.addRequestProperty("Content-Type", str);
                httpsURLConnection.setRequestProperty("Accept", str);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(stringBuilder3);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                if (httpsURLConnection.getResponseCode() == 403) {
                    WifiRemoteViewActivity.this.k0();
                    return;
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                while (true) {
                    stringBuilder3 = bufferedReader.readLine();
                    if (stringBuilder3 != null) {
                        JSONObject jSONObject = new JSONObject(stringBuilder3);
                        System.out.println(jSONObject);
                        if (stringBuilder3.contains(str2)) {
                            String string = jSONObject.getJSONObject("ITEM").getString(str2);
                            WifiRemoteViewActivity.P1 = string;
                            WifiRemoteViewActivity.this.R(str2, string);
                            SharedPreferences.Editor edit = WifiRemoteViewActivity.this.getSharedPreferences("vizio_rate_us", 0).edit();
                            edit.putInt("vizio_rate_us_id", 5);
                            edit.commit();
                            new Handler(Looper.getMainLooper()).postDelayed(new a(), 60000);
                        }
                        if (stringBuilder3.contains("Pairing request denied")) {
                            WifiRemoteViewActivity.this.k0();
                        }
                        if (stringBuilder3.contains("Invalid parameter")) {
                            WifiRemoteViewActivity.this.T();
                        }
                    } else {
                        httpsURLConnection.disconnect();
                        return;
                    }
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            } catch (JSONException e4) {
                e4.printStackTrace();
            }
        }
    }

    class v implements View.OnClickListener {
        v() {
        }

        public void onClick(View view) {
            WifiRemoteViewActivity.this.vibrator.vibrate(5);
        }
    }

    class w extends Thread {

        class aaa implements Runnable {

            class aa implements View.OnClickListener {
                aa() {
                }

                public void onClick(View view) {
                    WifiRemoteViewActivity.this.d1.dismiss();
                }
            }

            class b implements View.OnClickListener {

                class aaaa implements Runnable {
                    aaaa() {
                    }

                    public void run() {

                        //l0();
                    }
                }

                b() {
                }

                public void onClick(View view) {
                    WifiRemoteViewActivity.this.U();
                    WifiRemoteViewActivity.this.d1.dismiss();
                    new Handler(Looper.getMainLooper()).postDelayed(new aaa(), 60000);
                }
            }

            aaa() {
            }

            public void run() {
                WifiRemoteViewActivity.this.d1 = new Dialog(WifiRemoteViewActivity.this);
                WifiRemoteViewActivity.this.d1.setContentView(R.layout.fragment_pairing);
                WifiRemoteViewActivity.this.d1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                if (Build.VERSION.SDK_INT >= 3) {
                    WifiRemoteViewActivity.this.d1.getWindow().setSoftInputMode(5);
                }
                MaterialButton button = (MaterialButton) WifiRemoteViewActivity.this.d1.findViewById(R.id.pairing_ok);
                MaterialButton button2 = (MaterialButton) WifiRemoteViewActivity.this.d1.findViewById(R.id.pairing_cancel);
                WifiRemoteViewActivity WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
                WifiRemoteViewActivity_.e1 = (EditText) WifiRemoteViewActivity_.d1.findViewById(R.id.pairing_pin);
                button2.setOnClickListener(new aa());
                button.setOnClickListener(new b());
                WifiRemoteViewActivity.this.d1.setCancelable(false);
                WifiRemoteViewActivity.this.d1.show();
            }
        }

        w() {
        }

        public void run() {
            WifiRemoteViewActivity.this.runOnUiThread(new aaa());
        }
    }

    class x implements Runnable {
        final /* synthetic */ int F0;
        final /* synthetic */ int G0;
        final /* synthetic */ String H0;

        x(int i, int i2, String str) {
            this.F0 = i;
            this.G0 = i2;
            this.H0 = str;
        }

        public void run() {
            String str = "application/json";
            try {
                String format = String.format("%s%s", new Object[]{WifiRemoteViewActivity.L1, WifiRemoteViewActivity.this.Z0});
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{\"KEYLIST\": [{\"CODESET\": \"");
                stringBuilder.append(this.F0);
                stringBuilder.append("\", \"CODE\":");
                stringBuilder.append(this.G0);
                stringBuilder.append(", \"ACTION\": \"");
                stringBuilder.append(this.H0);
                stringBuilder.append("\" }]}");
                String stringBuilder2 = stringBuilder.toString();
                Log.d("Command", stringBuilder2);
                URL url = new URL(format);
                WifiRemoteViewActivity.m0();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(WifiRemoteViewActivity.this.K1);
                httpsURLConnection.setRequestMethod("PUT");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.addRequestProperty("Content-Type", str);
                httpsURLConnection.setRequestProperty("Accept", str);
                httpsURLConnection.setRequestProperty("Auth", WifiRemoteViewActivity.P1);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(stringBuilder2);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                if (httpsURLConnection.getResponseCode() == 403) {
                    WifiRemoteViewActivity.this.k0();
                    return;
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                while (true) {
                    stringBuilder2 = bufferedReader.readLine();
                    if (stringBuilder2 != null) {
                        System.out.println(new JSONObject(stringBuilder2));
                    } else {
                        httpsURLConnection.disconnect();
                        return;
                    }
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            } catch (JSONException e4) {
                e4.printStackTrace();
            }
        }
    }

    class y implements Runnable {
        final /* synthetic */ String F0;
        final /* synthetic */ int G0;
        final /* synthetic */ String H0;

        y(String str, int i, String str2) {
            this.F0 = str;
            this.G0 = i;
            this.H0 = str2;
        }

        public void run() {
            String str = "application/json";
            try {
                String format = String.format("%s%s", new Object[]{WifiRemoteViewActivity.L1, WifiRemoteViewActivity.this.a1});
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{\"VALUE\": {\"MESSAGE\": ");
                stringBuilder.append(this.F0);
                stringBuilder.append(",\"NAME_SPACE\":");
                stringBuilder.append(this.G0);
                stringBuilder.append(" ,\"APP_ID\": \"");
                stringBuilder.append(this.H0);
                stringBuilder.append("\"}}");
                String stringBuilder2 = stringBuilder.toString();
                Log.d("Json", stringBuilder2);
                URL url = new URL(format);
                WifiRemoteViewActivity.m0();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(WifiRemoteViewActivity.this.K1);
                httpsURLConnection.setRequestMethod("PUT");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.addRequestProperty("Content-Type", str);
                httpsURLConnection.setRequestProperty("Accept", str);
                httpsURLConnection.setRequestProperty("Auth", WifiRemoteViewActivity.P1);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(stringBuilder2);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                if (httpsURLConnection.getResponseCode() == 403) {
                    WifiRemoteViewActivity.this.k0();
                    return;
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                while (true) {
                    format = bufferedReader.readLine();
                    if (format != null) {
                        System.out.println(new JSONObject(format));
                    } else {
                        return;
                    }
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            } catch (JSONException e4) {
                e4.printStackTrace();
            }
        }
    }

    class z implements HostnameVerifier {
        z(WifiRemoteViewActivity WifiRemoteViewActivity_) {
        }

        public boolean verify(String str, SSLSession sSLSession) {
            String str2 = "Host Name";
            if (WifiRemoteViewActivity.L1.contains(str)) {
                Log.d(str2, str);
                return true;
            }
            Log.d(str2, WifiRemoteViewActivity.L1);
            return false;
        }
    }


    private void P(String str, int i, String str2) {
        if (P1 != null) {
            new Thread(new y(str, i, str2)).start();
        } else {
            U();
        }
    }

    private void R(String str, String str2) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        M1 = defaultSharedPreferences;
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString(str, str2);
        edit.apply();
        if (str2 == null) {
            onSharedPreferenceChanged(N1, null);
        }
    }

    private void S(int i, int i2, String str) {
        if (P1 != null) {
            new Thread(new x(i, i2, str)).start();
        } else {
            U();
        }
    }

    private void e0() {
        Bundle bundle = new Bundle();
        bundle.putString("content_type", "UTRC_Review_Vizio");
    }

    private void k0() {
        new w().start();
    }

    private static void m0() {
        TrustManager[] trustManagerArr = new TrustManager[]{new a0()};
        try {
            SSLContext instance = SSLContext.getInstance("SSL");
            instance.init(null, trustManagerArr, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(instance.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String Q(String str) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        N1 = defaultSharedPreferences;
        return defaultSharedPreferences.getString(str, null);
    }

    public void T() {
        new Thread(new t()).start();
    }

    public void U() {
        new Thread(new u()).start();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_remote_view);
        L1 = "https://";
        String str = "AUTH_TOKEN";
        P1 = Q(str);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("hello");
        stringBuilder.append(P1);
        Log.d(str, stringBuilder.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(L1);
        stringBuilder2.append(WifiRemoteActivity.vizUrl);
        stringBuilder2.append(":7345");
        str = stringBuilder2.toString();
        L1 = str;
        Log.d("CONNECTIONPATH", str);
        getWindow().addFlags(128);
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        this.H1 = false;
        findViewById(R.id.power_off_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(11, 2, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.volume_up).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(5, 1, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.volume_down).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(5, 0, "KEYPRESS");
            playVibrate();
        });
//        findViewById(R.id.mute_vizio).setOnClickListener(new j0());
        findViewById(R.id.channel_up).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(8, 1, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.channel_down).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(8, 0, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.ok_up).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(3, 8, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.ok_down).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(3, 0, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.ok_left).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(3, 1, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.ok_right).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(3, 7, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.ok_button).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(3, 2, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.back_button).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(4, 0, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.button_source).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(7, 1, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.info_button).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(4, 6, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.button_menu).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(4, 8, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.buttonhome).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(4, 3, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.youtube_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.P("null", 5, "1");
            playVibrate();
        });
        findViewById(R.id.vudu_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.P("\"https://my.vudu.com/castReceiver/index.html?launch-source=app-icon\"", 2, "31");
            playVibrate();
        });
        findViewById(R.id.netflix_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.P("null", 3, "1");
            playVibrate();
        });
        findViewById(R.id.primevideo_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.P("null", 2, "4");
            playVibrate();
        });
        findViewById(R.id.xumo_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.P("{\"CAST_NAMESPACE\":\"urn:x-cast:com.google.cast.media\",\"CAST_MESSAGE\":{\"type\":\"LOAD\",\"media\":{},\"autoplay\":true,\"currentTime\":0,\"customData\":{}}}", 0, "36E1EA1F");
            playVibrate();
        });
        findViewById(R.id.hulu_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.P("null", 2, "3");
            playVibrate();
        });
        findViewById(R.id.redbox_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.P("null", 2, "41");
            playVibrate();
        });
        findViewById(R.id.watchfree_vizio).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.P("null", 2, "22");
            playVibrate();
        });
        findViewById(R.id.play_button).setOnClickListener(v -> {
            boolean z;
            WifiRemoteViewActivity WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
//            String str = "KEYPRESS";
            if (WifiRemoteViewActivity_.H1) {
                WifiRemoteViewActivity_.S(2, 3, "KEYPRESS");
                playVibrate();
                WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
                z = false;
            } else {
                WifiRemoteViewActivity_.S(2, 2, "KEYPRESS");
                playVibrate();
                WifiRemoteViewActivity_ = WifiRemoteViewActivity.this;
                z = true;
            }
            WifiRemoteViewActivity_.H1 = z;
        });
        findViewById(R.id.fforward).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(2, 0, "KEYPRESS");
            playVibrate();
        });
        findViewById(R.id.freverse).setOnClickListener(v -> {
            WifiRemoteViewActivity.this.S(2, 1, "KEYPRESS");
            playVibrate();
        });
        if (P1 == null) {
            T();
        }


    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
    }


}
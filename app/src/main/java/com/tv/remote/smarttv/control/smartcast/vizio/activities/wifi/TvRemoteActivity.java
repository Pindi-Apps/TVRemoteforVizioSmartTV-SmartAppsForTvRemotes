package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ChannelBottomSheetBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class TvRemoteActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {
    static String base = "http://";
    public static SharedPreferences sharedPreferences;
    public static String responseBuilder;
    public static String preferenceToken;
    boolean playToggle;
    private Vibrator vibrator;
    final HostnameVerifier hostnameVerifier = new nameVerifier();
    String pair = "/pairing/pair";
    String stringBuilder = "";
    Dialog pairingDialog;
    EditText pairingEditText;
    String pairString;
    String pairingStart = "/pairing/start";
    String appLaunch = "/app/launch";
    String keyCommand = "/key_command/";


    class okRight implements OnClickListener {
        okRight() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(3, 7, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class okBtn implements OnClickListener {
        okBtn() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(3, 2, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class backButton implements OnClickListener {
        backButton() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(4, 0, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class btnSource implements OnClickListener {
        btnSource() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(7, 1, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class infoButton implements OnClickListener {
        infoButton() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(4, 6, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class buttonMenu implements OnClickListener {
        buttonMenu() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(4, 8, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class volumeDown implements OnClickListener {
        volumeDown() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(5, 0, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class buttonHome implements OnClickListener {
        buttonHome() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(4, 3, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class youtubeVizio implements OnClickListener {
        youtubeVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.P("null", 5, "1");
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class vuduVizio implements OnClickListener {
        vuduVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.P("\"https://my.vudu.com/castReceiver/index.html?launch-source=app-icon\"", 2, "31");
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class Mute implements OnClickListener {
        Mute() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(5, 4, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class netflizVizio implements OnClickListener {
        netflizVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.P("null", 3, "1");
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class channelUp implements OnClickListener {
        channelUp() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(8, 1, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class powerOff implements OnClickListener {
        powerOff() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(11, 2, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class channelDown implements OnClickListener {
        channelDown() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(8, 0, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class primeVideoVizio implements OnClickListener {
        primeVideoVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.P("null", 2, "4");
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class btnUp implements OnClickListener {
        btnUp() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(3, 8, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class xumoVizio implements OnClickListener {
        xumoVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.P("{\"CAST_NAMESPACE\":\"urn:x-cast:com.google.cast.media\",\"CAST_MESSAGE\":{\"type\":\"LOAD\",\"media\":{},\"autoplay\":true,\"currentTime\":0,\"customData\":{}}}", 0, "36E1EA1F");
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class okDown implements OnClickListener {
        okDown() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(3, 0, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class huluVizio implements OnClickListener {
        huluVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.P("null", 2, "3");
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class okLeft implements OnClickListener {
        okLeft() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(3, 1, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class redBoxVizio implements OnClickListener {
        redBoxVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.P("null", 2, "41");
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class watchFreeVizio implements OnClickListener {
        watchFreeVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.P("null", 2, "22");
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class playButtonVizio implements OnClickListener {
        playButtonVizio() {
        }

        public void onClick(View view) {
            boolean playBoolean;
            TvRemoteActivity tvRemoteActivity_ = TvRemoteActivity.this;
            if (tvRemoteActivity_.playToggle) {
                tvRemoteActivity_.pressExecute(2, 3, AppConstant.keyPress);
                TvRemoteActivity.this.vibrator.vibrate(5);
                playBoolean = false;
            } else {
                tvRemoteActivity_.pressExecute(2, 2, AppConstant.keyPress);
                TvRemoteActivity.this.vibrator.vibrate(5);
                tvRemoteActivity_ = TvRemoteActivity.this;
                playBoolean = true;
            }
            tvRemoteActivity_.playToggle = playBoolean;
        }
    }

    class forwardVizio implements OnClickListener {
        forwardVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(2, 0, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class reverseVizio implements OnClickListener {
        reverseVizio() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(2, 1, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class pairingRunnable implements Runnable {
        pairingRunnable() {
        }

        public void run() {
            String str = "application/json";
            String str2 = "PAIRING_REQ_TOKEN";
            try {
                String format = String.format(AppConstant.format, TvRemoteActivity.base, TvRemoteActivity.this.pairingStart);
                TvRemoteActivity tvRemoteActivity_ = TvRemoteActivity.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(AppConstant.vizioMatric);
                stringBuilder.append("(");
                stringBuilder.append(Build.MODEL);
                stringBuilder.append(")");
                tvRemoteActivity_.stringBuilder = stringBuilder.toString();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("{\"DEVICE_ID\":\"");
                stringBuilder2.append(TvRemoteActivity.this.stringBuilder);
                stringBuilder2.append(111);
                stringBuilder2.append("\",\"DEVICE_NAME\":\"");
                stringBuilder2.append(TvRemoteActivity.this.stringBuilder);
                stringBuilder2.append("\"}");
                String stringBuilder3 = stringBuilder2.toString();
                URL url = new URL(format);
                TvRemoteActivity.verify();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("PUT");
                httpsURLConnection.setHostnameVerifier(TvRemoteActivity.this.hostnameVerifier);
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
                            TvRemoteActivity.responseBuilder = stringBuilder3;
                            TvRemoteActivity.this.R(str2, stringBuilder3);
                        }
                        TvRemoteActivity.this.k0();
                    }
                }
                httpsURLConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG", "run: exception=" + e.getMessage());
            }
        }
    }

    class tokenExecute implements Runnable {

        class a implements Runnable {
            a() {
            }

            public void run() {
            }
        }

        tokenExecute() {
        }

        public void run() {
            String str = "application/json";
            String str2 = "AUTH_TOKEN";
            try {
                TvRemoteActivity tvRemoteActivity_ = TvRemoteActivity.this;
                if (tvRemoteActivity_.pairingEditText != null)
                    tvRemoteActivity_.pairString = tvRemoteActivity_.pairingEditText.getText().toString();
                String format = String.format(AppConstant.format, TvRemoteActivity.base, TvRemoteActivity.this.pair);
                TvRemoteActivity tvRemoteActivity_2 = TvRemoteActivity.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(AppConstant.vizioMatric);
                stringBuilder.append("(");
                stringBuilder.append(Build.MODEL);
                stringBuilder.append(")");
                tvRemoteActivity_2.stringBuilder = stringBuilder.toString();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("{\"DEVICE_ID\": \"");
                stringBuilder2.append(TvRemoteActivity.this.stringBuilder);
                stringBuilder2.append(111);
                stringBuilder2.append("\",\"CHALLENGE_TYPE\": 1,\"RESPONSE_VALUE\": \"");
                stringBuilder2.append(TvRemoteActivity.this.pairString);
                stringBuilder2.append("\",\"PAIRING_REQ_TOKEN\":");
                stringBuilder2.append(TvRemoteActivity.responseBuilder);
                stringBuilder2.append("}");
                String stringBuilder3 = stringBuilder2.toString();
                URL url = new URL(format);
                TvRemoteActivity.verify();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(TvRemoteActivity.this.hostnameVerifier);
                httpsURLConnection.setRequestMethod("PUT");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.addRequestProperty("Content-Type", str);
                httpsURLConnection.setRequestProperty("Accept", str);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(stringBuilder3);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                if (httpsURLConnection.getResponseCode() == 403) {
                    TvRemoteActivity.this.k0();
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
                            TvRemoteActivity.preferenceToken = string;
                            TvRemoteActivity.this.R(str2, string);
                            Editor edit = TvRemoteActivity.this.getSharedPreferences("vizio_rate_us", 0).edit();
                            edit.putInt("vizio_rate_us_id", 5);
                            edit.commit();
                            new Handler(Looper.getMainLooper()).postDelayed(new a(), 60000);
                        }
                        if (stringBuilder3.contains("Pairing request denied")) {
                            TvRemoteActivity.this.k0();
                        }
                        if (stringBuilder3.contains("Invalid parameter")) {
                            TvRemoteActivity.this.startPairing();
                        }
                    } else {
                        httpsURLConnection.disconnect();
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class volumeUp implements OnClickListener {
        volumeUp() {
        }

        public void onClick(View view) {
            TvRemoteActivity.this.pressExecute(5, 1, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }

    class w extends Thread {

        class aaa implements Runnable {

            class cancelRunnable implements OnClickListener {
                cancelRunnable() {
                }

                public void onClick(View view) {
                    TvRemoteActivity.this.pairingDialog.dismiss();
                }
            }

            class b implements OnClickListener {

                class aaaa implements Runnable {
                    aaaa() {
                    }

                    public void run() {

                    }
                }

                b() {
                }

                public void onClick(View view) {
                    TvRemoteActivity.this.token();
                    TvRemoteActivity.this.pairingDialog.dismiss();
                    new Handler(Looper.getMainLooper()).postDelayed(new aaa(), 60000);
                }
            }

            aaa() {
            }

            public void run() {
                TvRemoteActivity.this.pairingDialog = new Dialog(TvRemoteActivity.this);
                TvRemoteActivity.this.pairingDialog.setContentView(R.layout.fragment_pairing);
                TvRemoteActivity.this.pairingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                TvRemoteActivity.this.pairingDialog.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                Button button = (Button) TvRemoteActivity.this.pairingDialog.findViewById(R.id.pairing_ok);
                Button button2 = (Button) TvRemoteActivity.this.pairingDialog.findViewById(R.id.pairing_cancel);
                TvRemoteActivity tvRemoteActivity_ = TvRemoteActivity.this;
                tvRemoteActivity_.pairingEditText = (EditText) tvRemoteActivity_.pairingDialog.findViewById(R.id.pairing_pin);
                button2.setOnClickListener(new cancelRunnable());
                button.setOnClickListener(new b());
                TvRemoteActivity.this.pairingDialog.setCancelable(false);
                TvRemoteActivity.this.pairingDialog.show();
            }
        }

        w() {
        }

        public void run() {
            TvRemoteActivity.this.runOnUiThread(new aaa());
        }
    }

    class x implements Runnable {
        final int F0;
        final int G0;
        final String H0;

        x(int i, int i2, String str) {
            this.F0 = i;
            this.G0 = i2;
            this.H0 = str;
        }

        public void run() {
            String str = "application/json";
            try {
                String format = String.format(AppConstant.format, TvRemoteActivity.base, TvRemoteActivity.this.keyCommand);
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
                TvRemoteActivity.verify();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(TvRemoteActivity.this.hostnameVerifier);
                httpsURLConnection.setRequestMethod("PUT");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.addRequestProperty("Content-Type", str);
                httpsURLConnection.setRequestProperty("Accept", str);
                httpsURLConnection.setRequestProperty("Auth", TvRemoteActivity.preferenceToken);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(stringBuilder2);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                if (httpsURLConnection.getResponseCode() == 403) {
                    TvRemoteActivity.this.k0();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class y implements Runnable {
        final String F0;
        final int G0;
        final String H0;

        y(String str, int i, String str2) {
            this.F0 = str;
            this.G0 = i;
            this.H0 = str2;
        }

        public void run() {
            String str = "application/json";
            try {
                String format = String.format(AppConstant.format, TvRemoteActivity.base, TvRemoteActivity.this.appLaunch);
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
                TvRemoteActivity.verify();
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setHostnameVerifier(TvRemoteActivity.this.hostnameVerifier);
                httpsURLConnection.setRequestMethod("PUT");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.addRequestProperty("Content-Type", str);
                httpsURLConnection.setRequestProperty("Accept", str);
                httpsURLConnection.setRequestProperty("Auth", TvRemoteActivity.preferenceToken);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(stringBuilder2);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                if (httpsURLConnection.getResponseCode() == 403) {
                    TvRemoteActivity.this.k0();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class nameVerifier implements HostnameVerifier {
        nameVerifier() {
        }

        public boolean verify(String str, SSLSession sSLSession) {
            String str2 = "Host Name";
            if (TvRemoteActivity.base.contains(str)) {
                Log.d(str2, str);
                return true;
            }
            Log.d(str2, TvRemoteActivity.base);
            return false;
        }
    }


    private void P(String str, int i, String str2) {
        if (preferenceToken != null) {
            new Thread(new y(str, i, str2)).start();
        } else {
            token();
        }
    }

    private void R(String str, String str2) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = defaultSharedPreferences;
        Editor edit = defaultSharedPreferences.edit();
        edit.putString(str, str2);
        edit.apply();
        if (str2 == null) {
            onSharedPreferenceChanged(sharedPreferences, null);
        }
    }

    private void pressExecute(int i, int i2, String str) {
        if (preferenceToken != null) {
            new Thread(new x(i, i2, str)).start();
        } else {
            token();
        }
    }


    private void e0() {
        Bundle bundle = new Bundle();
        bundle.putString("content_type", "UTRC_Review_Vizio");
    }


    private void k0() {
        new w().start();
    }


    private static void verify() {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new Custom()}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        } catch (Exception e) {
            Log.i("TAG", "btnUp: error=" + e.getMessage());
        }
    }

    public String defaultPreference(String str) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return defaultSharedPreferences.getString(str, null);
    }

    public void startPairing() {
        new Thread(new pairingRunnable()).start();
    }

    public void token() {
        new Thread(new tokenExecute()).start();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wifi_remote_view);
        base = "https://";
        String auth_token = "AUTH_TOKEN";
        preferenceToken = defaultPreference(auth_token);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("hello");
        stringBuilder.append(preferenceToken);
        Log.d(auth_token, stringBuilder.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(base);
        stringBuilder2.append(FindActivity.e1);
        stringBuilder2.append(":7345");
        auth_token = stringBuilder2.toString();
        base = auth_token;
//        getWindow().addFlags(128);
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        this.playToggle = false;
        findViewById(R.id.ok_button).setOnClickListener(new okBtn());
        findViewById(R.id.back_button).setOnClickListener(new backButton());
        findViewById(R.id.button_source).setOnClickListener(new btnSource());
        findViewById(R.id.info_button).setOnClickListener(new infoButton());
//        findViewById(R.id.netflix_vizio).setOnClickListener(new netflizVizio());
//        findViewById(R.id.primevideo_vizio).setOnClickListener(new primeVideoVizio());
//        findViewById(R.id.xumo_vizio).setOnClickListener(new xumoVizio());
//        findViewById(R.id.hulu_vizio).setOnClickListener(new huluVizio());
        findViewById(R.id.power_off_vizio).setOnClickListener(new powerOff());
        findViewById(R.id.volume_up).setOnClickListener(new volumeUp());
        findViewById(R.id.volume_down).setOnClickListener(new volumeDown());
        findViewById(R.id.cvMute).setOnClickListener(new Mute());
        findViewById(R.id.channel_up).setOnClickListener(new channelUp());
        findViewById(R.id.channel_down).setOnClickListener(new channelDown());
        findViewById(R.id.ok_up).setOnClickListener(new btnUp());
        findViewById(R.id.ok_down).setOnClickListener(new okDown());
        findViewById(R.id.ok_left).setOnClickListener(new okLeft());
        findViewById(R.id.ok_right).setOnClickListener(new okRight());
//        findViewById(R.id.redbox_vizio).setOnClickListener(new redBoxVizio());
        findViewById(R.id.watchfree_vizio).setOnClickListener(new watchFreeVizio());
        findViewById(R.id.play_button).setOnClickListener(new playButtonVizio());
        findViewById(R.id.fforward).setOnClickListener(new forwardVizio());
        findViewById(R.id.freverse).setOnClickListener(new reverseVizio());
        findViewById(R.id.button_menu).setOnClickListener(new buttonMenu());
        findViewById(R.id.buttonhome).setOnClickListener(new buttonHome());
        findViewById(R.id.youtube_vizio).setOnClickListener(new youtubeVizio());
//        findViewById(R.id.vudu_vizio).setOnClickListener(new vuduVizio());


        findViewById(R.id.cvChannels).setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TvRemoteActivity.this);
            ChannelBottomSheetBinding binding = ChannelBottomSheetBinding.inflate(getLayoutInflater());
            bottomSheetDialog.setContentView(binding.getRoot());
            binding.llHulu.setOnClickListener(v1 -> {
//                new huluVizio();
                TvRemoteActivity.this.P("null", 2, "3");
                TvRemoteActivity.this.vibrator.vibrate(5);
                bottomSheetDialog.dismiss();
            });
            binding.llYoutube.setOnClickListener(v1 -> {
//                new youtubeVizio();
                TvRemoteActivity.this.P("null", 5, "1");
                TvRemoteActivity.this.vibrator.vibrate(5);
                bottomSheetDialog.dismiss();
            });
            binding.llNetflix.setOnClickListener(v1 -> {
//                new netflizVizio();
                TvRemoteActivity.this.P("null", 3, "1");
                TvRemoteActivity.this.vibrator.vibrate(5);
                bottomSheetDialog.dismiss();
            });
            binding.llPrimeVideo.setOnClickListener(v1 -> {
//                new primeVideoVizio();
                TvRemoteActivity.this.P("null", 2, "4");
                TvRemoteActivity.this.vibrator.vibrate(5);
                bottomSheetDialog.dismiss();
            });
            binding.llRedbox.setOnClickListener(v1 -> {
//                new redBoxVizio();
                TvRemoteActivity.this.P("null", 2, "41");
                TvRemoteActivity.this.vibrator.vibrate(5);
                bottomSheetDialog.dismiss();
            });
            binding.llXumo.setOnClickListener(v1 -> {
//                new xumoVizio();
                TvRemoteActivity.this.P("{\"CAST_NAMESPACE\":\"urn:x-cast:com.google.cast.media\",\"CAST_MESSAGE\":{\"type\":\"LOAD\",\"media\":{},\"autoplay\":true,\"currentTime\":0,\"customData\":{}}}", 0, "36E1EA1F");
                TvRemoteActivity.this.vibrator.vibrate(5);
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        });

        findViewById(R.id.cvTouch).setOnClickListener(v -> {
            findViewById(R.id.llDial).setVisibility(View.GONE);
            findViewById(R.id.cvTouchPad).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.cvRemoteCircle).setOnClickListener(v -> {
            findViewById(R.id.llDial).setVisibility(View.VISIBLE);
            findViewById(R.id.cvTouchPad).setVisibility(View.GONE);
        });

        findViewById(R.id.cvTouchPad).setOnTouchListener(new SwipeListener());


        if (preferenceToken == null) {
            startPairing();
        }
    }


    public class SwipeListener implements View.OnTouchListener {
        private int min_distance = 100;
        private float downX, downY, upX, upY;
        View v;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            this.v = v;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (Math.abs(deltaX) > min_distance) {

                            if (deltaX < 0) {
                                this.onLeftToRightSwipe();
                                return true;
                            }
                            if (deltaX > 0) {
                                this.onRightToLeftSwipe();
                                return true;
                            }
                        } else {

                            return false;
                        }
                    } else {
                        if (Math.abs(deltaY) > min_distance) {
                            if (deltaY < 0) {
                                this.onTopToBottomSwipe();
                                return true;
                            }
                            if (deltaY > 0) {
                                this.onBottomToTopSwipe();
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                    return false;
                }
            }
            return false;
        }

        public void onLeftToRightSwipe() {
//            new okRight();
            TvRemoteActivity.this.pressExecute(3, 7, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }

        public void onRightToLeftSwipe() {
//            new okLeft();
            TvRemoteActivity.this.pressExecute(3, 1, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }

        public void onTopToBottomSwipe() {
//            new okDown();
            TvRemoteActivity.this.pressExecute(3, 0, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }

        public void onBottomToTopSwipe() {
//            new btnUp();
            TvRemoteActivity.this.pressExecute(3, 8, AppConstant.keyPress);
            TvRemoteActivity.this.vibrator.vibrate(5);
        }
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
    }
}

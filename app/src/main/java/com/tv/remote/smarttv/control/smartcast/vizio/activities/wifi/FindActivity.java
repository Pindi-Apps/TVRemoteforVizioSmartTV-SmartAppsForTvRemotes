package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.chaos.view.PinView;
import com.tv.remote.smarttv.control.smartcast.vizio.R;
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityWifiRemoteBinding;
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.AppExtKt;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class FindActivity extends AppCompatActivity {
    public static String e1 = "";
    static ListView listView;

    String base;

    public static ArrayAdapter arrayAdapter;
    public static TextView headingTextView;
    public static TextView textView;
    public static ProgressBar progressBar;
    public static String vizioUrl;

    public static String preferenceToken;
    public static String responseBuilder;

    public static SharedPreferences sharedPreferences;


    static class nameVerifier implements HostnameVerifier {
        nameVerifier() {

        }

        public boolean verify(String str, SSLSession sSLSession) {
            String str2 = "Host Name";
            if (FindActivity.e1.contains(str)) {
                Log.d(str2, str);
                return true;
            }
            Log.d(str2, TvRemoteActivity.base);
            return false;
        }
    }


    private void verify() {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new Custom()}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        } catch (Exception e) {
            Log.i("TAG", "btnUp: error=" + e.getMessage());
        }
    }


    public void startPairing() {
//        new Thread(() -> {
        String str = "application/json";
        String str2 = "PAIRING_REQ_TOKEN";
        try {

            String auth_token = FindActivity.e1;
            preferenceToken = defaultPreference(auth_token);
            StringBuilder stringBuilder6 = new StringBuilder();
            stringBuilder6.append("hello");
            stringBuilder6.append(preferenceToken);
            Log.d(auth_token, stringBuilder6.toString());
            StringBuilder stringBuilderFinal = new StringBuilder();
            stringBuilderFinal.append("https://");
            stringBuilderFinal.append(FindActivity.e1);
            stringBuilderFinal.append(":7345");
            auth_token = stringBuilderFinal.toString();
            base = auth_token;
            String format = String.format(AppConstant.format, base, "/pairing/start");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(AppConstant.vizioRemote);
            stringBuilder.append("(");
            stringBuilder.append(Build.MODEL);
            stringBuilder.append(")");
            String activityStringBuilder = stringBuilder.toString();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("{\"DEVICE_ID\":\"");
            stringBuilder2.append(activityStringBuilder);
            stringBuilder2.append(111);
            stringBuilder2.append("\",\"DEVICE_NAME\":\"");
            stringBuilder2.append(activityStringBuilder);
            stringBuilder2.append("\"}");
            String stringBuilder3 = stringBuilder2.toString();
            URL url = new URL(format);
            verify();
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("PUT");
            httpsURLConnection.setHostnameVerifier(new nameVerifier());
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
                        responseBuilder = stringBuilder3;
                        R(str2, stringBuilder3);
                        this.runOnUiThread(this::pairingDialog);
                    }


                }
            }
            httpsURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "run: exception=" + e.getMessage());
        }


//        }).start();
    }

    private void pairingDialog() {

        Dialog pairingDialog = new Dialog(FindActivity.this);
        pairingDialog.setContentView(R.layout.fragment_pairing);

        if (pairingDialog.getWindow() != null){
            pairingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }




//        pairingDialog.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        Button button = (Button) pairingDialog.findViewById(R.id.pairing_ok);
        Button button2 = (Button) pairingDialog.findViewById(R.id.pairing_cancel);


        PinView pairingEditText = (PinView) pairingDialog.findViewById(R.id.pinView);

        InputMethodManager imm = (InputMethodManager) FindActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(pairingEditText, 0);


        button2.setOnClickListener(v -> pairingDialog.dismiss());
        button.setOnClickListener(v -> {
            String token = Objects.requireNonNull(pairingEditText.getText()).toString().trim();
            if (token.isEmpty()) {
                pairingEditText.setError("Filed Required");
            } else {
                pairingDialog.dismiss();
//                Log.i("TAG", "pairingDialog: token =" + token);
                Executors.newSingleThreadExecutor().execute(() -> checkToken(token));
            }
        });
        pairingDialog.setCancelable(false);
        pairingDialog.show();
    }


    private void checkToken(String pairString) {

        String str = "application/json";
        String str2 = "AUTH_TOKEN";
        try {

            String format = String.format(AppConstant.format, base, "/pairing/pair");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(AppConstant.vizioRemote);
            stringBuilder.append("(");
            stringBuilder.append(Build.MODEL);
            stringBuilder.append(")");
            String activityStringBuilder = stringBuilder.toString();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("{\"DEVICE_ID\": \"");
            stringBuilder2.append(activityStringBuilder);
            stringBuilder2.append(111);
            stringBuilder2.append("\",\"CHALLENGE_TYPE\": 1,\"RESPONSE_VALUE\": \"");
            stringBuilder2.append(pairString);
            stringBuilder2.append("\",\"PAIRING_REQ_TOKEN\":");
            stringBuilder2.append(responseBuilder);
            stringBuilder2.append("}");
            String stringBuilder3 = stringBuilder2.toString();
            URL url = new URL(format);
            verify();
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setHostnameVerifier(new nameVerifier());
            httpsURLConnection.setRequestMethod("PUT");
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.addRequestProperty("Content-Type", str);
            httpsURLConnection.setRequestProperty("Accept", str);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
            outputStreamWriter.write(stringBuilder3);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            if (httpsURLConnection.getResponseCode() == 403) {
                this.runOnUiThread(this::pairingDialog);
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
                        preferenceToken = string;
                        R(FindActivity.e1, string);
                        Editor edit = FindActivity.this.getSharedPreferences("vizio_rate_us", 0).edit();
                        edit.putInt("vizio_rate_us_id", 5);
                        edit.apply();
                        FindActivity.this.startActivity(new Intent(FindActivity.this.getApplicationContext(), TvRemoteActivity.class));
                        FindActivity.this.finish();
                    }
                    if (stringBuilder3.contains("Pairing request denied")) {
                        this.runOnUiThread(this::pairingDialog);
                    }
                    if (stringBuilder3.contains("Invalid parameter")) {
                        startPairing();
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


    private void R(String str, String str2) {
//        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences defaultSharedPreferences = this.getSharedPreferences(AppConstant.PreferenceName, MODE_PRIVATE);
        sharedPreferences = defaultSharedPreferences;
        Editor edit = defaultSharedPreferences.edit();
        edit.putString(str, str2);
        edit.apply();
        if (str2 == null) {
            onSharedPreferenceChanged(sharedPreferences, null);
        }
    }

    public String defaultPreference(String str) {
        SharedPreferences defaultSharedPreferences = this.getSharedPreferences(AppConstant.PreferenceName, MODE_PRIVATE);
        return defaultSharedPreferences.getString(str, null);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
    }


    class adapterClick implements OnItemClickListener {

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            FindActivity.e1 = "";
            FindActivity.e1 = ((DeviceModel) adapterView.getItemAtPosition(i)).hostAddress();
            Editor edit = FindActivity.this.getSharedPreferences("vizio_url", 0).edit();
            edit.putString("vizio_url_string", FindActivity.e1);
            edit.apply();

//            pairingDialog();


            String auth_token = FindActivity.e1;
            preferenceToken = defaultPreference(auth_token);

            if (preferenceToken == null) {
                Executors.newSingleThreadExecutor().execute(FindActivity.this::startPairing);
            } else {

                FindActivity.this.startActivity(new Intent(FindActivity.this.getApplicationContext(), TvRemoteActivity.class));
                FindActivity.this.finish();

            }


        }
    }


//    private class PairingTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                String str = "application/json";
//                String str2 = "PAIRING_REQ_TOKEN";
//
//                String auth_token = "AUTH_TOKEN";
//                preferenceToken = defaultPreference(auth_token);
//                StringBuilder stringBuilder6 = new StringBuilder();
//                stringBuilder6.append("hello");
//                stringBuilder6.append(preferenceToken);
//                Log.d(auth_token, stringBuilder6.toString());
//                StringBuilder stringBuilderFinal = new StringBuilder();
//                stringBuilderFinal.append("https://");
//                stringBuilderFinal.append(FindActivity.e1);
//                stringBuilderFinal.append(":7345");
//                auth_token = stringBuilderFinal.toString();
//                String base = auth_token;
//
//                String format = String.format(AppConstant.format, base, "/pairing/start");
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append(AppConstant.vizioMatric);
//                stringBuilder.append("(");
//                stringBuilder.append(Build.MODEL);
//                stringBuilder.append(")");
//                String activityStringBuilder = stringBuilder.toString();
//                StringBuilder stringBuilder2 = new StringBuilder();
//                stringBuilder2.append("{\"DEVICE_ID\":\"");
//                stringBuilder2.append(activityStringBuilder);
//                stringBuilder2.append(111);
//                stringBuilder2.append("\",\"DEVICE_NAME\":\"");
//                stringBuilder2.append(activityStringBuilder);
//                stringBuilder2.append("\"}");
//                String stringBuilder3 = stringBuilder2.toString();
//                URL url = new URL(format);
//                verify();
//                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
//                httpsURLConnection.setRequestMethod("PUT");
//                httpsURLConnection.setHostnameVerifier(new nameVerifier());
//                httpsURLConnection.setDoOutput(true);
//                httpsURLConnection.addRequestProperty("Content-Type", str);
//                httpsURLConnection.setRequestProperty("Accept", str);
//                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
//                outputStreamWriter.write(stringBuilder3);
//                outputStreamWriter.flush();
//                outputStreamWriter.close();
//                if (httpsURLConnection.getResponseCode() != 500) {
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
//                    while (true) {
//                        stringBuilder3 = bufferedReader.readLine();
//                        if (stringBuilder3 == null) {
//                            break;
//                        }
//                        JSONObject jSONObject = new JSONObject(stringBuilder3);
//                        System.out.println(jSONObject);
//                        if (stringBuilder3.contains(str2)) {
//                            stringBuilder3 = jSONObject.getJSONObject("ITEM").getString(str2);
//                            TvRemoteActivity.responseBuilder = stringBuilder3;
//                            R(str2, stringBuilder3);
//                        }
//
//                        httpsURLConnection.disconnect();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.i("TAG", "doInBackground: exception=" + e.getMessage());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            pairingDialog();
//        }
//    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityWifiRemoteBinding binding = ActivityWifiRemoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        insetsController.setAppearanceLightStatusBars(true);
        insetsController.setAppearanceLightNavigationBars(true);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
                            | WindowInsetsCompat.Type.ime()
            );

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
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

package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tv.remote.smarttv.control.smartcast.vizio.BuildConfig;
import com.tv.remote.smarttv.control.smartcast.vizio.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class FindActivity extends AppCompatActivity {
    public static String e1 = "";
    static ListView listView;

    static String base = "http://";
    public static ArrayAdapter arrayAdapter;
    public static TextView headingTextView;
    public static TextView textView;
    public static ProgressBar progressBar;
//    public static String n1;
    public static String vizioUrl;

//    public static String preferenceToken;
//    public static SharedPreferences sharedPreferences;
//
//    public static  String responseBuilder;




//    static class nameVerifier implements HostnameVerifier {
//        nameVerifier() {
//
//        }
//
//        public boolean verify(String str, SSLSession sSLSession) {
//            String str2 = "Host Name";
//            if (base.contains(str)) {
//                Log.d(str2, str);
//                return true;
//            }
//            return false;
//        }
//    }


//    private void verify() {
//
//        try {
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, new TrustManager[]{new Custom()}, null);
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//
//        } catch (Exception e) {
//            Log.i("TAG", "btnUp: error=" + e.getMessage());
//            Toast.makeText(this, "exception cert=" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//
//
//    public void startPairing() {
//        String str = "application/json";
//        String str2 = "PAIRING_REQ_TOKEN";
//        try {
//
//            String auth_token = "AUTH_TOKEN";
//            preferenceToken = defaultPreference(auth_token);
//            StringBuilder stringBuilder6 = new StringBuilder();
//            stringBuilder6.append("hello");
//            stringBuilder6.append(preferenceToken);
//            Log.d(auth_token, stringBuilder6.toString());
//            StringBuilder stringBuilderFinal = new StringBuilder();
//            stringBuilderFinal.append("https://");
//            stringBuilderFinal.append(FindActivity.e1);
//            stringBuilderFinal.append(":7345");
//            auth_token = stringBuilderFinal.toString();
//            base = auth_token;
//
//
//            String format = String.format(AppConstant.format, base, "/pairing/start");
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(AppConstant.vizioMatric);
//            stringBuilder.append("(");
//            stringBuilder.append(Build.MODEL);
//            stringBuilder.append(")");
//            String activityStringBuilder = stringBuilder.toString();
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append("{\"DEVICE_ID\":\"");
//            stringBuilder2.append(activityStringBuilder);
//            stringBuilder2.append(111);
//            stringBuilder2.append("\",\"DEVICE_NAME\":\"");
//            stringBuilder2.append(activityStringBuilder);
//            stringBuilder2.append("\"}");
//            String stringBuilder3 = stringBuilder2.toString();
//            URL url = new URL(format);
//            Toast.makeText(this, "url=" + url, Toast.LENGTH_SHORT).show();
//            verify();
//            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
//            httpsURLConnection.setRequestMethod("PUT");
//            httpsURLConnection.setHostnameVerifier((hostname, session) -> {
//                if (base.contains(hostname)) {
//                    Log.d(str2, str);
//                    Toast.makeText(this, "hostName is true", Toast.LENGTH_SHORT).show();
//                    return true;
//                }else {
//                    Toast.makeText(this, "hostName is false", Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });
//            httpsURLConnection.setDoOutput(true);
//            httpsURLConnection.addRequestProperty("Content-Type", str);
//            httpsURLConnection.setRequestProperty("Accept", str);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
//            Toast.makeText(this, "flush="+ stringBuilder3, Toast.LENGTH_SHORT).show();
//            outputStreamWriter.write(stringBuilder3);
//            outputStreamWriter.flush();
//            outputStreamWriter.close();
//            if (httpsURLConnection.getResponseCode() != 500) {
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
//                while (true) {
//                    stringBuilder3 = bufferedReader.readLine();
//                    if (stringBuilder3 == null) {
//                        break;
//                    }
//                    JSONObject jSONObject = new JSONObject(stringBuilder3);
////                        System.out.println(jSONObject);
//                    Toast.makeText(this, "json=" + jSONObject, Toast.LENGTH_SHORT).show();
//                    if (stringBuilder3.contains(str2)) {
//                        stringBuilder3 = jSONObject.getJSONObject("ITEM").getString(str2);
//                        responseBuilder = stringBuilder3;
//                        R(str2, stringBuilder3);
//                    }
//
//                    pairingDialog();
//
//                }
//            }
//            httpsURLConnection.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i("TAG", "run: exception=" + e.getMessage());
//            Toast.makeText(this, "exception=" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
//
//    private void pairingDialog(){
//
//        Dialog pairingDialog = new Dialog(FindActivity.this);
//        pairingDialog.setContentView(R.layout.fragment_pairing);
//        pairingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        pairingDialog.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        Button button = (Button) pairingDialog.findViewById(R.id.pairing_ok);
//        Button button2 = (Button) pairingDialog.findViewById(R.id.pairing_cancel);
//        EditText pairingEditText = (EditText) pairingDialog.findViewById(R.id.pairing_pin);
//        button2.setOnClickListener(v -> {
//            pairingDialog.dismiss();
//        });
//        button.setOnClickListener(v -> {
//            pairingDialog.dismiss();
//            String token = pairingEditText.getText().toString().trim();
//            if (token.isEmpty()){
//                pairingEditText.setError("Filed Required");
//            }else {
//                checkToken(pairingEditText.getText().toString().trim());
//            }
//        });
//        pairingDialog.setCancelable(false);
//        pairingDialog.show();
//    }
//
//
//    private void checkToken(String pairString){
//
//        String str = "application/json";
//        String str2 = "AUTH_TOKEN";
//        try {
//
//            String format = String.format(AppConstant.format, base, "/pairing/start");
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(AppConstant.vizioMatric);
//            stringBuilder.append("(");
//            stringBuilder.append(Build.MODEL);
//            stringBuilder.append(")");
//             String activityStringBuilder = stringBuilder.toString();
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append("{\"DEVICE_ID\": \"");
//            stringBuilder2.append(activityStringBuilder);
//            stringBuilder2.append(111);
//            stringBuilder2.append("\",\"CHALLENGE_TYPE\": 1,\"RESPONSE_VALUE\": \"");
//            stringBuilder2.append(pairString);
//            stringBuilder2.append("\",\"PAIRING_REQ_TOKEN\":");
//            stringBuilder2.append(responseBuilder);
//            stringBuilder2.append("}");
//            String stringBuilder3 = stringBuilder2.toString();
//            URL url = new URL(format);
//            verify();
//            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
//            httpsURLConnection.setHostnameVerifier(new nameVerifier());
//            httpsURLConnection.setRequestMethod("PUT");
//            httpsURLConnection.setDoOutput(true);
//            httpsURLConnection.addRequestProperty("Content-Type", str);
//            httpsURLConnection.setRequestProperty("Accept", str);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
//            outputStreamWriter.write(stringBuilder3);
//            outputStreamWriter.flush();
//            outputStreamWriter.close();
//            if (httpsURLConnection.getResponseCode() == 403) {
//                pairingDialog();
//                return;
//            }
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
//            while (true) {
//                stringBuilder3 = bufferedReader.readLine();
//                if (stringBuilder3 != null) {
//                    JSONObject jSONObject = new JSONObject(stringBuilder3);
//                    System.out.println(jSONObject);
//                    if (stringBuilder3.contains(str2)) {
//                        String string = jSONObject.getJSONObject("ITEM").getString(str2);
//                        preferenceToken = string;
//                        R(str2, string);
//                        Editor edit = FindActivity.this.getSharedPreferences("vizio_rate_us", 0).edit();
//                        edit.putInt("vizio_rate_us_id", 5);
//                        edit.apply();
//                        FindActivity.this.startActivity(new Intent(FindActivity.this.getApplicationContext(), TvRemoteActivity.class));
//                        FindActivity.this.finish();
//                    }
//                    if (stringBuilder3.contains("Pairing request denied")) {
//                        pairingDialog();
//                    }
//                    if (stringBuilder3.contains("Invalid parameter")) {
//                        startPairing();
//                    }
//                } else {
//                    httpsURLConnection.disconnect();
//                    return;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//    }
//
//
//    private void R(String str, String str2) {
//        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        sharedPreferences = defaultSharedPreferences;
//        Editor edit = defaultSharedPreferences.edit();
//        edit.putString(str, str2);
//        edit.apply();
//        if (str2 == null) {
//            onSharedPreferenceChanged(sharedPreferences, null);
//        }
//    }
//
//
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
//    }



    class adapterClick implements OnItemClickListener {

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            FindActivity.e1 = "";
            FindActivity.e1 = ((DeviceModel) adapterView.getItemAtPosition(i)).hostAddress();
            Editor edit = FindActivity.this.getSharedPreferences("vizio_url", 0).edit();
            edit.putString("vizio_url_string", FindActivity.e1);
            edit.apply();
            FindActivity.this.startActivity(new Intent(FindActivity.this.getApplicationContext(), TvRemoteActivity.class));



//            if (preferenceToken == null) {
//                startPairing();
//            }



        }
    }

//    public String defaultPreference(String str) {
//        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        return defaultSharedPreferences.getString(str, null);
//    }



    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wifi_remote);
        String str = "";
//        n1 = getSharedPreferences("vizio_saved_device", 0).getString("vizio_device", str);
//        vizioUrl = getSharedPreferences("vizio_url", 0).getString("vizio_url_string", str);
//        preferenceToken = defaultPreference("AUTH_TOKEN");
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

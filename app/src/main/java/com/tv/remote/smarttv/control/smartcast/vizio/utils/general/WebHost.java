package com.tv.remote.smarttv.control.smartcast.vizio.utils.general;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Random;
import fi.iki.elonen.NanoHTTPD;

public class WebHost {
    private static final String NAME = "_fileNameIs";
    private static final int PORT = 1159;
    public static String TAG = "WebHost";
    private static Context mCtx;
    private static WebHost web;
    private AndroidServer androidServer;
    public HashMap<String, String> filePathMap;

    public static synchronized WebHost getInstance(Context context) {
        WebHost webHost;
        synchronized (WebHost.class) {
            if (web == null) {
                web = new WebHost(context);
            }
            webHost = web;
        }
        return webHost;
    }

    public WebHost(Context context) {
        mCtx = context;
    }

    private void initServer() {
        if (this.androidServer == null && isConnectedInWifi()) {
            String ipAddress = getIpAddress();
            if (!isEmpty(ipAddress)) {
                this.androidServer = new AndroidServer(ipAddress, PORT);
            }
        }
    }

    public void continueServer() {
        try {
            if (this.androidServer != null) {
                endServer();
            }
            initServer();
            AndroidServer androidServer2 = this.androidServer;
            if (androidServer2 != null) {
                androidServer2.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endServer() {
        try {
            AndroidServer androidServer2 = this.androidServer;
            if (androidServer2 != null) {
                androidServer2.stop();
                this.androidServer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isStarted() {
        return true;
    }

    public String putFile(String str) {
        if (this.filePathMap == null) {
            this.filePathMap = new HashMap<>();
        }
        String generateId = generateId();
        while (this.filePathMap.containsKey(generateId)) {
            generateId = generateId();
        }
        this.filePathMap.put(generateId, str);
        return "http://" + this.androidServer.getHostname() + ":" + this.androidServer.getListeningPort() + "/" + NAME + "/" + generateId;
    }

    public boolean isConnectedInWifi() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable() || !activeNetworkInfo.isConnected() || !activeNetworkInfo.getTypeName().equals("WIFI")) {
            Log.e(TAG, "Wifi Not Available");
            return false;
        }
        Log.e(TAG, "Wifi Available");
        return true;
    }

    private String getIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) mCtx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                return "";
            }
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
            return String.format("%d.%d.%d.%d", new Object[]{Integer.valueOf(ipAddress & 255), Integer.valueOf((ipAddress >> 8) & 255), Integer.valueOf((ipAddress >> 16) & 255), Integer.valueOf((ipAddress >> 24) & 255)});
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getIpAccess() {
        if (!isStarted()) {
            return "";
        }
        return "http://" + this.androidServer.getHostname() + ":" + this.androidServer.getListeningPort();
    }

    private String generateId() {
        int pow = (int) Math.pow(10.0d, (double) 7);
        return String.valueOf(pow + new Random().nextInt(pow * 9));
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("") || str.trim().toLowerCase().equalsIgnoreCase("null");
    }

    public class AndroidServer extends NanoHTTPD {
        public AndroidServer(int i) {
            super(i);
        }

        public AndroidServer(String str, int i) {
            super(str, i);
        }

        public Response serve(IHTTPSession iHTTPSession) {
            try {
                if (iHTTPSession.getMethod() == Method.GET) {
                    String uri = iHTTPSession.getUri();
                    if (uri.contains(WebHost.NAME)) {
                        String[] split = uri.replaceFirst("/", "").trim().split("/");
                        if (split.length == 2 && split[0].equals(WebHost.NAME) && WebHost.this.filePathMap != null && WebHost.this.filePathMap.containsKey(split[1])) {
                            String str2 = (String) WebHost.this.filePathMap.get(split[1]);
                            String str3 = WebHost.TAG;
                            if (!WebHost.isEmpty(str2)) {
                                File file = new File(str2);
                                if (file.exists()) {
                                    return newFixedLengthResponse(Response.Status.OK, getMimeTypeForFile(file.getAbsolutePath()), new FileInputStream(file), file.length());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse("<p>Error! File not found.</p>");
        }
    }
}

package com.tv.remote.smarttv.control.smartcast.vizio.remoteConfig.wifiPackage;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class FindVizAsyncTask extends AsyncTask<Activity, Void, Void> {
    public static DatagramSocket c;
    private static final String DEFAULT_ADDRESS = "239.255.255.250";
    private static final String DEFAULT_QUERY = "M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:1900\r\nMAN: \"ssdp:discover\"\r\nMX: 1\r\nST: urn:dial-multiscreen-org:service:dial:1\r\n\r\n";

    private Activity currentActivity;
    private Context mContext;
    //  private DatabaseReference mDatabase;


    public FindVizAsyncTask(Activity activity) {
        this.currentActivity = activity;
        this.mContext = activity.getApplicationContext();
    }

    public static void executeWithActivity(Activity activity) {
        new FindVizAsyncTask(activity).execute();
    }


    public Void doInBackground(Activity... activityArr) {
        DatagramSocket datagramSocket;
        WifiManager wifiManager = (WifiManager) this.mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            MulticastLock createMulticastLock = wifiManager.createMulticastLock("The Lock");
            createMulticastLock.acquire();
            try {
                InetAddress byName = InetAddress.getByName("239.255.255.250");
                DatagramSocket datagramSocket2 = new DatagramSocket(null);
                datagramSocket2.setReuseAddress(true);
                datagramSocket2.bind(new InetSocketAddress(1900));
                c = datagramSocket2;
                c.send(new DatagramPacket(DEFAULT_QUERY.getBytes(), DEFAULT_QUERY.length(), byName, 1900));
                long currentTimeMillis = System.currentTimeMillis();
                while (System.currentTimeMillis() - currentTimeMillis < 5000) {
                    DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
                    c.receive(datagramPacket);
                    String str = new String(datagramPacket.getData());

                    Log.d("DevSearch", datagramPacket.getAddress().getHostAddress());
                    WifiRemoteUtils wifiRemoteUtilsVar = new WifiRemoteUtils(datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort(), new String(datagramPacket.getData(), 0, datagramPacket.getLength()), currentActivity);

                }
                datagramSocket = c;
            } catch (IOException e) {
                e.printStackTrace();
                datagramSocket = c;
            } catch (Throwable th) {
                datagramSocket = c;
                if (datagramSocket != null) {
                    datagramSocket.close();
                }
            }
        }
        return null;
    }
}

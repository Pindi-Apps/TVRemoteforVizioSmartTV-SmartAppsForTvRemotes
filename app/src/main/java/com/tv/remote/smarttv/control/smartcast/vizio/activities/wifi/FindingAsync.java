package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class FindingAsync extends AsyncTask<Activity, Void, Void> {
    public static DatagramSocket datagramSocket;
    private static final String DEFAULT_QUERY = "M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:1900\r\nMAN: \"ssdp:discover\"\r\nMX: 1\r\nST: urn:dial-multiscreen-org:service:dial:1\r\n\r\n";

    private Activity activity;
    private Context context;


    public FindingAsync(Activity activity) {
        this.activity =  activity;
        this.context = activity.getApplicationContext();
    }

    public static void execute(Activity activity) {
        new FindingAsync(activity).execute();
    }

    public Void doInBackground(Activity... activityArr) {
        DatagramSocket datagramSocket;
        WifiManager wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            MulticastLock createMulticastLock = wifiManager.createMulticastLock("The Lock");
            createMulticastLock.acquire();
            try {
                InetAddress byName = InetAddress.getByName("239.255.255.250");
                DatagramSocket datagramSocket2 = new DatagramSocket(null);
                datagramSocket2.setReuseAddress(true);
                datagramSocket2.bind(new InetSocketAddress(1900));
                FindingAsync.datagramSocket = datagramSocket2;
                FindingAsync.datagramSocket.send(new DatagramPacket(DEFAULT_QUERY.getBytes(), DEFAULT_QUERY.length(), byName, 1900));
                long currentTimeMillis = System.currentTimeMillis();
                while (System.currentTimeMillis() - currentTimeMillis < 5000) {
                    DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
                    FindingAsync.datagramSocket.receive(datagramPacket);
                    String str = new String(datagramPacket.getData());
                    DeviceModel deviceModelVar = new DeviceModel(datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort(), new String(datagramPacket.getData(), 0, datagramPacket.getLength()), activity);
                }
                datagramSocket = FindingAsync.datagramSocket;
            } catch (Exception e) {
                e.printStackTrace();
                datagramSocket = FindingAsync.datagramSocket;
            }
        }
        return null;
    }
}

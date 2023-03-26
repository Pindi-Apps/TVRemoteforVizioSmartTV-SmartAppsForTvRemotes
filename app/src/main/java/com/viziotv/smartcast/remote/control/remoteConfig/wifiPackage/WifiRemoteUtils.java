package com.viziotv.smartcast.remote.control.remoteConfig.wifiPackage;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;

import com.viziotv.smartcast.remote.control.activities.WifiRemoteActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WifiRemoteUtils {
    private String deviceName = "Device";
    private String hotAddressB;
    private String hotAddressC;
    String nodeValue = "";
    class FetchDevices implements Runnable {
        final Activity mActivity;

        class FilterDevices implements Runnable {
            final WifiRemoteUtils wifiRemoteUtils;

            FilterDevices(WifiRemoteUtils wifiRemoteUtilsVar) {
                this.wifiRemoteUtils = wifiRemoteUtilsVar;
            }

            public void run() {
                if (WifiRemoteUtils.this.getDeviceName().contains("Vizio") || WifiRemoteUtils.this.getDeviceName().contains("VIZIO") | WifiRemoteUtils.this.getDeviceName().contains("vizio")) {
                    Editor edit;
//                    Log.d("Count", String.valueOf(WifiRemoteActivity.listViewAdapter.getCount()));


                    if (WifiRemoteActivity.listViewAdapter.getCount() > 0) {


                    } else if (WifiRemoteUtils.this.b().equals(WifiRemoteActivity.o1)) {
                        if (WifiRemoteUtils.this.b().equals(WifiRemoteActivity.o1)) {
                            if (WifiRemoteActivity.i1 != null)
                                WifiRemoteActivity.i1.setVisibility(View.VISIBLE);
                            if (WifiRemoteActivity.listViewAdapter != null)
                                WifiRemoteActivity.listViewAdapter.notifyDataSetChanged();
                        }
                        if (WifiRemoteActivity.h1 != null)
                            WifiRemoteActivity.h1.setVisibility(View.GONE);
                        if (WifiRemoteActivity.progressBar != null)
                            WifiRemoteActivity.progressBar.setVisibility(View.GONE);
                        if (WifiRemoteActivity.i1 != null)
                            WifiRemoteActivity.i1.setText(SetTextData.a);
                        edit = FetchDevices.this.mActivity.getSharedPreferences("vizio_saved_device", 0).edit();
                        edit.putString("vizio_device", String.valueOf(WifiRemoteUtils.this.getDeviceName()));
                        edit.apply();
                    }


                    if (WifiRemoteActivity.listViewAdapter != null) {
                        WifiRemoteActivity.listViewAdapter.add(this.wifiRemoteUtils);
                    }


                    if (WifiRemoteActivity.listView != null)
                        WifiRemoteActivity.listView.setAdapter(WifiRemoteActivity.listViewAdapter);
                    if (WifiRemoteActivity.listViewAdapter != null)
                        WifiRemoteActivity.listViewAdapter.notifyDataSetChanged();
                    if (WifiRemoteActivity.h1 != null)
                        WifiRemoteActivity.h1.setVisibility(View.GONE);
                    if (WifiRemoteActivity.progressBar != null)
                        WifiRemoteActivity.progressBar.setVisibility(View.GONE);
                    if (WifiRemoteActivity.i1 != null)
                        WifiRemoteActivity.i1.setText(SetTextData.a);

                    edit = FetchDevices.this.mActivity.getSharedPreferences("vizio_saved_device", 0).edit();
                    edit.putString("vizio_device", String.valueOf(WifiRemoteUtils.this.getDeviceName()));
                    edit.apply();
                }

                if (WifiRemoteActivity.listViewAdapter != null)
                    WifiRemoteActivity.listViewAdapter.notifyDataSetChanged();
            }
        }

        FetchDevices(Activity activity) {
            this.mActivity = activity;
        }

        public void run() {
            try {
                if (WifiRemoteUtils.this.getHotAddressC().trim().isEmpty()) {
                    Log.d("Vizio", "url is empty");
                    return;
                }
                Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new URL(WifiRemoteUtils.this.getHotAddressC()).openStream()));
                parse.getDocumentElement().normalize();
                Element element = (Element) parse.getElementsByTagName("device").item(0);
                String nodeValue = element.getElementsByTagName("friendlyName").item(0).getChildNodes().item(0).getNodeValue();
                Log.d("Device", nodeValue);
                HashMap<String, Object> dataMap = new HashMap<>();
                dataMap.put("timeStamp", new Date());
                dataMap.put("DevName", nodeValue);
                String nodeValue2 = element.getElementsByTagName("manufacturer").item(0).getChildNodes().item(0).getNodeValue();
                NodeList childNodes = element.getElementsByTagName("modelName").item(0).getChildNodes();
                dataMap.put("manufacturer", nodeValue2);
                dataMap.put("modelName", childNodes);
                if (childNodes != null) {
                    WifiRemoteUtils.this.nodeValue = childNodes.item(0).getNodeValue();
                }
                WifiRemoteUtils wifiRemoteUtilsVar = WifiRemoteUtils.this;
                if (wifiRemoteUtilsVar.nodeValue == null) {
                    wifiRemoteUtilsVar.nodeValue = "...";
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nodeValue);
                stringBuilder.append(" ( ");
                stringBuilder.append(nodeValue2);
                stringBuilder.append("+");
                stringBuilder.append(WifiRemoteUtils.this.nodeValue);
                stringBuilder.append(" )");
                wifiRemoteUtilsVar.setDeviceName(stringBuilder.toString());
                this.mActivity.runOnUiThread(new FilterDevices(new WifiRemoteUtils(WifiRemoteUtils.this.hotAddressB, WifiRemoteUtils.this.getDeviceName())));
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }
    }

    public WifiRemoteUtils(String str, int i, String str2, Activity activity) {
        this.hotAddressB = str;
        getHotAddressB(str);
        str = e(str2, "LOCATION:");
        this.hotAddressC = str;
        setHotAddressC(str);
        startFetchingDevices(activity);
    }

    public WifiRemoteUtils(String str, String str2) {
        this.hotAddressB = str;
        getHotAddressB(str);
        setDeviceName(str2);
    }

    private String e(String str, String str2) {
        int indexOf = str.indexOf(str2);
        if (indexOf == -1) {
            return "";
        }
        indexOf += str2.length();
        return str.substring(indexOf, str.indexOf("\r\n", indexOf));
    }

    private void startFetchingDevices(Activity activity) {
        new Thread(new FetchDevices(activity)).start();
    }

    public String b() {
        return this.hotAddressB;
    }

    public String getHotAddressC() {
        return this.hotAddressC;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void getHotAddressB(String str) {
        this.hotAddressB = str;
    }

    public void setHotAddressC(String str) {
        this.hotAddressC = str;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

}

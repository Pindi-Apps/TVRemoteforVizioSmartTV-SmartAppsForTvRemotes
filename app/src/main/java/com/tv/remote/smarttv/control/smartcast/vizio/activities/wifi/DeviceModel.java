package com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

public class DeviceModel {
    private String device = "Device";
    private String hostAddress;
    private String hostAddressAfter;
    String nodeValue = "";

    class SavingRunnable implements Runnable {
        final Activity activity;

        class validateModel implements Runnable {
            final DeviceModel deviceModel;

            validateModel(DeviceModel deviceModelVar) {
                this.deviceModel = deviceModelVar;
            }

            public void run() {
                if (DeviceModel.this.device().contains("Vizio") || DeviceModel.this.device().contains("VIZIO") | DeviceModel.this.device().contains("vizio")) {
                    Editor edit;
                    if (FindActivity.arrayAdapter.getCount() > 0) {

                    } else if (DeviceModel.this.hostAddress().equals(FindActivity.vizioUrl)) {
                        if (DeviceModel.this.hostAddress().equals(FindActivity.vizioUrl)) {
                            if (FindActivity.textView != null)
                                FindActivity.textView.setVisibility(View.VISIBLE);
                            if (FindActivity.arrayAdapter != null)
                                FindActivity.arrayAdapter.notifyDataSetChanged();
                        }
                        if (FindActivity.headingTextView != null)
                            FindActivity.headingTextView.setVisibility(View.GONE);
                        if (FindActivity.progressBar != null)
                            FindActivity.progressBar.setVisibility(View.GONE);
                        edit = SavingRunnable.this.activity.getSharedPreferences("vizio_saved_device", 0).edit();
                        edit.putString("vizio_device", String.valueOf(DeviceModel.this.device()));
                        edit.apply();
                    }
                    if (FindActivity.arrayAdapter != null) {
                        FindActivity.arrayAdapter.add(this.deviceModel);
                    }

                    if (FindActivity.listView != null)
                        FindActivity.listView.setAdapter(FindActivity.arrayAdapter);
                    if (FindActivity.arrayAdapter != null)
                        FindActivity.arrayAdapter.notifyDataSetChanged();
                    if (FindActivity.headingTextView != null)
                        FindActivity.headingTextView.setVisibility(View.GONE);
                    if (FindActivity.progressBar != null)
                        FindActivity.progressBar.setVisibility(View.GONE);
                    edit = SavingRunnable.this.activity.getSharedPreferences("vizio_saved_device", 0).edit();
                    edit.putString("vizio_device", String.valueOf(DeviceModel.this.device()));
                    edit.apply();
                }
                if (FindActivity.arrayAdapter != null)
                    FindActivity.arrayAdapter.notifyDataSetChanged();
            }
        }

        SavingRunnable(Activity activity) {
            this.activity = activity;
        }

        public void run() {
            try {
                if (DeviceModel.this.hostAddressAfter().trim().isEmpty()) {
                    return;
                }
                Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new URL(DeviceModel.this.hostAddressAfter()).openStream()));
                parse.getDocumentElement().normalize();
                Element element = (Element) parse.getElementsByTagName("device").item(0);
                String nodeValue = ((Element) element.getElementsByTagName("friendlyName").item(0)).getChildNodes().item(0).getNodeValue();
                Log.d("Device", nodeValue);
                HashMap<String, Object> datamap = new HashMap<>();
                datamap.put("timeStamp", new Date());
                datamap.put("DevName", nodeValue);
                String nodeValue2 = ((Element) element.getElementsByTagName("manufacturer").item(0)).getChildNodes().item(0).getNodeValue();
                NodeList childNodes = ((Element) element.getElementsByTagName("modelName").item(0)).getChildNodes();
                datamap.put("manufacturer", nodeValue2);
                datamap.put("modelName", childNodes);


                if (childNodes != null) {
                    DeviceModel.this.nodeValue = childNodes.item(0).getNodeValue();
                }
                DeviceModel deviceModelVar = DeviceModel.this;
                if (deviceModelVar.nodeValue == null) {
                    deviceModelVar.nodeValue = "...";
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nodeValue);
                stringBuilder.append(" ( ");
                stringBuilder.append(nodeValue2);
                stringBuilder.append("+");
                stringBuilder.append(DeviceModel.this.nodeValue);
                stringBuilder.append(" )");
                deviceModelVar.device(stringBuilder.toString());
                this.activity.runOnUiThread(new validateModel(new DeviceModel(DeviceModel.this.hostAddress, DeviceModel.this.device())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public DeviceModel(String str, int i, String str2, Activity activity) {
        this.hostAddress = str;
        hostAddress(str);
        str = FindWithLocation(str2, "LOCATION:");
        this.hostAddressAfter = str;
        hostAddressAfter(str);
        start(activity);
    }

    public DeviceModel(String str, String str2) {
        this.hostAddress = str;
        hostAddress(str);
        device(str2);
    }

    private String FindWithLocation(String str, String str2) {
        int indexOf = str.indexOf(str2);
        if (indexOf == -1) {
            return "";
        }
        indexOf += str2.length();
        return str.substring(indexOf, str.indexOf("\r\n", indexOf));
    }

    private void start(Activity activity) {
        new Thread(new SavingRunnable(activity)).start();
    }

    public String hostAddress() {
        return this.hostAddress;
    }

    public String hostAddressAfter() {
        return this.hostAddressAfter;
    }

    public String device() {
        return this.device;
    }

    public void hostAddress(String str) {
        this.hostAddress = str;
    }

    public void hostAddressAfter(String str) {
        this.hostAddressAfter = str;
    }

    public void device(String str) {
        this.device = str;
    }

}

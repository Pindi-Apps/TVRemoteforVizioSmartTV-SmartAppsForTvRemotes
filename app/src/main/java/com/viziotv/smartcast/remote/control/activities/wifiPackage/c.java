package com.viziotv.smartcast.remote.control.activities.wifiPackage;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class c {
    private String a = "Device";
    private String b;
    private String c;
    String d = "";

    class a implements Runnable {
        final Activity F0;

        class z implements Runnable {
            final c F0;

            z(  c cVar) {
                this.F0 = cVar;
            }

            public void run() {
                 if( c.this.d().contains("Vizio") ||   c.this.d().contains("VIZIO") |   c.this.d().contains("vizio"))
                {
                    Editor edit;
                    Log.d("Count", String.valueOf(WifiRemoteActivity.g1.getCount()));



                    if (WifiRemoteActivity.g1.getCount() > 0) {
                    } else if (  c.this.b().equals(WifiRemoteActivity.o1)) {
                        if (  c.this.b().equals(WifiRemoteActivity.o1)) {
                            if(WifiRemoteActivity.i1 != null)
                                WifiRemoteActivity.i1.setVisibility(View.VISIBLE);
                            if( WifiRemoteActivity.g1 != null)
                                WifiRemoteActivity.g1.notifyDataSetChanged();
                        }
                        if(WifiRemoteActivity.h1 != null)
                            WifiRemoteActivity.h1.setVisibility(View.GONE);
                        if(WifiRemoteActivity.j1 != null)
                            WifiRemoteActivity.j1.setVisibility(View.GONE);
                        if(WifiRemoteActivity.i1 != null)
                            WifiRemoteActivity.i1.setText( f.a);
                        edit = a.this.F0.getSharedPreferences("vizio_saved_device", 0).edit();
                        edit.putString("vizio_device", String.valueOf(  c.this.d()));
                        edit.apply();
                    }
                    if( WifiRemoteActivity.g1 != null) {
                        WifiRemoteActivity.g1.add(this.F0);
                    }


                    if( WifiRemoteActivity.f1 != null)
                        WifiRemoteActivity.f1.setAdapter(WifiRemoteActivity.g1);
                        if(WifiRemoteActivity.g1 != null)
                            WifiRemoteActivity.g1.notifyDataSetChanged();
                    if(WifiRemoteActivity.h1 != null)
                        WifiRemoteActivity.h1.setVisibility(View.GONE);
                    if(WifiRemoteActivity.j1 != null)
                        WifiRemoteActivity.j1.setVisibility(View.GONE);
                    if(WifiRemoteActivity.i1 != null)
                        WifiRemoteActivity.i1.setText( f.a);

                    edit = a.this.F0.getSharedPreferences("vizio_saved_device", 0).edit();
                    edit.putString("vizio_device", String.valueOf(  c.this.d()));
                    edit.apply();
                }

                if(WifiRemoteActivity.g1 != null)
                    WifiRemoteActivity.g1.notifyDataSetChanged();
            }
        }

        a(Activity activity) {
            this.F0 = activity;
        }

        public void run() {
            try {
               // System.out.println("url is "+c.this.c());
                if(c.this.c().trim().isEmpty())
                {
                    Log.d("Vizio","url is empty");
                   return;
                }
                Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new URL(  c.this.c()).openStream()));
                parse.getDocumentElement().normalize();
                Element element = (Element) parse.getElementsByTagName("device").item(0);
                String nodeValue = ((Element) element.getElementsByTagName("friendlyName").item(0)).getChildNodes().item(0).getNodeValue();
                Log.d("Device", nodeValue);
                HashMap<String, Object> datamap = new HashMap<>();
                datamap.put("timeStamp",new Date());
                datamap.put("DevName",nodeValue);

                String nodeValue2 = ((Element) element.getElementsByTagName("manufacturer").item(0)).getChildNodes().item(0).getNodeValue();
                NodeList childNodes = ((Element) element.getElementsByTagName("modelName").item(0)).getChildNodes();
                datamap.put("manufacturer", nodeValue2);
                datamap.put("modelName", childNodes);



                if (childNodes != null) {
                      c.this.d = childNodes.item(0).getNodeValue();
                }
                  c cVar =   c.this;
                if (cVar.d == null) {
                    cVar.d = "...";
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nodeValue);
                stringBuilder.append(" ( ");
                stringBuilder.append(nodeValue2);
                stringBuilder.append("+");
                stringBuilder.append(  c.this.d);
                stringBuilder.append(" )");
                cVar.i(stringBuilder.toString());
                this.F0.runOnUiThread(new z(new  c(  c.this.b,   c.this.d())));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            } catch (SAXException e4) {
                e4.printStackTrace();
            }
        }
    }

    public c(String str, int i, String str2, Activity activity) {
        this.b = str;
        g(str);
        str = e(str2, "LOCATION:");
        this.c = str;
        h(str);
        f(activity);
    }

    public c(String str, String str2) {
        this.b = str;
        g(str);
        i(str2);
    }

    private String e(String str, String str2) {
        int indexOf = str.indexOf(str2);
        if (indexOf == -1) {
            return "";
        }
        indexOf += str2.length();
        return str.substring(indexOf, str.indexOf("\r\n", indexOf));
    }

    private void f(Activity activity) {
        new Thread(new a(activity)).start();
    }

    public String b() {
        return this.b;
    }

    public String c() {
        return this.c;
    }

    public String d() {
        return this.a;
    }

    public void g(String str) {
        this.b = str;
    }

    public void h(String str) {
        this.c = str;
    }

    public void i(String str) {
        this.a = str;
    }

}

package com.connectsdk.service.upnp;

import com.connectsdk.core.MediaInfo;
import com.connectsdk.core.Util;
import com.connectsdk.discovery.provider.ssdp.SSDPClient;
import com.connectsdk.service.DLNAService;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.URLServiceSubscription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class DLNAHttpServer {
    final int port = 49291;
    volatile boolean running = false;
    CopyOnWriteArrayList<URLServiceSubscription<?>> subscriptions = new CopyOnWriteArrayList<>();
    volatile ServerSocket welcomeSocket;

    public synchronized void start() {
        if (!this.running) {
            this.running = true;
            try {
                Objects.requireNonNull(this);
                this.welcomeSocket = new ServerSocket(49291);
                Util.runInBackground(new Runnable() {
                    public void run() {
                        DLNAHttpServer.this.processRequests();
                    }
                }, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void stop() {
        if (this.running) {
            Iterator<URLServiceSubscription<?>> it = this.subscriptions.iterator();
            while (it.hasNext()) {
                it.next().unsubscribe();
            }
            this.subscriptions.clear();
            if (this.welcomeSocket != null && !this.welcomeSocket.isClosed()) {
                try {
                    this.welcomeSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            this.welcomeSocket = null;
            this.running = false;
        }
    }

    /* access modifiers changed from: private */
    public void processRequests() {
        while (this.running && this.welcomeSocket != null && !this.welcomeSocket.isClosed()) {
            BufferedReader inFromClient = null;
            DataOutputStream outToClient = null;
            try {
                Socket connectionSocket = this.welcomeSocket.accept();
                String body = null;
                try {
                    inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    do {
                        int read = inFromClient.read();
                        int c = read;
                        if (read == -1) {
                            break;
                        }
                        sb.append((char) c);
                    } while (!sb.toString().endsWith("\r\n\r\n"));
                    StringBuilder sb2 = new StringBuilder();
                    do {
                        int read2 = inFromClient.read();
                        int c2 = read2;
                        if (read2 == -1) {
                            break;
                        }
                        sb2.append((char) c2);
                        body = sb2.toString();
                    } while (!body.endsWith("</e:propertyset>"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                PrintWriter out = null;
                try {
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    out = new PrintWriter(outToClient);
                    out.println(SSDPClient.OK);
                    out.println("Connection: Close");
                    out.println("Content-Length: 0");
                    out.println();
                    out.flush();
                    try {
                        inFromClient.close();
                        out.close();
                        outToClient.close();
                        connectionSocket.close();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    } catch (NullPointerException ex3) {
                        ex3.printStackTrace();
                    }
                } catch (IOException ex4) {
                    ex4.printStackTrace();
                    inFromClient.close();
                    out.close();
                    outToClient.close();
                    connectionSocket.close();
                } catch (Throwable th) {
                    try {
                        inFromClient.close();
                        out.close();
                        outToClient.close();
                        connectionSocket.close();
                    } catch (IOException ex5) {
                        ex5.printStackTrace();
                    } catch (NullPointerException ex6) {
                        ex6.printStackTrace();
                    }
                    throw th;
                }
                if (body != null) {
                    InputStream stream = null;
                    try {
                        stream = new ByteArrayInputStream(body.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException ex7) {
                        ex7.printStackTrace();
                    }
                    try {
                        JSONArray propertySet = new DLNANotifyParser().parse(stream);
                        for (int i = 0; i < propertySet.length(); i++) {
                            JSONObject property = propertySet.getJSONObject(i);
                            if (property.has("LastChange")) {
                                handleLastChange(property.getJSONObject("LastChange"));
                            }
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    } catch (JSONException e3) {
                        e3.printStackTrace();
                    }
                }
            } catch (IOException ex8) {
                ex8.printStackTrace();
                return;
            }
        }
    }

    private void handleLastChange(JSONObject lastChange) throws JSONException {
        if (lastChange.has("InstanceID")) {
            JSONArray instanceIDs = lastChange.getJSONArray("InstanceID");
            for (int i = 0; i < instanceIDs.length(); i++) {
                JSONArray events = instanceIDs.getJSONArray(i);
                for (int j = 0; j < events.length(); j++) {
                    handleEntry(events.getJSONObject(j));
                }
            }
        }
    }

    private void handleEntry(JSONObject entry) throws JSONException {
        boolean mute;
        if (entry.has("TransportState")) {
            MediaControl.PlayStateStatus status = MediaControl.PlayStateStatus.convertTransportStateToPlayStateStatus(entry.getString("TransportState"));
            Iterator<URLServiceSubscription<?>> it = this.subscriptions.iterator();
            while (it.hasNext()) {
                URLServiceSubscription<?> sub = it.next();
                if (sub.getTarget().equalsIgnoreCase(DLNAService.PLAY_STATE)) {
                    for (int j = 0; j < sub.getListeners().size(); j++) {
                        Util.postSuccess((ResponseListener) sub.getListeners().get(j), status);
                    }
                }
            }
        }
        if ((entry.has("Volume") && !entry.has("channel")) || (entry.has("Volume") && entry.getString("channel").equals("Master"))) {
            float volume = ((float) entry.getInt("Volume")) / 100.0f;
            Iterator<URLServiceSubscription<?>> it2 = this.subscriptions.iterator();
            while (it2.hasNext()) {
                URLServiceSubscription<?> sub2 = it2.next();
                if (sub2.getTarget().equalsIgnoreCase("volume")) {
                    for (int j2 = 0; j2 < sub2.getListeners().size(); j2++) {
                        Util.postSuccess((ResponseListener) sub2.getListeners().get(j2), Float.valueOf(volume));
                    }
                }
            }
        }
        if ((entry.has("Mute") && !entry.has("channel")) || (entry.has("Mute") && entry.getString("channel").equals("Master"))) {
            String muteStatus = entry.getString("Mute");
            try {
                boolean z = true;
                if (Integer.parseInt(muteStatus) != 1) {
                    z = false;
                }
                mute = z;
            } catch (NumberFormatException e) {
                mute = Boolean.parseBoolean(muteStatus);
            }
            Iterator<URLServiceSubscription<?>> it3 = this.subscriptions.iterator();
            while (it3.hasNext()) {
                URLServiceSubscription<?> sub3 = it3.next();
                if (sub3.getTarget().equalsIgnoreCase("mute")) {
                    for (int j3 = 0; j3 < sub3.getListeners().size(); j3++) {
                        Util.postSuccess((ResponseListener) sub3.getListeners().get(j3), Boolean.valueOf(mute));
                    }
                }
            }
        }
        if (entry.has("CurrentTrackMetaData")) {
            MediaInfo info = DLNAMediaInfoParser.getMediaInfo(entry.getString("CurrentTrackMetaData"));
            Iterator<URLServiceSubscription<?>> it4 = this.subscriptions.iterator();
            while (it4.hasNext()) {
                URLServiceSubscription<?> sub4 = it4.next();
                if (sub4.getTarget().equalsIgnoreCase("info")) {
                    for (int j4 = 0; j4 < sub4.getListeners().size(); j4++) {
                        Util.postSuccess((ResponseListener) sub4.getListeners().get(j4), info);
                    }
                }
            }
        }
    }

    public int getPort() {
        return 49291;
    }

    public List<URLServiceSubscription<?>> getSubscriptions() {
        return this.subscriptions;
    }

    public void setSubscriptions(List<URLServiceSubscription<?>> subscriptions2) {
        this.subscriptions = new CopyOnWriteArrayList<>(subscriptions2);
    }

    public boolean isRunning() {
        return this.running;
    }
}

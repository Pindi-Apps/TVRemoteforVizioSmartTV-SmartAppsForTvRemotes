package com.connectsdk.service;

import android.graphics.PointF;
import android.util.Log;

import com.connectsdk.core.AppInfo;
import com.connectsdk.core.ChannelInfo;
import com.connectsdk.core.ExternalInputInfo;
import com.connectsdk.core.MediaInfo;
import com.connectsdk.core.Util;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryFilter;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.etc.helper.DeviceServiceReachability;
import com.connectsdk.etc.helper.HttpConnection;
import com.connectsdk.etc.helper.HttpMessage;
import com.connectsdk.service.capability.CapabilityMethods;
import com.connectsdk.service.capability.ExternalInputControl;
import com.connectsdk.service.capability.KeyControl;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.MouseControl;
import com.connectsdk.service.capability.PowerControl;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.TextInputControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.NotSupportedServiceSubscription;
import com.connectsdk.service.command.ServiceCommand;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.connectsdk.service.command.URLServiceSubscription;
import com.connectsdk.service.config.NetcastTVServiceConfig;
import com.connectsdk.service.config.ServiceConfig;
import com.connectsdk.service.config.ServiceDescription;
import com.connectsdk.service.netcast.NetcastAppNumberParser;
import com.connectsdk.service.netcast.NetcastApplicationsParser;
import com.connectsdk.service.netcast.NetcastChannelParser;
import com.connectsdk.service.netcast.NetcastHttpServer;
import com.connectsdk.service.netcast.NetcastVirtualKeycodes;
import com.connectsdk.service.netcast.NetcastVolumeParser;
import com.connectsdk.service.sessions.LaunchSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NetcastTVService extends DeviceService implements Launcher, MediaControl, MediaPlayer, TVControl, VolumeControl, ExternalInputControl, MouseControl, TextInputControl, PowerControl, KeyControl {
    public static final String ID = "Netcast TV";
    public static final String ROAP_PATH_APP_STORE = "/roap/api/command/";
    public static final String SMART_SHARE = "SmartShare™";
    public static final String TARGET_3D_MODE = "3DMode";
    public static final String TARGET_APPLIST_GET = "applist_get";
    public static final String TARGET_APPNUM_GET = "appnum_get";
    public static final String TARGET_CHANNEL_LIST = "channel_list";
    public static final String TARGET_CURRENT_CHANNEL = "cur_channel";
    public static final String TARGET_IS_3D = "is_3D";
    public static final String TARGET_VOLUME_INFO = "volume_info";
    public static final String UDAP_API_COMMAND = "command";
    public static final String UDAP_API_EVENT = "event";
    public static final String UDAP_API_PAIRING = "pairing";
    public static final String UDAP_PATH_APPTOAPP_COMMAND = "/udap/api/apptoapp/command/";
    public static final String UDAP_PATH_APPTOAPP_DATA = "/udap/api/apptoapp/data/";
    public static final String UDAP_PATH_COMMAND = "/udap/api/command";
    public static final String UDAP_PATH_DATA = "/udap/api/data";
    public static final String UDAP_PATH_EVENT = "/udap/api/event";
    public static final String UDAP_PATH_PAIRING = "/udap/api/pairing";
    List<AppInfo> applications;
    DIALService dialService;
    DLNAService dlnaService;
    NetcastHttpServer httpServer;
    LaunchSession inputPickerSession;
    StringBuilder keyboardString;
    PointF mMouseDistance;
    Boolean mMouseIsMoving;
    private ResponseListener<String> mTextChangedListener = new ResponseListener<String>() {
        /* class com.connectsdk.service.NetcastTVService.AnonymousClass36 */

        @Override // com.connectsdk.service.capability.listeners.ErrorListener
        public void onError(ServiceCommandError serviceCommandError) {
        }

        public void onSuccess(String str) {
            NetcastTVService.this.keyboardString = new StringBuilder(str);
        }
    };
    State state = State.INITIAL;
    List<URLServiceSubscription<?>> subscriptions;

    /* access modifiers changed from: package-private */
    public enum State {
        NONE,
        INITIAL,
        CONNECTING,
        PAIRING,
        PAIRED,
        DISCONNECTING
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public void button(String str) {
    }

    @Override // com.connectsdk.service.capability.ExternalInputControl
    public ExternalInputControl getExternalInput() {
        return this;
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public KeyControl getKeyControl() {
        return this;
    }

    @Override // com.connectsdk.service.capability.Launcher
    public Launcher getLauncher() {
        return this;
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public MediaPlayer getMediaPlayer() {
        return this;
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public MouseControl getMouseControl() {
        return this;
    }

    @Override // com.connectsdk.service.capability.PowerControl
    public PowerControl getPowerControl() {
        return this;
    }

    @Override // com.connectsdk.service.capability.TVControl
    public TVControl getTVControl() {
        return this;
    }

    @Override // com.connectsdk.service.capability.TextInputControl
    public TextInputControl getTextInputControl() {
        return this;
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public VolumeControl getVolumeControl() {
        return this;
    }

    @Override // com.connectsdk.service.DeviceService
    public boolean isConnectable() {
        return true;
    }

    public NetcastTVService(ServiceDescription serviceDescription, ServiceConfig serviceConfig) {
        super(serviceDescription, serviceConfig);
        this.pairingType = PairingType.PIN_CODE;
        if (serviceDescription.getPort() != 8080) {
            serviceDescription.setPort(8080);
        }
        this.applications = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
        this.keyboardString = new StringBuilder();
        this.state = State.INITIAL;
        this.inputPickerSession = null;
    }

    public static DiscoveryFilter discoveryFilter() {
        return new DiscoveryFilter(ID, "urn:schemas-upnp-org:device:MediaRenderer:1");
    }

    @Override // com.connectsdk.service.DeviceService
    public CapabilityPriorityLevel getPriorityLevel(Class<? extends CapabilityMethods> cls) {
        if (cls.equals(MediaPlayer.class)) {
            return getMediaPlayerCapabilityLevel();
        }
        if (cls.equals(MediaControl.class)) {
            return getMediaControlCapabilityLevel();
        }
        if (cls.equals(Launcher.class)) {
            return getLauncherCapabilityLevel();
        }
        if (cls.equals(TVControl.class)) {
            return getTVControlCapabilityLevel();
        }
        if (cls.equals(VolumeControl.class)) {
            return getVolumeControlCapabilityLevel();
        }
        if (cls.equals(ExternalInputControl.class)) {
            return getExternalInputControlPriorityLevel();
        }
        if (cls.equals(MouseControl.class)) {
            return getMouseControlCapabilityLevel();
        }
        if (cls.equals(TextInputControl.class)) {
            return getTextInputControlCapabilityLevel();
        }
        if (cls.equals(PowerControl.class)) {
            return getPowerControlCapabilityLevel();
        }
        if (cls.equals(KeyControl.class)) {
            return getKeyControlCapabilityLevel();
        }
        return CapabilityPriorityLevel.NOT_SUPPORTED;
    }

    @Override // com.connectsdk.service.DeviceService
    public void setServiceDescription(ServiceDescription serviceDescription) {
        super.setServiceDescription(serviceDescription);
        if (serviceDescription.getPort() != 8080) {
            serviceDescription.setPort(8080);
        }
    }

    @Override // com.connectsdk.service.DeviceService
    public void connect() {
        if (this.state != State.INITIAL) {
            String str = Util.T;
            Log.w(str, "already connecting; not trying to connect again: " + this.state);
            return;
        }
        if (!(this.serviceConfig instanceof NetcastTVServiceConfig)) {
            ServiceConfig.ServiceConfigListener listener = this.serviceConfig.getListener();
            this.serviceConfig = new NetcastTVServiceConfig(this.serviceConfig.getServiceUUID());
            this.serviceConfig.setListener(listener);
        }
        if (DiscoveryManager.getInstance().getPairingLevel() == DiscoveryManager.PairingLevel.ON) {
            if (((NetcastTVServiceConfig) this.serviceConfig).getPairingKey() == null || ((NetcastTVServiceConfig) this.serviceConfig).getPairingKey().length() == 0) {
                showPairingKeyOnTV();
            } else {
                sendPairingKey(((NetcastTVServiceConfig) this.serviceConfig).getPairingKey());
            }
            Util.runInBackground(new Runnable() {
                /* class com.connectsdk.service.NetcastTVService.AnonymousClass1 */

                public void run() {
                    NetcastTVService netcastTVService = NetcastTVService.this;
                    netcastTVService.httpServer = new NetcastHttpServer(netcastTVService, netcastTVService.getServiceDescription().getPort(), NetcastTVService.this.mTextChangedListener);
                    NetcastTVService.this.httpServer.setSubscriptions(NetcastTVService.this.subscriptions);
                    NetcastTVService.this.httpServer.start();
                }
            });
            return;
        }
        hConnectSuccess();
    }

    @Override // com.connectsdk.service.DeviceService
    public void disconnect() {
        endPairing(null);
        this.connected = false;
        if (this.mServiceReachability != null) {
            this.mServiceReachability.stop();
        }
        Util.runOnUI(new Runnable() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass2 */

            public void run() {
                if (NetcastTVService.this.listener != null) {
                    NetcastTVService.this.listener.onDisconnect(NetcastTVService.this, null);
                }
            }
        });
        NetcastHttpServer netcastHttpServer = this.httpServer;
        if (netcastHttpServer != null) {
            netcastHttpServer.stop();
            this.httpServer = null;
        }
        this.state = State.INITIAL;
    }

    @Override // com.connectsdk.service.DeviceService
    public boolean isConnected() {
        return this.connected;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void hConnectSuccess() {
        this.connected = true;
        reportConnected(true);
    }

    @Override // com.connectsdk.service.DeviceService, com.connectsdk.etc.helper.DeviceServiceReachability.DeviceServiceReachabilityListener
    public void onLoseReachability(DeviceServiceReachability deviceServiceReachability) {
        if (this.connected) {
            disconnect();
        } else if (this.mServiceReachability != null) {
            this.mServiceReachability.stop();
        }
    }

    public void hostByeBye() {
        disconnect();
    }

    public void showPairingKeyOnTV() {
        this.state = State.CONNECTING;
        ResponseListener r0 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass3 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                if (NetcastTVService.this.listener != null) {
                    DeviceServiceListener deviceServiceListener = NetcastTVService.this.listener;
                    NetcastTVService netcastTVService = NetcastTVService.this;
                    deviceServiceListener.onPairingRequired(netcastTVService, netcastTVService.pairingType, null);
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                NetcastTVService.this.state = State.INITIAL;
                if (NetcastTVService.this.listener != null) {
                    NetcastTVService.this.listener.onConnectionFailure(NetcastTVService.this, serviceCommandError);
                }
            }
        };
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_PAIRING);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "showKey");
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_PAIRING, hashMap), r0).send();
    }

    @Override // com.connectsdk.service.DeviceService
    public void cancelPairing() {
        removePairingKeyOnTV();
        this.state = State.INITIAL;
    }

    public void removePairingKeyOnTV() {
        ResponseListener r0 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass4 */

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
            }

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
            }
        };
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_PAIRING);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "CancelAuthKeyReq");
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_PAIRING, hashMap), r0).send();
    }

    @Override // com.connectsdk.service.DeviceService
    public void sendPairingKey(final String str) {
        this.state = State.PAIRING;
        if (!(this.serviceConfig instanceof NetcastTVServiceConfig)) {
            this.serviceConfig = new NetcastTVServiceConfig(this.serviceConfig.getServiceUUID());
        }
        ResponseListener r0 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass5 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                NetcastTVService.this.state = State.PAIRED;
                ((NetcastTVServiceConfig) NetcastTVService.this.serviceConfig).setPairingKey(str);
                NetcastTVService.this.hConnectSuccess();
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                NetcastTVService.this.state = State.INITIAL;
                if (NetcastTVService.this.listener != null) {
                    NetcastTVService.this.listener.onConnectionFailure(NetcastTVService.this, serviceCommandError);
                }
            }
        };
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_PAIRING);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "hello");
        hashMap.put("port", String.valueOf(this.serviceDescription.getPort()));
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_PAIRING, hashMap), r0).send();
    }

    private void endPairing(ResponseListener<Object> responseListener) {
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_PAIRING);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "byebye");
        hashMap.put("port", String.valueOf(this.serviceDescription.getPort()));
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_PAIRING, hashMap), responseListener).send();
    }

    @Override // com.connectsdk.service.capability.Launcher
    public CapabilityPriorityLevel getLauncherCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    class NetcastTVLaunchSessionR extends LaunchSession {
        String appName;
        NetcastTVService service;

        @Override // com.connectsdk.service.sessions.LaunchSession
        public void close(ResponseListener<Object> responseListener) {
        }

        NetcastTVLaunchSessionR(NetcastTVService netcastTVService, String str, String str2) {
            this.service = netcastTVService;
            this.appId = str;
        }

        NetcastTVLaunchSessionR(NetcastTVService netcastTVService, JSONObject jSONObject) throws JSONException {
            this.service = netcastTVService;
            fromJSONObject(jSONObject);
        }

        @Override // com.connectsdk.core.JSONSerializable, com.connectsdk.service.sessions.LaunchSession
        public JSONObject toJSONObject() throws JSONException {
            JSONObject jSONObject = super.toJSONObject();
            jSONObject.put("type", "netcasttv");
            jSONObject.put("appName", this.appName);
            return jSONObject;
        }

        @Override // com.connectsdk.core.JSONDeserializable, com.connectsdk.service.sessions.LaunchSession
        public void fromJSONObject(JSONObject jSONObject) throws JSONException {
            super.fromJSONObject(jSONObject);
            this.appName = jSONObject.optString("appName");
        }
    }

    public void getApplication(String str, final AppInfoListener appInfoListener) {
        ResponseListener r0 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass6 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                Util.postSuccess(appInfoListener, new AppInfo((String) obj) {
                    /* class com.connectsdk.service.NetcastTVService.AnonymousClass6.AnonymousClass1 */
                    final /* synthetic */ String val$strObj;

                    {
                        this.val$strObj = (String) obj;
                        setId(NetcastTVService.this.decToHex(val$strObj));
                    }
                });
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                if (appInfoListener != null) {
                    Util.postError(appInfoListener, serviceCommandError);
                }
            }
        };
        ServiceCommand<ResponseListener<Object>> serviceCommand = new ServiceCommand<ResponseListener<Object>>(this, getUDAPRequestURL(UDAP_PATH_APPTOAPP_DATA + str), null, r0);
        serviceCommand.setHttpMethod(ServiceCommand.TYPE_GET);
        serviceCommand.send();
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchApp(String str, final AppLaunchListener appLaunchListener) {
        getAppInfoForId(str, new AppInfoListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass7 */

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appLaunchListener, serviceCommandError);
            }

            public void onSuccess(AppInfo appInfo) {
                NetcastTVService.this.launchAppWithInfo(appInfo, appLaunchListener);
            }
        });
    }

    private void getAppInfoForId(final String str, final AppInfoListener appInfoListener) {
        getAppList(new AppListListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass8 */

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appInfoListener, serviceCommandError);
            }

            public void onSuccess(List<AppInfo> list) {
                for (AppInfo appInfo : list) {
                    if (appInfo.getName().equalsIgnoreCase(str)) {
                        Util.postSuccess(appInfoListener, appInfo);
                        return;
                    }
                }
                Util.postError(appInfoListener, new ServiceCommandError(0, "Unable to find the App with id", null));
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void launchApplication(final String str, final String str2, String str3, final AppLaunchListener appLaunchListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("id", str2);
            jSONObject.put("title", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ResponseListener r0 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass9 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                LaunchSession launchSessionForAppId = LaunchSession.launchSessionForAppId(str2);
                launchSessionForAppId.setAppName(str);
                launchSessionForAppId.setService(NetcastTVService.this);
                launchSessionForAppId.setSessionType(LaunchSession.LaunchSessionType.App);
                Util.postSuccess(appLaunchListener, launchSessionForAppId);
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appLaunchListener, serviceCommandError);
            }
        };
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_APPTOAPP_COMMAND);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "AppExecute");
        hashMap.put("auid", str2);
        if (str != null) {
            hashMap.put("appname", str);
        }
        if (str3 != null) {
            hashMap.put("contentid", str3);
        }
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_COMMAND, hashMap), r0).send();
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchAppWithInfo(AppInfo appInfo, AppLaunchListener appLaunchListener) {
        launchAppWithInfo(appInfo, null, appLaunchListener);
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchAppWithInfo(AppInfo appInfo, Object obj, AppLaunchListener appLaunchListener) {
        String encode = HttpMessage.encode(appInfo.getName());
        String id = appInfo.getId();
        String str = null;
        JSONObject jSONObject = obj instanceof JSONObject ? (JSONObject) obj : null;
        if (jSONObject != null) {
            try {
                str = (String) jSONObject.get("contentId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        launchApplication(encode, id, str, appLaunchListener);
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchBrowser(String str, final AppLaunchListener appLaunchListener) {
        if (!(str == null || str.length() == 0)) {
            Log.w(Util.T, "Netcast TV does not support deeplink for Browser");
        }
        getApplication("Internet", new AppInfoListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass10 */

            public void onSuccess(AppInfo appInfo) {
                NetcastTVService.this.launchApplication("Internet", appInfo.getId(), null, appLaunchListener);
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appLaunchListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchYouTube(String str, AppLaunchListener appLaunchListener) {
        launchYouTube(str, 0.0f, appLaunchListener);
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchYouTube(final String str, float f, final AppLaunchListener appLaunchListener) {
        if (getDIALService() != null) {
            getDIALService().getLauncher().launchYouTube(str, f, appLaunchListener);
        } else if (((double) f) <= 0.0d) {
            getApplication("YouTube", new AppInfoListener() {
                /* class com.connectsdk.service.NetcastTVService.AnonymousClass11 */

                public void onSuccess(AppInfo appInfo) {
                    NetcastTVService.this.launchApplication(appInfo.getName(), appInfo.getId(), str, appLaunchListener);
                }

                @Override // com.connectsdk.service.capability.listeners.ErrorListener
                public void onError(ServiceCommandError serviceCommandError) {
                    Util.postError(appLaunchListener, serviceCommandError);
                }
            });
        } else {
            Util.postError(appLaunchListener, new ServiceCommandError(0, "Cannot reach DIAL service for launching with provided start time", null));
        }
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchHulu(final String str, final AppLaunchListener appLaunchListener) {
        getApplication("Hulu", new AppInfoListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass12 */

            public void onSuccess(AppInfo appInfo) {
                NetcastTVService.this.launchApplication("Hulu", appInfo.getId(), str, appLaunchListener);
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appLaunchListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchNetflix(final String str, final AppLaunchListener appLaunchListener) {
        if (!this.serviceDescription.getModelNumber().equals("4.0")) {
            launchApp("Netflix", appLaunchListener);
        } else {
            getApplication("Netflix", new AppInfoListener() {
                /* class com.connectsdk.service.NetcastTVService.AnonymousClass13 */

                public void onSuccess(final AppInfo appInfo) {
                    JSONObject jSONObject = new JSONObject();
                    try {
                        jSONObject.put("id", appInfo.getId());
                        jSONObject.put("name", "Netflix");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ResponseListener r1 = new ResponseListener<Object>() {
                        /* class com.connectsdk.service.NetcastTVService.AnonymousClass13.AnonymousClass1 */

                        @Override // com.connectsdk.service.capability.listeners.ResponseListener
                        public void onSuccess(Object obj) {
                            LaunchSession launchSessionForAppId = LaunchSession.launchSessionForAppId(appInfo.getId());
                            launchSessionForAppId.setAppName("Netflix");
                            launchSessionForAppId.setService(NetcastTVService.this);
                            launchSessionForAppId.setSessionType(LaunchSession.LaunchSessionType.App);
                            Util.postSuccess(appLaunchListener, launchSessionForAppId);
                        }

                        @Override // com.connectsdk.service.capability.listeners.ErrorListener
                        public void onError(ServiceCommandError serviceCommandError) {
                            Util.postError(appLaunchListener, serviceCommandError);
                        }
                    };
                    String uDAPRequestURL = NetcastTVService.this.getUDAPRequestURL(NetcastTVService.UDAP_PATH_APPTOAPP_COMMAND);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", "SearchCMDPlaySDPContent");
                    hashMap.put("content_type", "1");
                    hashMap.put("conts_exec_type", "20");
                    hashMap.put("conts_plex_type_flag", "N");
                    hashMap.put("conts_search_id", "2023237");
                    hashMap.put("conts_age", "18");
                    hashMap.put("exec_id", "netflix");
                    hashMap.put("item_id", "-Q m=http%3A%2F%2Fapi.netflix.com%2Fcatalog%2Ftitles%2Fmovies%2F" + str + "&amp;source_type=4&amp;trackId=6054700&amp;trackUrl=https%3A%2F%2Fapi.netflix.com%2FAPI_APP_ID_6261%3F%23Search%3F");
                    hashMap.put("app_type", "");
                    new ServiceCommand(NetcastTVService.this, uDAPRequestURL, NetcastTVService.this.getUDAPMessageBody(NetcastTVService.UDAP_API_COMMAND, hashMap), r1).send();
                }

                @Override // com.connectsdk.service.capability.listeners.ErrorListener
                public void onError(ServiceCommandError serviceCommandError) {
                    Util.postError(appLaunchListener, serviceCommandError);
                }
            });
        }
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void launchAppStore(final String str, final AppLaunchListener appLaunchListener) {
        if (!this.serviceDescription.getModelNumber().equals("4.0")) {
            launchApp("LG Smart World", appLaunchListener);
            return;
        }
        String uDAPRequestURL = getUDAPRequestURL(ROAP_PATH_APP_STORE);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "SearchCMDPlaySDPContent");
        hashMap.put("content_type", "4");
        hashMap.put("conts_exec_type", "");
        hashMap.put("conts_plex_type_flag", "");
        hashMap.put("conts_search_id", "");
        hashMap.put("conts_age", "12");
        hashMap.put("exec_id", "");
        hashMap.put("item_id", HttpMessage.encode(str));
        hashMap.put("app_type", "S");
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_COMMAND, hashMap), new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass14 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                LaunchSession launchSessionForAppId = LaunchSession.launchSessionForAppId(str);
                launchSessionForAppId.setAppName("LG Smart World");
                launchSessionForAppId.setService(NetcastTVService.this);
                launchSessionForAppId.setSessionType(LaunchSession.LaunchSessionType.App);
                Util.postSuccess(appLaunchListener, launchSessionForAppId);
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appLaunchListener, serviceCommandError);
            }
        }).send();
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void closeApp(LaunchSession launchSession, ResponseListener<Object> responseListener) {
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_APPTOAPP_COMMAND);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "AppTerminate");
        hashMap.put("auid", launchSession.getAppId());
        if (launchSession.getAppName() != null) {
            hashMap.put("appname", HttpMessage.encode(launchSession.getAppName()));
        }
        new ServiceCommand(launchSession.getService(), uDAPRequestURL, getUDAPMessageBody(UDAP_API_COMMAND, hashMap), responseListener).send();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void getTotalNumberOfApplications(int i, final AppCountListener appCountListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand = new ServiceCommand<>(this, getUDAPRequestURL(UDAP_PATH_DATA, TARGET_APPNUM_GET, String.valueOf(i)), null, new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass15 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                Util.postSuccess(appCountListener, Integer.valueOf(NetcastTVService.this.parseAppNumberXmlToJSON((String) obj)));
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appCountListener, serviceCommandError);
            }
        });
        serviceCommand.setHttpMethod(ServiceCommand.TYPE_GET);
        serviceCommand.send();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */


        private void getApplications(int type, int number, final AppListListener listener) {
        ResponseListener<Object> responseListener = new ResponseListener<Object>() {

            @Override
            public void onSuccess(Object response) {
                String strObj = (String) response;

                JSONArray applicationArray = parseApplicationsXmlToJSON(strObj);
                List<AppInfo> appList = new ArrayList<AppInfo>();

                for (int i = 0; i < applicationArray.length(); i++)
                {
                    try {
                        final JSONObject appJSON = applicationArray.getJSONObject(i);

                        AppInfo appInfo = new AppInfo() {{
                            setId(appJSON.getString("id"));
                            setName(appJSON.getString("title"));
                        }};

                        appList.add(appInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Util.postSuccess(listener, appList);
            }

            @Override
            public void onError(ServiceCommandError error) {
                Util.postError(listener, error);
            }
        };

        String requestURL = getUDAPRequestURL(UDAP_PATH_DATA, TARGET_APPLIST_GET, String.valueOf(type), "0", String.valueOf(number));

        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
        command.setHttpMethod(ServiceCommand.TYPE_GET);
        command.send();
    }




//    private void getApplications(int i, int i2, final Launcher.AppListListener appListListener) {
//        ServiceCommand serviceCommand = new ServiceCommand(this, getUDAPRequestURL(UDAP_PATH_DATA, TARGET_APPLIST_GET, String.valueOf(i), "0", String.valueOf(i2)), null, new ResponseListener<Object>() {
//            /* class com.connectsdk.service.NetcastTVService.AnonymousClass16 */
//
//            @Override // com.connectsdk.service.capability.listeners.ResponseListener
//            public void onSuccess(Object obj) {
//                JSONArray parseApplicationsXmlToJSON = NetcastTVService.this.parseApplicationsXmlToJSON((String) obj);
//                ArrayList arrayList = new ArrayList();
//                for (int i = 0; i < parseApplicationsXmlToJSON.length(); i++) {
//                    try {
//                        arrayList.add(new AppInfo(parseApplicationsXmlToJSON.getJSONObject(i)) {
//                            /* class com.connectsdk.service.NetcastTVService.AnonymousClass16.AnonymousClass1 */
//                            final /* synthetic */ JSONObject val$appJSON;
//
//                            {
//                                this.val$appJSON = (JSONObject) obj;
//                                setId(val$appJSON.getString("id"));
//                                setName(val$appJSON.getString("title"));
//                            }
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Util.postSuccess(appListListener, arrayList);
//            }
//
//            @Override // com.connectsdk.service.capability.listeners.ErrorListener
//            public void onError(ServiceCommandError serviceCommandError) {
//                Util.postError(appListListener, serviceCommandError);
//            }
//        });
//        serviceCommand.setHttpMethod(ServiceCommand.TYPE_GET);
//        serviceCommand.send();
//    }

    @Override // com.connectsdk.service.capability.Launcher
    public void getAppList(final AppListListener appListListener) {
        this.applications.clear();
        getTotalNumberOfApplications(2, new AppCountListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass17 */

            public void onSuccess(Integer num) {
                NetcastTVService.this.getApplications(2, num.intValue(), new AppListListener() {
                    /* class com.connectsdk.service.NetcastTVService.AnonymousClass17.AnonymousClass1 */

                    public void onSuccess(List<AppInfo> list) {
                        NetcastTVService.this.applications.addAll(list);
                        NetcastTVService.this.getTotalNumberOfApplications(3, new AppCountListener() {
                            /* class com.connectsdk.service.NetcastTVService.AnonymousClass17.AnonymousClass1.AnonymousClass1 */

                            public void onSuccess(Integer num) {
                                NetcastTVService.this.getApplications(3, num.intValue(), new AppListListener() {
                                    /* class com.connectsdk.service.NetcastTVService.AnonymousClass17.AnonymousClass1.AnonymousClass1.AnonymousClass1 */

                                    public void onSuccess(List<AppInfo> list) {
                                        NetcastTVService.this.applications.addAll(list);
                                        Util.postSuccess(appListListener, NetcastTVService.this.applications);
                                    }

                                    @Override // com.connectsdk.service.capability.listeners.ErrorListener
                                    public void onError(ServiceCommandError serviceCommandError) {
                                        Util.postError(appListListener, serviceCommandError);
                                    }
                                });
                            }

                            @Override // com.connectsdk.service.capability.listeners.ErrorListener
                            public void onError(ServiceCommandError serviceCommandError) {
                                Util.postError(appListListener, serviceCommandError);
                            }
                        });
                    }

                    @Override // com.connectsdk.service.capability.listeners.ErrorListener
                    public void onError(ServiceCommandError serviceCommandError) {
                        Util.postError(appListListener, serviceCommandError);
                    }
                });
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appListListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void getRunningApp(AppInfoListener appInfoListener) {
        Util.postError(appInfoListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.Launcher
    public ServiceSubscription<AppInfoListener> subscribeRunningApp(AppInfoListener appInfoListener) {
        Util.postError(appInfoListener, ServiceCommandError.notSupported());
        return new NotSupportedServiceSubscription<>();
    }

    @Override // com.connectsdk.service.capability.Launcher
    public void getAppState(LaunchSession launchSession, final AppStateListener appStateListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand = new ServiceCommand<>(this, String.format(Locale.US, "%s%s", getUDAPRequestURL(UDAP_PATH_APPTOAPP_DATA), String.format(Locale.US, "/%s/status", launchSession.getAppId())), null, new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass18 */

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appStateListener, serviceCommandError);
            }

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                AppState appState;
                String str = (String) obj;
                if (str.equalsIgnoreCase("NONE")) {
                    appState = new AppState(false, false);
                } else if (str.equalsIgnoreCase("LOAD")) {
                    appState = new AppState(false, true);
                } else if (str.equalsIgnoreCase("RUN_NF")) {
                    appState = new AppState(true, false);
                } else if (str.equalsIgnoreCase("TERM")) {
                    appState = new AppState(false, true);
                } else {
                    appState = new AppState(false, false);
                }
                Util.postSuccess(appStateListener, appState);
            }
        });
        serviceCommand.setHttpMethod(ServiceCommand.TYPE_GET);
        serviceCommand.send();
    }

    @Override // com.connectsdk.service.capability.Launcher
    public ServiceSubscription<AppStateListener> subscribeAppState(LaunchSession launchSession, AppStateListener appStateListener) {
        Util.postError(appStateListener, ServiceCommandError.notSupported());
        return null;
    }

    @Override // com.connectsdk.service.capability.TVControl
    public CapabilityPriorityLevel getTVControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void getChannelList(final ChannelListListener channelListListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand = new ServiceCommand<>(this, getUDAPRequestURL(UDAP_PATH_DATA, TARGET_CHANNEL_LIST), null, new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass19 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                String str = (String) obj;
                try {
                    SAXParserFactory newInstance = SAXParserFactory.newInstance();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
                    SAXParser newSAXParser = newInstance.newSAXParser();
                    NetcastChannelParser netcastChannelParser = new NetcastChannelParser();
                    newSAXParser.parse(byteArrayInputStream, netcastChannelParser);
                    JSONArray jSONChannelArray = netcastChannelParser.getJSONChannelArray();
                    ArrayList<ChannelInfo> arrayList = new ArrayList<>();
                    for (int i = 0; i < jSONChannelArray.length(); i++) {
                        try {
                            arrayList.add(NetcastChannelParser.parseRawChannelData((JSONObject) jSONChannelArray.get(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Util.postSuccess(channelListListener, arrayList);
                } catch (ParserConfigurationException e2) {
                    e2.printStackTrace();
                } catch (SAXException e3) {
                    e3.printStackTrace();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(channelListListener, serviceCommandError);
            }
        });
        serviceCommand.setHttpMethod(ServiceCommand.TYPE_GET);
        serviceCommand.send();
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void channelUp(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.CHANNEL_UP.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void channelDown(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.CHANNEL_DOWN.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void setChannel(final ChannelInfo channelInfo, final ResponseListener<Object> responseListener) {
        getChannelList(new ChannelListListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass20 */

            public void onSuccess(List<ChannelInfo> list) {
                String uDAPRequestURL = NetcastTVService.this.getUDAPRequestURL(NetcastTVService.UDAP_PATH_COMMAND);
                HashMap<String, String> hashMap = new HashMap<>();
                int i = 0;
                while (true) {
                    if (i >= list.size()) {
                        break;
                    }
                    ChannelInfo channelInfo = list.get(i);
                    JSONObject rawData = channelInfo.getRawData();
                    try {
                        String str = channelInfo.getNumber().split("-")[0];
                        String str2 = channelInfo.getNumber().split("-")[1];
                        int majorNumber = channelInfo.getMajorNumber();
                        int minorNumber = channelInfo.getMinorNumber();
                        String str3 = (String) rawData.get("sourceIndex");
                        int intValue = ((Integer) rawData.get("physicalNumber")).intValue();
                        if (Integer.valueOf(str).intValue() == majorNumber && Integer.valueOf(str2).intValue() == minorNumber) {
                            hashMap.put("name", "HandleChannelChange");
                            hashMap.put("major", str);
                            hashMap.put("minor", str2);
                            hashMap.put("sourceIndex", str3);
                            hashMap.put("physicalNum", String.valueOf(intValue));
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                new ServiceCommand(NetcastTVService.this, uDAPRequestURL, NetcastTVService.this.getUDAPMessageBody(NetcastTVService.UDAP_API_COMMAND, hashMap), responseListener).send();
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(responseListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void getCurrentChannel(final ChannelListener channelListener) {
        new ServiceCommand(this, getUDAPRequestURL(UDAP_PATH_DATA, TARGET_CURRENT_CHANNEL), null, new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass21 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                String str = (String) obj;
                try {
                    SAXParserFactory newInstance = SAXParserFactory.newInstance();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
                    SAXParser newSAXParser = newInstance.newSAXParser();
                    NetcastChannelParser netcastChannelParser = new NetcastChannelParser();
                    newSAXParser.parse(byteArrayInputStream, netcastChannelParser);
                    JSONArray jSONChannelArray = netcastChannelParser.getJSONChannelArray();
                    if (jSONChannelArray.length() > 0) {
                        Util.postSuccess(channelListener, NetcastChannelParser.parseRawChannelData((JSONObject) jSONChannelArray.get(0)));
                    }
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e2) {
                    e2.printStackTrace();
                } catch (IOException e3) {
                    e3.printStackTrace();
                } catch (JSONException e4) {
                    e4.printStackTrace();
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(channelListener, serviceCommandError);
            }
        }).send();
    }

    @Override // com.connectsdk.service.capability.TVControl
    public ServiceSubscription<ChannelListener> subscribeCurrentChannel(ChannelListener channelListener) {
        getCurrentChannel(channelListener);
        URLServiceSubscription<ChannelListener> uRLServiceSubscription = new URLServiceSubscription<>(this, "ChannelChanged", null, null);
        uRLServiceSubscription.setHttpMethod(ServiceCommand.TYPE_GET);
        uRLServiceSubscription.addListener( channelListener);
        addSubscription(uRLServiceSubscription);
        return uRLServiceSubscription;
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void getProgramInfo(ProgramInfoListener programInfoListener) {
        Util.postError(programInfoListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.TVControl
    public ServiceSubscription<ProgramInfoListener> subscribeProgramInfo(ProgramInfoListener programInfoListener) {
        Util.postError(programInfoListener, ServiceCommandError.notSupported());
        return null;
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void getProgramList(ProgramListListener programListListener) {
        Util.postError(programListListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.TVControl
    public ServiceSubscription<ProgramListListener> subscribeProgramList(ProgramListListener programListListener) {
        Util.postError(programListListener, ServiceCommandError.notSupported());
        return null;
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void set3DEnabled(final boolean z, final ResponseListener<Object> responseListener) {
        get3DEnabled(new State3DModeListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass22 */

            public void onSuccess(Boolean bool) {
                if (z != bool.booleanValue()) {
                    NetcastTVService.this.sendVirtualKeyCode(NetcastVirtualKeycodes.VIDEO_3D.getCode(), responseListener);
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(responseListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.TVControl
    public void get3DEnabled(final State3DModeListener state3DModeListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand = new ServiceCommand<>(this, getUDAPRequestURL(UDAP_PATH_DATA, TARGET_IS_3D), null, new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass23 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                Util.postSuccess(state3DModeListener, Boolean.valueOf(((String) obj).toUpperCase(Locale.ENGLISH).contains("TRUE")));
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(state3DModeListener, serviceCommandError);
            }
        });
        serviceCommand.setHttpMethod(ServiceCommand.TYPE_GET);
        serviceCommand.send();
    }

    @Override // com.connectsdk.service.capability.TVControl
    public ServiceSubscription<State3DModeListener> subscribe3DEnabled(State3DModeListener state3DModeListener) {
        get3DEnabled(state3DModeListener);
        URLServiceSubscription<State3DModeListener> uRLServiceSubscription = new URLServiceSubscription<>(this, TARGET_3D_MODE, null, null);
        uRLServiceSubscription.setHttpMethod(ServiceCommand.TYPE_GET);
        uRLServiceSubscription.addListener( state3DModeListener);
        addSubscription(uRLServiceSubscription);
        return uRLServiceSubscription;
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public CapabilityPriorityLevel getVolumeControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public void volumeUp(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.VOLUME_UP.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public void volumeDown(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.VOLUME_DOWN.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public void setVolume(float f, ResponseListener<Object> responseListener) {
        if (getDLNAService() != null) {
            getDLNAService().setVolume(f, responseListener);
        } else {
            Util.postError(responseListener, ServiceCommandError.notSupported());
        }
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public void getVolume(final VolumeListener volumeListener) {
        getVolumeStatus(new VolumeStatusListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass24 */

            public void onSuccess(VolumeStatus volumeStatus) {
                Util.postSuccess(volumeListener, Float.valueOf(volumeStatus.volume));
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(volumeListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public void setMute(final boolean z, final ResponseListener<Object> responseListener) {
        getVolumeStatus(new VolumeStatusListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass25 */

            public void onSuccess(VolumeStatus volumeStatus) {
                if (z != volumeStatus.isMute) {
                    NetcastTVService.this.sendVirtualKeyCode(NetcastVirtualKeycodes.MUTE.getCode(), responseListener);
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(responseListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public void getMute(final MuteListener muteListener) {
        getVolumeStatus(new VolumeStatusListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass26 */

            public void onSuccess(VolumeStatus volumeStatus) {
                Util.postSuccess(muteListener, Boolean.valueOf(volumeStatus.isMute));
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(muteListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public ServiceSubscription<VolumeListener> subscribeVolume(VolumeListener volumeListener) {
        if (getDLNAService() != null) {
            return getDLNAService().subscribeVolume(volumeListener);
        }
        Util.postError(volumeListener, ServiceCommandError.notSupported());
        return null;
    }

    @Override // com.connectsdk.service.capability.VolumeControl
    public ServiceSubscription<MuteListener> subscribeMute(MuteListener muteListener) {
        if (getDLNAService() != null) {
            return getDLNAService().subscribeMute(muteListener);
        }
        Util.postError(muteListener, ServiceCommandError.notSupported());
        return null;
    }

    private void getVolumeStatus(final VolumeStatusListener volumeStatusListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand = new ServiceCommand<>(this, getUDAPRequestURL(UDAP_PATH_DATA, TARGET_VOLUME_INFO), null, new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass27 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                JSONObject parseVolumeXmlToJSON = NetcastTVService.this.parseVolumeXmlToJSON((String) obj);
                try {
                    Util.postSuccess(volumeStatusListener, new VolumeStatus(((Boolean) parseVolumeXmlToJSON.get(CastService.CAST_SERVICE_MUTE_SUBSCRIPTION_NAME)).booleanValue(), (float) ((Integer) parseVolumeXmlToJSON.get("level")).intValue()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(volumeStatusListener, serviceCommandError);
            }
        });
        serviceCommand.setHttpMethod(ServiceCommand.TYPE_GET);
        serviceCommand.send();
    }

    @Override // com.connectsdk.service.capability.ExternalInputControl
    public CapabilityPriorityLevel getExternalInputControlPriorityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.ExternalInputControl
    public void launchInputPicker(final AppLaunchListener appLaunchListener) {
        getApplication(HttpMessage.encode("Input List"), new AppInfoListener() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass28 */

            public void onSuccess(AppInfo appInfo) {
                NetcastTVService.this.launchApplication("Input List", appInfo.getId(), null, new AppLaunchListener() {
                    /* class com.connectsdk.service.NetcastTVService.AnonymousClass28.AnonymousClass1 */

                    public void onSuccess(LaunchSession launchSession) {
                        if (NetcastTVService.this.inputPickerSession == null) {
                            NetcastTVService.this.inputPickerSession = launchSession;
                        }
                        Util.postSuccess(appLaunchListener, launchSession);
                    }

                    @Override // com.connectsdk.service.capability.listeners.ErrorListener
                    public void onError(ServiceCommandError serviceCommandError) {
                        Util.postError(appLaunchListener, serviceCommandError);
                    }
                });
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appLaunchListener, serviceCommandError);
            }
        });
    }

    @Override // com.connectsdk.service.capability.ExternalInputControl
    public void closeInputPicker(LaunchSession launchSession, ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.EXIT.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.ExternalInputControl
    public void getExternalInputList(ExternalInputListListener externalInputListListener) {
        Util.postError(externalInputListListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.ExternalInputControl
    public void setExternalInput(ExternalInputInfo externalInputInfo, ResponseListener<Object> responseListener) {
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public CapabilityPriorityLevel getMediaPlayerCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void getMediaInfo(MediaInfoListener mediaInfoListener) {
        if (getDLNAService() != null) {
            getDLNAService().getMediaInfo(mediaInfoListener);
        } else if (mediaInfoListener != null) {
            Util.postError(mediaInfoListener, ServiceCommandError.notSupported());
        }
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public ServiceSubscription<MediaInfoListener> subscribeMediaInfo(MediaInfoListener mediaInfoListener) {
        if (getDLNAService() != null) {
            return getDLNAService().subscribeMediaInfo(mediaInfoListener);
        }
        if (mediaInfoListener == null) {
            return null;
        }
        Util.postError(mediaInfoListener, ServiceCommandError.notSupported());
        return null;
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void displayImage(String str, String str2, String str3, String str4, String str5, final LaunchListener launchListener) {
        if (getDLNAService() != null) {
            getDLNAService().displayImage(str, str2, str3, str4, str5, new LaunchListener() {
                /* class com.connectsdk.service.NetcastTVService.AnonymousClass29 */

                @Override // com.connectsdk.service.capability.listeners.ErrorListener
                public void onError(ServiceCommandError serviceCommandError) {
                    if (launchListener != null) {
                        Util.postError(launchListener, serviceCommandError);
                    }
                }

                public void onSuccess(MediaLaunchObject mediaLaunchObject) {
                    mediaLaunchObject.launchSession.setAppId(NetcastTVService.SMART_SHARE);
                    mediaLaunchObject.launchSession.setAppName(NetcastTVService.SMART_SHARE);
                    mediaLaunchObject.mediaControl = NetcastTVService.this.getMediaControl();
                    if (launchListener != null) {
                        Util.postSuccess(launchListener, mediaLaunchObject);
                    }
                }
            });
            return;
        }
        System.err.println("DLNA Service is not ready yet");
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void displayImage(MediaInfo mediaInfo, LaunchListener launchListener) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6 = null;
        if (mediaInfo != null) {
            String url = mediaInfo.getUrl();
            String mimeType = mediaInfo.getMimeType();
            String title = mediaInfo.getTitle();
            String description = mediaInfo.getDescription();
            if (mediaInfo.getImages() != null && mediaInfo.getImages().size() > 0) {
                str6 = mediaInfo.getImages().get(0).getUrl();
            }
            str = str6;
            str5 = url;
            str4 = mimeType;
            str3 = title;
            str2 = description;
        } else {
            str5 = null;
            str4 = null;
            str3 = null;
            str2 = null;
            str = null;
        }
        displayImage(str5, str4, str3, str2, str, launchListener);
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void playMedia(String str, String str2, String str3, String str4, String str5, boolean z, LaunchListener launchListener) {
        playMedia(new MediaInfo.Builder(str, str2).setTitle(str3).setDescription(str4).setIcon(str5).build(), z, launchListener);
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void playMedia(MediaInfo mediaInfo, boolean z, final LaunchListener launchListener) {
        if (getDLNAService() != null) {
            getDLNAService().playMedia(mediaInfo, z, new LaunchListener() {
                /* class com.connectsdk.service.NetcastTVService.AnonymousClass30 */

                @Override // com.connectsdk.service.capability.listeners.ErrorListener
                public void onError(ServiceCommandError serviceCommandError) {
                    if (launchListener != null) {
                        Util.postError(launchListener, serviceCommandError);
                    }
                }

                public void onSuccess(MediaLaunchObject mediaLaunchObject) {
                    mediaLaunchObject.launchSession.setAppId(NetcastTVService.SMART_SHARE);
                    mediaLaunchObject.launchSession.setAppName(NetcastTVService.SMART_SHARE);
                    mediaLaunchObject.mediaControl = NetcastTVService.this.getMediaControl();
                    if (launchListener != null) {
                        Util.postSuccess(launchListener, mediaLaunchObject);
                    }
                }
            });
            return;
        }
        System.err.println("DLNA Service is not ready yet");
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void closeMedia(LaunchSession launchSession, ResponseListener<Object> responseListener) {
        if (getDLNAService() == null) {
            Util.postError(responseListener, new ServiceCommandError(0, "Service is not connected", null));
        } else {
            getDLNAService().closeMedia(launchSession, responseListener);
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public MediaControl getMediaControl() {
        return DiscoveryManager.getInstance().getPairingLevel() == DiscoveryManager.PairingLevel.OFF ? getDLNAService() : this;
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public CapabilityPriorityLevel getMediaControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void play(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.PLAY.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void pause(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.PAUSE.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void stop(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.STOP.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void rewind(ResponseListener<Object> responseListener) {
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void fastForward(ResponseListener<Object> responseListener) {
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void previous(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.SKIP_BACKWARD.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void next(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.SKIP_FORWARD.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void seek(long j, ResponseListener<Object> responseListener) {
        if (getDLNAService() != null) {
            getDLNAService().seek(j, responseListener);
        } else if (responseListener != null) {
            Util.postError(responseListener, ServiceCommandError.notSupported());
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getDuration(DurationListener durationListener) {
        if (getDLNAService() != null) {
            getDLNAService().getDuration(durationListener);
        } else if (durationListener != null) {
            Util.postError(durationListener, ServiceCommandError.notSupported());
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getPosition(PositionListener positionListener) {
        if (getDLNAService() != null) {
            getDLNAService().getPosition(positionListener);
        } else if (positionListener != null) {
            Util.postError(positionListener, ServiceCommandError.notSupported());
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getPlayState(PlayStateListener playStateListener) {
        if (getDLNAService() != null) {
            getDLNAService().getPlayState(playStateListener);
        } else if (playStateListener != null) {
            Util.postError(playStateListener, ServiceCommandError.notSupported());
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public ServiceSubscription<PlayStateListener> subscribePlayState(PlayStateListener playStateListener) {
        if (getDLNAService() != null) {
            return getDLNAService().subscribePlayState(playStateListener);
        }
        Util.postError(playStateListener, ServiceCommandError.notSupported());
        return null;
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public CapabilityPriorityLevel getMouseControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    private void setMouseCursorVisible(boolean z, ResponseListener<Object> responseListener) {
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_EVENT);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "CursorVisible");
        hashMap.put("mode", "auto");
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody("event", hashMap), responseListener).send();
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public void connectMouse() {
        setMouseCursorVisible(true, new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass31 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                Log.d(Util.T, "Netcast TV's mouse has been connected");
                NetcastTVService.this.mMouseDistance = new PointF(0.0f, 0.0f);
                NetcastTVService.this.mMouseIsMoving = false;
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Log.w(Util.T, "Netcast TV's mouse connection has been failed");
            }
        });
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public void disconnectMouse() {
        setMouseCursorVisible(false, null);
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public void click() {
        ResponseListener r0 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass32 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Log.w(Util.T, "Netcast TV's mouse click has been failed");
            }
        };
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "HandleTouchClick");
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_COMMAND, hashMap), r0).send();
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public void move(double d, double d2) {
        PointF pointF = this.mMouseDistance;
        double d3 = (double) pointF.x;
        Double.isNaN(d3);
        pointF.x = (float) (d3 + d);
        PointF pointF2 = this.mMouseDistance;
        double d4 = (double) pointF2.y;
        Double.isNaN(d4);
        pointF2.y = (float) (d4 + d2);
        if (!this.mMouseIsMoving.booleanValue()) {
            this.mMouseIsMoving = true;
            moveMouse();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void moveMouse() {
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "HandleTouchMove");
        hashMap.put("x", String.valueOf((int) this.mMouseDistance.x));
        hashMap.put("y", String.valueOf((int) this.mMouseDistance.y));
        PointF pointF = this.mMouseDistance;
        pointF.y = 0.0f;
        pointF.x = 0.0f;
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_COMMAND, hashMap), new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass33 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                if (NetcastTVService.this.mMouseDistance.x > 0.0f || NetcastTVService.this.mMouseDistance.y > 0.0f) {
                    moveMouse();
                } else {
                    NetcastTVService.this.mMouseIsMoving = false;
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Log.w(Util.T, "Netcast TV's mouse move has failed");
                NetcastTVService.this.mMouseIsMoving = false;
            }
        }).send();
    }

//        private void moveMouse() {
//        String requestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
//
//        int x = (int)mMouseDistance.x;
//        int y = (int)mMouseDistance.y;
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "HandleTouchMove");
//        params.put("x", String.valueOf(x));
//        params.put("y", String.valueOf(y));
//
//        mMouseDistance.x = mMouseDistance.y = 0;
//
//        final NetcastTVService mouseService = this;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                if (mMouseDistance.x > 0 || mMouseDistance.y > 0)
//                    mouseService.moveMouse();
//                else
//                    mMouseIsMoving = false;
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Log.w(Util.T, "Netcast TV's mouse move has failed");
//
//                mMouseIsMoving = false;
//            }
//        };
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        request.send();
//    }

    @Override // com.connectsdk.service.capability.MouseControl
    public void move(PointF pointF) {
        move((double) pointF.x, (double) pointF.y);
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public void scroll(double d, double d2) {
        ResponseListener r6 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass34 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Log.w(Util.T, "Netcast TV's mouse scroll has been failed");
            }
        };
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "HandleTouchWheel");
        if (d2 > 0.0d) {
//            hashMap.put(AppMeasurementSdk.ConditionalUserProperty.VALUE, DiscoveryConstants.UNSECURE_PORT_TAG);
        } else {
//            hashMap.put(AppMeasurementSdk.ConditionalUserProperty.VALUE, "down");
        }
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody(UDAP_API_COMMAND, hashMap), r6).send();
    }

    @Override // com.connectsdk.service.capability.MouseControl
    public void scroll(PointF pointF) {
        scroll((double) pointF.x, (double) pointF.y);
    }

    @Override // com.connectsdk.service.capability.TextInputControl
    public CapabilityPriorityLevel getTextInputControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.TextInputControl
    public ServiceSubscription<TextInputStatusListener> subscribeTextInputStatus(TextInputStatusListener textInputStatusListener) {
        this.keyboardString = new StringBuilder();
        URLServiceSubscription<TextInputStatusListener> uRLServiceSubscription = new URLServiceSubscription<>(this, "KeyboardVisible", null, null);
        uRLServiceSubscription.addListener( textInputStatusListener);
        addSubscription(uRLServiceSubscription);
        return uRLServiceSubscription;
    }

    @Override // com.connectsdk.service.capability.TextInputControl
    public void sendText(String str) {
        String str2 = Util.T;
        Log.d(str2, "Add to Queue: " + str);
        this.keyboardString.append(str);
        handleKeyboardInput("Editing", this.keyboardString.toString());
    }

    @Override // com.connectsdk.service.capability.TextInputControl
    public void sendEnter() {
        ResponseListener r0 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass35 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Log.w(Util.T, "Netcast TV's enter key has been failed");
            }
        };
        handleKeyboardInput("EditEnd", this.keyboardString.toString());
        sendVirtualKeyCode(NetcastVirtualKeycodes.RED.getCode(), r0);
    }

    @Override // com.connectsdk.service.capability.TextInputControl
    public void sendDelete() {
        if (this.keyboardString.length() > 1) {
            StringBuilder sb = this.keyboardString;
            sb.deleteCharAt(sb.length() - 1);
        } else {
            this.keyboardString = new StringBuilder();
        }
        handleKeyboardInput("Editing", this.keyboardString.toString());
    }

    private void handleKeyboardInput(String str, String str2) {
        ResponseListener r0 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass37 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Log.w(Util.T, "Netcast TV's keyboard input has been failed");
            }
        };
        String uDAPRequestURL = getUDAPRequestURL(UDAP_PATH_EVENT);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "TextEdited");
        hashMap.put("state", str);
//        hashMap.put(AppMeasurementSdk.ConditionalUserProperty.VALUE, str2);
        new ServiceCommand(this, uDAPRequestURL, getUDAPMessageBody("event", hashMap), r0).send();
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public CapabilityPriorityLevel getKeyControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public void up(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.KEY_UP.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public void down(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.KEY_DOWN.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public void left(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.KEY_LEFT.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public void right(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.KEY_RIGHT.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public void ok(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.OK.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public void back(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.BACK.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public void home(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.HOME.getCode(), responseListener);
    }

    @Override // com.connectsdk.service.capability.PowerControl
    public CapabilityPriorityLevel getPowerControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.PowerControl
    public void powerOff(ResponseListener<Object> responseListener) {
        sendVirtualKeyCode(NetcastVirtualKeycodes.POWER.getCode(), new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass38 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Log.w(Util.T, "Netcast TV's power off has been failed");
            }
        });
    }

    @Override // com.connectsdk.service.capability.PowerControl
    public void powerOn(ResponseListener<Object> responseListener) {
        if (responseListener != null) {
            responseListener.onError(ServiceCommandError.notSupported());
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private JSONObject parseVolumeXmlToJSON(String str) {
        SAXParserFactory newInstance = SAXParserFactory.newInstance();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
            SAXParser newSAXParser = newInstance.newSAXParser();
            NetcastVolumeParser netcastVolumeParser = new NetcastVolumeParser();
            newSAXParser.parse(byteArrayInputStream, netcastVolumeParser);
            return netcastVolumeParser.getVolumeStatus();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e2) {
            e2.printStackTrace();
            return null;
        } catch (IOException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int parseAppNumberXmlToJSON(String str) {
        SAXParserFactory newInstance = SAXParserFactory.newInstance();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
            SAXParser newSAXParser = newInstance.newSAXParser();
            NetcastAppNumberParser netcastAppNumberParser = new NetcastAppNumberParser();
            newSAXParser.parse(byteArrayInputStream, netcastAppNumberParser);
            return netcastAppNumberParser.getApplicationNumber();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return 0;
        } catch (SAXException e2) {
            e2.printStackTrace();
            return 0;
        } catch (IOException e3) {
            e3.printStackTrace();
            return 0;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private JSONArray parseApplicationsXmlToJSON(String str) {
        SAXParserFactory newInstance = SAXParserFactory.newInstance();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
            SAXParser newSAXParser = newInstance.newSAXParser();
            NetcastApplicationsParser netcastApplicationsParser = new NetcastApplicationsParser();
            newSAXParser.parse(byteArrayInputStream, netcastApplicationsParser);
            return netcastApplicationsParser.getApplications();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e2) {
            e2.printStackTrace();
            return null;
        } catch (IOException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    public String getHttpMessageForHandleKeyInput(int i) {
        String valueOf = String.valueOf(i);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "HandleKeyInput");
//        hashMap.put(AppMeasurementSdk.ConditionalUserProperty.VALUE, valueOf);
        return getUDAPMessageBody(UDAP_API_COMMAND, hashMap);
    }

    /* renamed from: com.connectsdk.service.NetcastTVService$41  reason: invalid class name */
    static /* synthetic */ class AnonymousClass41 {
        static final /* synthetic */ int[] $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode = new int[0];

        /* JADX WARNING: Can't wrap try/catch for region: R(26:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|26) */
        /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0078 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0084 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
//        static {
//            int[] iArr = new int[KeyControl.KeyCode.length()];
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode = iArr;
//            iArr[KeyControl.KeyCode.NUM_0.ordinal()] = 1;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_1.ordinal()] = 2;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_2.ordinal()] = 3;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_3.ordinal()] = 4;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_4.ordinal()] = 5;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_5.ordinal()] = 6;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_6.ordinal()] = 7;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_7.ordinal()] = 8;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_8.ordinal()] = 9;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.NUM_9.ordinal()] = 10;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.DASH.ordinal()] = 11;
//            $SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[KeyControl.KeyCode.ENTER.ordinal()] = 12;
//        }
    }

    @Override // com.connectsdk.service.capability.KeyControl
    public void sendKeyCode(KeyCode keyCode, ResponseListener<Object> responseListener) {
        switch (AnonymousClass41.$SwitchMap$com$connectsdk$service$capability$KeyControl$KeyCode[keyCode.ordinal()]) {
            case 1:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_0.getCode(), responseListener);
                return;
            case 2:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_1.getCode(), responseListener);
                return;
            case 3:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_2.getCode(), responseListener);
                return;
            case 4:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_3.getCode(), responseListener);
                return;
            case 5:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_4.getCode(), responseListener);
                return;
            case 6:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_5.getCode(), responseListener);
                return;
            case 7:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_6.getCode(), responseListener);
                return;
            case 8:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_7.getCode(), responseListener);
                return;
            case 9:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_8.getCode(), responseListener);
                return;
            case 10:
                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_9.getCode(), responseListener);
                return;
            case 11:
                sendVirtualKeyCode(NetcastVirtualKeycodes.DASH.getCode(), responseListener);
                return;
            case 12:
                sendVirtualKeyCode(NetcastVirtualKeycodes.OK.getCode(), responseListener);
                return;
            default:
                Util.postError(responseListener, new ServiceCommandError(0, "The keycode is not available", null));
                return;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendVirtualKeyCode(final int i, final ResponseListener<Object> responseListener) {
        setMouseCursorVisible(false, new ResponseListener<Object>() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass39 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new ServiceCommand(NetcastTVService.this, NetcastTVService.this.getUDAPRequestURL(NetcastTVService.UDAP_PATH_COMMAND), NetcastTVService.this.getHttpMessageForHandleKeyInput(i), responseListener).send();
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(responseListener, serviceCommandError);
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getUDAPRequestURL(String str) {
        return getUDAPRequestURL(str, null);
    }

    private String getUDAPRequestURL(String str, String str2) {
        return getUDAPRequestURL(str, str2, null);
    }

    private String getUDAPRequestURL(String str, String str2, String str3) {
        return getUDAPRequestURL(str, str2, str3, null, null);
    }

    private String getUDAPRequestURL(String str, String str2, String str3, String str4, String str5) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(this.serviceDescription.getIpAddress());
        sb.append(":");
        sb.append(this.serviceDescription.getPort());
        sb.append(str);
        if (str2 != null) {
            sb.append("?target=");
            sb.append(str2);
            if (str3 != null) {
                sb.append("&type=");
                sb.append(str3);
            }
            if (str4 != null) {
                sb.append("&index=");
                sb.append(str4);
            }
            if (str5 != null) {
                sb.append("&number=");
                sb.append(str5);
            }
        }
        return sb.toString();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getUDAPMessageBody(String str, Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<envelope>");
        sb.append("<api type=\"");
        sb.append(str);
        sb.append("\">");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(createNode(entry.getKey(), entry.getValue()));
        }
        sb.append("</api>");
        sb.append("</envelope>");
        return sb.toString();
    }

    private String createNode(String str, String str2) {
        return "<" + str + ">" + str2 + "</" + str + ">";
    }

    public String decToHex(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        try {
            return decToHex(Long.parseLong(str.trim()));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decToHex(long j) {
        return String.format("%016x", Long.valueOf(j));
    }

    @Override // com.connectsdk.service.DeviceService, com.connectsdk.service.command.ServiceCommand.ServiceCommandProcessor
    public void sendCommand(final ServiceCommand<?> serviceCommand) {
        Util.runInBackground(new Runnable() {
            /* class com.connectsdk.service.NetcastTVService.AnonymousClass40 */

            public void run() {
                Object payload = serviceCommand.getPayload();
                try {
                    HttpConnection newInstance = HttpConnection.newInstance(URI.create(serviceCommand.getTarget()));
                    newInstance.setHeader(HttpMessage.USER_AGENT, HttpMessage.UDAP_USER_AGENT);
                    newInstance.setHeader(HttpMessage.CONTENT_TYPE_HEADER, HttpMessage.CONTENT_TYPE_TEXT_XML);
                    if (payload != null && serviceCommand.getHttpMethod().equalsIgnoreCase(ServiceCommand.TYPE_POST)) {
                        newInstance.setMethod(HttpConnection.Method.POST);
                        newInstance.setPayload(payload.toString());
                    }
                    newInstance.execute();
                    int responseCode = newInstance.getResponseCode();
                    Log.d("", "RESP " + responseCode);
                    if (responseCode == 200) {
                        Util.postSuccess(serviceCommand.getResponseListener(), newInstance.getResponseString());
                    } else {
                        Util.postError(serviceCommand.getResponseListener(), ServiceCommandError.getError(responseCode));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Util.postError(serviceCommand.getResponseListener(), new ServiceCommandError(0, e.getMessage(), null));
                }
            }
        });
    }

    private void addSubscription(URLServiceSubscription<?> uRLServiceSubscription) {
        this.subscriptions.add(uRLServiceSubscription);
        NetcastHttpServer netcastHttpServer = this.httpServer;
        if (netcastHttpServer != null) {
            netcastHttpServer.setSubscriptions(this.subscriptions);
        }
    }

    @Override // com.connectsdk.service.DeviceService, com.connectsdk.service.command.ServiceCommand.ServiceCommandProcessor
    public void unsubscribe(URLServiceSubscription<?> uRLServiceSubscription) {
        this.subscriptions.remove(uRLServiceSubscription);
        NetcastHttpServer netcastHttpServer = this.httpServer;
        if (netcastHttpServer != null) {
            netcastHttpServer.setSubscriptions(this.subscriptions);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.connectsdk.service.DeviceService
    public void updateCapabilities() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (DiscoveryManager.getInstance().getPairingLevel() == DiscoveryManager.PairingLevel.ON) {
            Collections.addAll(arrayList, TextInputControl.Capabilities);
            Collections.addAll(arrayList, MouseControl.Capabilities);
            Collections.addAll(arrayList, KeyControl.Capabilities);
            Collections.addAll(arrayList, MediaPlayer.Capabilities);
            arrayList.add(PowerControl.Off);
            arrayList.add(MediaControl.Play);
            arrayList.add(MediaControl.Pause);
            arrayList.add(MediaControl.Stop);
            arrayList.add(MediaControl.Duration);
            arrayList.add(MediaControl.Position);
            arrayList.add(MediaControl.Seek);
            arrayList.add(Launcher.Application);
            arrayList.add(Launcher.Application_Close);
            arrayList.add(Launcher.Application_List);
            arrayList.add(Launcher.Browser);
            arrayList.add(Launcher.Hulu);
            arrayList.add(Launcher.Netflix);
            arrayList.add(Launcher.Netflix_Params);
            arrayList.add(Launcher.YouTube);
            arrayList.add(Launcher.YouTube_Params);
            arrayList.add(Launcher.AppStore);
            arrayList.add(TVControl.Channel_Up);
            arrayList.add(TVControl.Channel_Down);
            arrayList.add(TVControl.Channel_Get);
            arrayList.add(TVControl.Channel_List);
            arrayList.add(TVControl.Channel_Subscribe);
            arrayList.add(TVControl.Get_3D);
            arrayList.add(TVControl.Set_3D);
            arrayList.add(TVControl.Subscribe_3D);
            arrayList.add(ExternalInputControl.Picker_Launch);
            arrayList.add(ExternalInputControl.Picker_Close);
            arrayList.add(VolumeControl.Volume_Get);
            arrayList.add(VolumeControl.Volume_Up_Down);
            arrayList.add(VolumeControl.Mute_Get);
            arrayList.add(VolumeControl.Mute_Set);
            if (this.serviceDescription.getModelNumber().equals("4.0")) {
                arrayList.add(Launcher.AppStore_Params);
            }
        } else {
            Collections.addAll(arrayList, MediaPlayer.Capabilities);
            arrayList.add(MediaControl.Play);
            arrayList.add(MediaControl.Pause);
            arrayList.add(MediaControl.Stop);
            arrayList.add(Launcher.YouTube);
            arrayList.add(Launcher.YouTube_Params);
        }
        arrayList.add(MediaPlayer.Subtitle_SRT);
        setCapabilities(arrayList);
    }

    public DLNAService getDLNAService() {
        ConnectableDevice connectableDevice;
        if (this.dlnaService == null && (connectableDevice = DiscoveryManager.getInstance().getAllDevices().get(this.serviceDescription.getIpAddress())) != null) {
            DLNAService dLNAService = null;
            Iterator<DeviceService> it = connectableDevice.getServices().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                DeviceService next = it.next();
                if (DLNAService.class.isAssignableFrom(next.getClass())) {
                    dLNAService = (DLNAService) next;
                    break;
                }
            }
            this.dlnaService = dLNAService;
        }
        return this.dlnaService;
    }

    public DIALService getDIALService() {
        ConnectableDevice connectableDevice;
        if (this.dialService == null && (connectableDevice = DiscoveryManager.getInstance().getAllDevices().get(this.serviceDescription.getIpAddress())) != null) {
            DIALService dIALService = null;
            Iterator<DeviceService> it = connectableDevice.getServices().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                DeviceService next = it.next();
                if (DIALService.class.isAssignableFrom(next.getClass())) {
                    dIALService = (DIALService) next;
                    break;
                }
            }
            this.dialService = dIALService;
        }
        return this.dialService;
    }
}



//
//import android.graphics.PointF;
//import android.util.Log;
//
//import com.connectsdk.core.AppInfo;
//import com.connectsdk.core.ChannelInfo;
//import com.connectsdk.core.ExternalInputInfo;
//import com.connectsdk.core.ImageInfo;
//import com.connectsdk.core.MediaInfo;
//import com.connectsdk.core.Util;
//import com.connectsdk.device.ConnectableDevice;
//import com.connectsdk.discovery.DiscoveryFilter;
//import com.connectsdk.discovery.DiscoveryManager;
//import com.connectsdk.discovery.DiscoveryManager.PairingLevel;
//import com.connectsdk.etc.helper.DeviceServiceReachability;
//import com.connectsdk.etc.helper.HttpConnection;
//import com.connectsdk.etc.helper.HttpMessage;
//import com.connectsdk.service.capability.CapabilityMethods;
//import com.connectsdk.service.capability.ExternalInputControl;
//import com.connectsdk.service.capability.KeyControl;
//import com.connectsdk.service.capability.Launcher;
//import com.connectsdk.service.capability.MediaControl;
//import com.connectsdk.service.capability.MediaPlayer;
//import com.connectsdk.service.capability.MouseControl;
//import com.connectsdk.service.capability.PowerControl;
//import com.connectsdk.service.capability.TVControl;
//import com.connectsdk.service.capability.TextInputControl;
//import com.connectsdk.service.capability.VolumeControl;
//import com.connectsdk.service.capability.listeners.ResponseListener;
//import com.connectsdk.service.command.NotSupportedServiceSubscription;
//import com.connectsdk.service.command.ServiceCommand;
//import com.connectsdk.service.command.ServiceCommandError;
//import com.connectsdk.service.command.ServiceSubscription;
//import com.connectsdk.service.command.URLServiceSubscription;
//import com.connectsdk.service.config.NetcastTVServiceConfig;
//import com.connectsdk.service.config.ServiceConfig;
//import com.connectsdk.service.config.ServiceDescription;
//import com.connectsdk.service.netcast.NetcastAppNumberParser;
//import com.connectsdk.service.netcast.NetcastApplicationsParser;
//import com.connectsdk.service.netcast.NetcastChannelParser;
//import com.connectsdk.service.netcast.NetcastHttpServer;
//import com.connectsdk.service.netcast.NetcastVirtualKeycodes;
//import com.connectsdk.service.netcast.NetcastVolumeParser;
//import com.connectsdk.service.sessions.LaunchSession;
//import com.connectsdk.service.sessions.LaunchSession.LaunchSessionType;
//import com.connectsdk.service.webos.WebOSTVMouseSocketConnection;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.xml.sax.SAXException;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//
//public class NetcastTVService extends DeviceService implements Launcher, MediaControl, MediaPlayer, TVControl, VolumeControl, ExternalInputControl, MouseControl, TextInputControl, PowerControl, KeyControl {
//
//    public static final String ID = "Netcast TV";
//
//    public static final String UDAP_PATH_PAIRING = "/udap/api/pairing";
//    public static final String UDAP_PATH_DATA = "/udap/api/data";
//    public static final String UDAP_PATH_COMMAND = "/udap/api/command";
//    public static final String UDAP_PATH_EVENT = "/udap/api/event";
//
//    public static final String UDAP_PATH_APPTOAPP_DATA = "/udap/api/apptoapp/data/";
//    public static final String UDAP_PATH_APPTOAPP_COMMAND = "/udap/api/apptoapp/command/";
//    public static final String ROAP_PATH_APP_STORE = "/roap/api/command/";
//
//    public static final String UDAP_API_PAIRING = "pairing";
//    public static final String UDAP_API_COMMAND = "command";
//    public static final String UDAP_API_EVENT = "event";
//
//    public final static String TARGET_CHANNEL_LIST = "channel_list";
//    public final static String TARGET_CURRENT_CHANNEL = "cur_channel";
//    public final static String TARGET_VOLUME_INFO = "volume_info";
//    public final static String TARGET_APPLIST_GET = "applist_get";
//    public final static String TARGET_APPNUM_GET = "appnum_get";
//    public final static String TARGET_3D_MODE = "3DMode";
//    public final static String TARGET_IS_3D = "is_3D";
//    public static final String SMART_SHARE = "SmartShare™";
//
//    enum State {
//        NONE,
//        INITIAL,
//        CONNECTING,
//        PAIRING,
//        PAIRED,
//        DISCONNECTING
//    }
//
//    NetcastHttpServer httpServer;
//
//    DLNAService dlnaService;
//    DIALService dialService;
//
//    LaunchSession inputPickerSession;
//
//    List<AppInfo> applications;
//    List<URLServiceSubscription<?>> subscriptions;
//    StringBuilder keyboardString;
//
//    State state = State.INITIAL;
//
//    PointF mMouseDistance;
//    Boolean mMouseIsMoving;
//
//    public NetcastTVService(ServiceDescription serviceDescription, ServiceConfig serviceConfig) {
//        super(serviceDescription, serviceConfig);
//
//        pairingType = PairingType.PIN_CODE;
//
//        if (serviceDescription.getPort() != 8080)
//            serviceDescription.setPort(8080);
//
//        applications = new ArrayList<AppInfo>();
//        subscriptions = new ArrayList<URLServiceSubscription<?>>();
//
//        keyboardString = new StringBuilder();
//
//        state = State.INITIAL;
//
//        inputPickerSession = null;
//    }
//
//    public static DiscoveryFilter discoveryFilter() {
//        return new DiscoveryFilter(ID, "urn:schemas-upnp-org:device:MediaRenderer:1");
//    }
//
//    @Override
//    public CapabilityPriorityLevel getPriorityLevel(Class<? extends CapabilityMethods> clazz) {
//        if (clazz.equals(MediaPlayer.class)) {
//            return getMediaPlayerCapabilityLevel();
//        }
//        else if (clazz.equals(MediaControl.class)) {
//            return getMediaControlCapabilityLevel();
//        }
//        else if (clazz.equals(Launcher.class)) {
//            return getLauncherCapabilityLevel();
//        }
//        else if (clazz.equals(TVControl.class)) {
//            return getTVControlCapabilityLevel();
//        }
//        else if (clazz.equals(VolumeControl.class)) {
//            return getVolumeControlCapabilityLevel();
//        }
//        else if (clazz.equals(ExternalInputControl.class)) {
//            return getExternalInputControlPriorityLevel();
//        }
//        else if (clazz.equals(MouseControl.class)) {
//            return getMouseControlCapabilityLevel();
//        }
//        else if (clazz.equals(TextInputControl.class)) {
//            return getTextInputControlCapabilityLevel();
//        }
//        else if (clazz.equals(PowerControl.class)) {
//            return getPowerControlCapabilityLevel();
//        }
//        else if (clazz.equals(KeyControl.class)) {
//            return getKeyControlCapabilityLevel();
//        }
//        return CapabilityPriorityLevel.NOT_SUPPORTED;
//    }
//
//    @Override
//    public void setServiceDescription(ServiceDescription serviceDescription) {
//        super.setServiceDescription(serviceDescription);
//
//        if (serviceDescription.getPort() != 8080)
//            serviceDescription.setPort(8080);
//    }
//
//    @Override
//    public void connect() {
//        if (state != State.INITIAL) {
//            Log.w(Util.T, "already connecting; not trying to connect again: " + state);
//            return; // don't try to connect again while connected
//        }
//
//        if (!(serviceConfig instanceof NetcastTVServiceConfig)) {
//            ServiceConfig.ServiceConfigListener serviceConfigListener = serviceConfig.getListener();
//            serviceConfig = new NetcastTVServiceConfig(serviceConfig.getServiceUUID());
//            serviceConfig.setListener(serviceConfigListener);
//        }
//
//        if (DiscoveryManager.getInstance().getPairingLevel() == PairingLevel.ON) {
//            if (((NetcastTVServiceConfig) serviceConfig).getPairingKey() != null
//                    && ((NetcastTVServiceConfig)serviceConfig).getPairingKey().length() != 0) {
//
//                sendPairingKey(((NetcastTVServiceConfig) serviceConfig).getPairingKey());
//            }
//            else {
//                showPairingKeyOnTV();
//            }
//
//            Util.runInBackground(new Runnable() {
//
//                @Override
//                public void run() {
//                    httpServer = new NetcastHttpServer(NetcastTVService.this, getServiceDescription().getPort(), mTextChangedListener);
//                    httpServer.setSubscriptions(subscriptions);
//                    httpServer.start();
//                }
//            });
//        } else {
//            hConnectSuccess();
//        }
//    }
//
//    @Override
//    public void disconnect() {
//        endPairing(null);
//
//        connected = false;
//
//        if (mServiceReachability != null)
//            mServiceReachability.stop();
//
//        Util.runOnUI(new Runnable() {
//
//            @Override
//            public void run() {
//                if (listener != null)
//                    listener.onDisconnect(NetcastTVService.this, null);
//            }
//        });
//
//        if (httpServer != null) {
//            httpServer.stop();
//            httpServer = null;
//        }
//
//        state = State.INITIAL;
//    }
//
//    @Override
//    public boolean isConnectable() {
//        return true;
//    }
//
//    @Override
//    public boolean isConnected() {
//        return connected;
//    }
//
//    private void hConnectSuccess() {
//        //  TODO:  Fix this for Netcast.  Right now it is using the InetAddress reachable function.  Need to use an HTTP Method.
////        mServiceReachability = DeviceServiceReachability.getReachability(serviceDescription.getIpAddress(), this);
////        mServiceReachability.start();
//
//        connected = true;
//
//        // Pairing was successful, so report connected and ready
//        reportConnected(true);
//    }
//
//    @Override
//    public void onLoseReachability(DeviceServiceReachability reachability) {
//        if (connected) {
//            disconnect();
//        } else {
//            if (mServiceReachability != null)
//                mServiceReachability.stop();
//        }
//    }
//
//    public void hostByeBye () {
//        disconnect();
//    }
//
//    //============= Auth ==============================
//    public void showPairingKeyOnTV() {
//        state = State.CONNECTING;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                if (listener != null)
//                    listener.onPairingRequired(NetcastTVService.this, pairingType, null);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                state = State.INITIAL;
//
//                if (listener != null)
//                    listener.onConnectionFailure(NetcastTVService.this, error);
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_PAIRING);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "showKey");
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_PAIRING, params);
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        command.send();
//    }
//
//    @Override
//    public void cancelPairing() {
//        removePairingKeyOnTV();
//        state = State.INITIAL;
//    }
//
//    public void removePairingKeyOnTV() {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_PAIRING);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "CancelAuthKeyReq");
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_PAIRING, params);
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        command.send();
//    }
//
//    @Override
//    public void sendPairingKey(final String pairingKey) {
//        state = State.PAIRING;
//
//        if (!(serviceConfig instanceof NetcastTVServiceConfig)) {
//            serviceConfig = new NetcastTVServiceConfig(serviceConfig.getServiceUUID());
//        }
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                state = State.PAIRED;
//
//                ((NetcastTVServiceConfig)serviceConfig).setPairingKey(pairingKey);
//
//                hConnectSuccess();
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                state = State.INITIAL;
//
//                if (listener != null)
//                    listener.onConnectionFailure(NetcastTVService.this, error);
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_PAIRING);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "hello");
//        params.put("value", pairingKey);
//        params.put("port", String.valueOf(serviceDescription.getPort()));
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_PAIRING, params);
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        command.send();
//    }
//
//    private void endPairing(ResponseListener<Object> listener) {
//        String requestURL = getUDAPRequestURL(UDAP_PATH_PAIRING);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "byebye");
//        params.put("port", String.valueOf(serviceDescription.getPort()));
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_PAIRING, params);
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, listener);
//        command.send();
//    }
//
//
//    /******************
//    LAUNCHER
//     *****************/
//    public Launcher getLauncher() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getLauncherCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    class NetcastTVLaunchSessionR extends LaunchSession {
//        String appName;
//        NetcastTVService service;
//
//        NetcastTVLaunchSessionR (NetcastTVService service, String auid, String appName) {
//            this.service = service;
//            appId = auid;
//        }
//
//        NetcastTVLaunchSessionR (NetcastTVService service, JSONObject obj) throws JSONException {
//            this.service = service;
//            fromJSONObject(obj);
//        }
//
//        public void close(ResponseListener<Object> responseListener) {
//        }
//
//        @Override
//        public JSONObject toJSONObject() throws JSONException {
//            JSONObject obj = super.toJSONObject();
//            obj.put("type", "netcasttv");
//            obj.put("appName", appName);
//            return obj;
//        }
//
//        @Override
//        public void fromJSONObject(JSONObject obj) throws JSONException {
//            super.fromJSONObject(obj);
//            appName = obj.optString("appName");
//        }
//    }
//
//    public void getApplication(final String appName, final AppInfoListener listener) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                final String strObj = (String) response;
//
//                AppInfo appId = new AppInfo() {{
//                    setId(decToHex(strObj));
//                }};
//
//                Util.postSuccess(listener, appId);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                if (listener != null)
//                    Util.postError(listener, error);
//            }
//        };
//
//        String uri = UDAP_PATH_APPTOAPP_DATA + appName;
//        String requestURL = getUDAPRequestURL(uri);
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
//        command.setHttpMethod(ServiceCommand.TYPE_GET);
//        command.send();
//    }
//
//    @Override
//    public void launchApp(final String appId, final AppLaunchListener listener) {
//        getAppInfoForId(appId, new AppInfoListener() {
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//
//            @Override
//            public void onSuccess(AppInfo appInfo) {
//                launchAppWithInfo(appInfo, listener);
//            }
//        });
//    }
//
//    private void getAppInfoForId(final String appId, final AppInfoListener listener) {
//        getAppList(new AppListListener() {
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//
//            @Override
//            public void onSuccess(List<AppInfo> object) {
//                for (AppInfo info : object) {
//                    if (info.getName().equalsIgnoreCase(appId)) {
//                        Util.postSuccess(listener, info);
//                        return;
//                    }
//                }
//                Util.postError(listener, new ServiceCommandError(0, "Unable to find the App with id", null));
//            }
//        });
//    }
//
//    private void launchApplication(final String appName, final String auid, final String contentId, final Launcher.AppLaunchListener listener) {
//        JSONObject jsonObj = new JSONObject();
//
//        try {
//            jsonObj.put("id", auid);
//            jsonObj.put("title", appName);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                LaunchSession launchSession = LaunchSession.launchSessionForAppId(auid);
//                launchSession.setAppName(appName);
//                launchSession.setService(NetcastTVService.this);
//                launchSession.setSessionType(LaunchSessionType.App);
//
//                Util.postSuccess(listener, launchSession);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_APPTOAPP_COMMAND);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "AppExecute");
//        params.put("auid", auid);
//        if (appName != null) {
//            params.put("appname", appName);
//        }
//        if (contentId != null) {
//            params.put("contentid", contentId);
//        }
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        request.send();
//    }
//
//    @Override
//    public void launchAppWithInfo(AppInfo appInfo, Launcher.AppLaunchListener listener) {
//        launchAppWithInfo(appInfo, null, listener);
//    }
//
//    @Override
//    public void launchAppWithInfo(AppInfo appInfo, Object params, Launcher.AppLaunchListener listener) {
//        String appName = HttpMessage.encode(appInfo.getName());
//        String appId = appInfo.getId();
//        String contentId = null;
//        JSONObject mParams = null;
//        if (params instanceof JSONObject)
//            mParams = (JSONObject) params;
//
//        if (mParams != null) {
//            try {
//                contentId = (String) mParams.get("contentId");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        launchApplication(appName, appId, contentId, listener);
//    }
//
//    @Override
//    public void launchBrowser(String url, final Launcher.AppLaunchListener listener) {
//        if (!(url == null || url.length() == 0))
//            Log.w(Util.T, "Netcast TV does not support deeplink for Browser");
//
//        final String appName = "Internet";
//
//        getApplication(appName, new AppInfoListener() {
//
//            @Override
//            public void onSuccess(AppInfo appInfo) {
//                String contentId = null;
//                launchApplication(appName, appInfo.getId(), contentId, listener);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//    }
//
//    @Override
//    public void launchYouTube(String contentId, Launcher.AppLaunchListener listener) {
//        launchYouTube(contentId, (float)0.0, listener);
//    }
//
//    @Override
//    public void launchYouTube(final String contentId, float startTime, final AppLaunchListener listener) {
//        if (getDIALService() != null) {
//            getDIALService().getLauncher().launchYouTube(contentId, startTime, listener);
//            return;
//        }
//
//        if (startTime <= 0.0) {
//            getApplication("YouTube", new AppInfoListener() {
//
//                @Override
//                public void onSuccess(AppInfo appInfo) {
//                    launchApplication(appInfo.getName(), appInfo.getId(), contentId, listener);
//                }
//
//                @Override
//                public void onError(ServiceCommandError error) {
//                    Util.postError(listener, error);
//                }
//            });
//        }
//        else {
//            Util.postError(listener, new ServiceCommandError(0, "Cannot reach DIAL service for launching with provided start time", null));
//        }
//    }
//
//    @Override
//    public void launchHulu(final String contentId, final Launcher.AppLaunchListener listener) {
//        final String appName = "Hulu";
//
//        getApplication(appName, new AppInfoListener() {
//
//            @Override
//            public void onSuccess(AppInfo appInfo) {
//                launchApplication(appName, appInfo.getId(), contentId, listener);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//    }
//
//    @Override
//    public void launchNetflix(final String contentId, final Launcher.AppLaunchListener listener) {
//        if (!serviceDescription.getModelNumber().equals("4.0")) {
//            launchApp("Netflix", listener);
//            return;
//        }
//
//        final String appName = "Netflix";
//
//        getApplication(appName, new AppInfoListener() {
//
//            @Override
//            public void onSuccess(final AppInfo appInfo) {
//                JSONObject jsonObj = new JSONObject();
//
//                try {
//                    jsonObj.put("id", appInfo.getId());
//                    jsonObj.put("name", appName);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//                    @Override
//                    public void onSuccess(Object response) {
//                        LaunchSession launchSession = LaunchSession.launchSessionForAppId(appInfo.getId());
//                        launchSession.setAppName(appName);
//                        launchSession.setService(NetcastTVService.this);
//                        launchSession.setSessionType(LaunchSessionType.App);
//
//                        Util.postSuccess(listener, launchSession);
//                    }
//
//                    @Override
//                    public void onError(ServiceCommandError error) {
//                        Util.postError(listener, error);
//                    }
//                };
//
//                String requestURL = getUDAPRequestURL(UDAP_PATH_APPTOAPP_COMMAND);
//
//                Map <String,String> params = new HashMap<String,String>();
//                params.put("name", "SearchCMDPlaySDPContent");
//                params.put("content_type", "1");
//                params.put("conts_exec_type", "20");
//                params.put("conts_plex_type_flag", "N");
//                params.put("conts_search_id", "2023237");
//                params.put("conts_age", "18");
//                params.put("exec_id", "netflix");
//                params.put("item_id", "-Q m=http%3A%2F%2Fapi.netflix.com%2Fcatalog%2Ftitles%2Fmovies%2F" + contentId + "&amp;source_type=4&amp;trackId=6054700&amp;trackUrl=https%3A%2F%2Fapi.netflix.com%2FAPI_APP_ID_6261%3F%23Search%3F");
//                params.put("app_type", "");
//
//                String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//                ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(NetcastTVService.this, requestURL, httpMessage, responseListener);
//                request.send();
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//    }
//
//    @Override
//    public void launchAppStore(final String appId, final AppLaunchListener listener) {
//        if (!serviceDescription.getModelNumber().equals("4.0")) {
//            launchApp("LG Smart World", listener);  // TODO: this will not work in Korea, use Korean name instead
//            return;
//        }
//
//        String targetPath = getUDAPRequestURL(ROAP_PATH_APP_STORE);
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("name", "SearchCMDPlaySDPContent");
//        params.put("content_type", "4");
//        params.put("conts_exec_type", "");
//        params.put("conts_plex_type_flag", "");
//        params.put("conts_search_id", "");
//        params.put("conts_age", "12");
//        params.put("exec_id", "");
//        params.put("item_id", HttpMessage.encode(appId));
//        params.put("app_type", "S");
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                LaunchSession launchSession = LaunchSession.launchSessionForAppId(appId);
//                launchSession.setAppName("LG Smart World"); // TODO: this will not work in Korea, use Korean name instead
//                launchSession.setService(NetcastTVService.this);
//                launchSession.setSessionType(LaunchSessionType.App);
//
//                Util.postSuccess(listener, launchSession);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, targetPath, httpMessage, responseListener);
//        command.send();
//    }
//
//    @Override
//    public void closeApp(LaunchSession launchSession, ResponseListener<Object> listener) {
//        String requestURL = getUDAPRequestURL(UDAP_PATH_APPTOAPP_COMMAND);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "AppTerminate");
//        params.put("auid", launchSession.getAppId());
//        if (launchSession.getAppName() != null)
//            params.put("appname", HttpMessage.encode(launchSession.getAppName()));
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(launchSession.getService(), requestURL, httpMessage, listener);
//        command.send();
//    }
//
//    private void getTotalNumberOfApplications(int type, final AppCountListener listener) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                String strObj = (String) response;
//
//                int applicationNumber = parseAppNumberXmlToJSON(strObj);
//
//                Util.postSuccess(listener, applicationNumber);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_DATA, TARGET_APPNUM_GET, String.valueOf(type));
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
//        command.setHttpMethod(ServiceCommand.TYPE_GET);
//        command.send();
//    }
//
//    private void getApplications(int type, int number, final AppListListener listener) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                String strObj = (String) response;
//
//                JSONArray applicationArray = parseApplicationsXmlToJSON(strObj);
//                List<AppInfo> appList = new ArrayList<AppInfo>();
//
//                for (int i = 0; i < applicationArray.length(); i++)
//                {
//                    try {
//                        final JSONObject appJSON = applicationArray.getJSONObject(i);
//
//                        AppInfo appInfo = new AppInfo() {{
//                            setId(appJSON.getString("id"));
//                            setName(appJSON.getString("title"));
//                        }};
//
//                        appList.add(appInfo);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                Util.postSuccess(listener, appList);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_DATA, TARGET_APPLIST_GET, String.valueOf(type), "0", String.valueOf(number));
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
//        command.setHttpMethod(ServiceCommand.TYPE_GET);
//        command.send();
//    }
//
//    @Override
//    public void getAppList(final AppListListener listener) {
//        applications.clear();
//        getTotalNumberOfApplications(2, new AppCountListener() {
//
//            @Override
//            public void onSuccess(final Integer count) {
//                getApplications(2, count, new AppListListener() {
//
//                    @Override
//                    public void onSuccess(List<AppInfo> apps) {
//                        applications.addAll(apps);
//
//                        getTotalNumberOfApplications(3, new AppCountListener() {
//
//                            @Override
//                            public void onSuccess(final Integer count) {
//                                getApplications(3, count, new AppListListener() {
//
//                                    @Override
//                                    public void onSuccess(List<AppInfo> apps) {
//                                        applications.addAll(apps);
//
//                                        Util.postSuccess(listener, applications);
//                                    }
//
//                                    @Override
//                                    public void onError(ServiceCommandError error) {
//                                        Util.postError(listener, error);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onError(ServiceCommandError error) {
//                                Util.postError(listener, error);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(ServiceCommandError error) {
//                        Util.postError(listener, error);
//                    }
//                });
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//    }
//
//    @Override
//    public void getRunningApp(AppInfoListener listener) {
//        // Do nothing - Not Supported
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public ServiceSubscription<AppInfoListener> subscribeRunningApp(AppInfoListener listener) {
//        // Do nothing - Not Supported
//        Util.postError(listener, ServiceCommandError.notSupported());
//
//        return new NotSupportedServiceSubscription<AppInfoListener>();
//    }
//
//    @Override
//    public void getAppState(final LaunchSession launchSession, final AppStateListener listener) {
//        String requestURL = String.format(Locale.US, "%s%s",
//                getUDAPRequestURL(UDAP_PATH_APPTOAPP_DATA),
//                String.format(Locale.US, "/%s/status", launchSession.getAppId()));
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//
//            @Override
//            public void onSuccess(Object object) {
//                String response = (String) object;
//                AppState appState;
//                if (response.equalsIgnoreCase("NONE"))
//                    appState = new AppState(false, false);
//                else if (response.equalsIgnoreCase("LOAD"))
//                    appState = new AppState(false, true);
//                else if (response.equalsIgnoreCase("RUN_NF"))
//                    appState = new AppState(true, false);
//                else if (response.equalsIgnoreCase("TERM"))
//                    appState = new AppState(false, true);
//                else
//                    appState = new AppState(false, false);
//
//                Util.postSuccess(listener, appState);
//            }
//        };
//
//        ServiceCommand<ResponseListener<Object>> command = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
//        command.setHttpMethod(ServiceCommand.TYPE_GET);
//        command.send();
//    }
//
//    @Override
//    public ServiceSubscription<AppStateListener> subscribeAppState(LaunchSession launchSession, AppStateListener listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//
//        return null;
//    }
//
//
//    /******************
//    TV CONTROL
//     *****************/
//    @Override
//    public TVControl getTVControl() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getTVControlCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public void getChannelList(final ChannelListListener listener) {
//        String requestURL = getUDAPRequestURL(UDAP_PATH_DATA, TARGET_CHANNEL_LIST);
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                String strObj = (String)response;
//
//                try {
//                    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//                    InputStream stream = new ByteArrayInputStream(strObj.getBytes("UTF-8"));
//                    SAXParser saxParser = saxParserFactory.newSAXParser();
//
//                    NetcastChannelParser parser = new NetcastChannelParser();
//                    saxParser.parse(stream, parser);
//
//                    JSONArray channelArray = parser.getJSONChannelArray();
//                    ArrayList<ChannelInfo> channelList = new ArrayList<ChannelInfo>();
//
//                    for (int i = 0; i < channelArray.length(); i++) {
//                        JSONObject rawData;
//                        try {
//                            rawData = (JSONObject) channelArray.get(i);
//
//                            ChannelInfo channel = NetcastChannelParser.parseRawChannelData(rawData);
//                            channelList.add(channel);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    Util.postSuccess(listener, channelList);
//                } catch (ParserConfigurationException e) {
//                    e.printStackTrace();
//                } catch (SAXException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
//        request.setHttpMethod(ServiceCommand.TYPE_GET);
//        request.send();
//    }
//
//    @Override
//    public void channelUp(ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.CHANNEL_UP.getCode(), listener);
//    }
//
//    @Override
//    public void channelDown(ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.CHANNEL_DOWN.getCode(), listener);
//    }
//
//    @Override
//    public void setChannel(final ChannelInfo channelInfo, final ResponseListener<Object> listener) {
//        getChannelList(new ChannelListListener() {
//
//            @Override
//            public void onSuccess(List<ChannelInfo> channelList) {
//                String requestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
//
//                Map<String, String> params = new HashMap<String, String>();
//
//                for (int i = 0; i < channelList.size(); i++) {
//                    ChannelInfo ch = channelList.get(i);
//                    JSONObject rawData = ch.getRawData();
//
//                    try {
//                        String major = channelInfo.getNumber().split("-")[0];
//                        String minor = channelInfo.getNumber().split("-")[1];
//
//                        int majorNumber = ch.getMajorNumber();
//                        int minorNumber = ch.getMinorNumber();
//
//                        String sourceIndex = (String) rawData.get("sourceIndex");
//                        int physicalNum = (Integer) rawData.get("physicalNumber");
//
//                        if (Integer.valueOf(major) == majorNumber
//                                && Integer.valueOf(minor) == minorNumber) {
//                            params.put("name", "HandleChannelChange");
//                            params.put("major", major);
//                            params.put("minor", minor);
//                            params.put("sourceIndex", sourceIndex);
//                            params.put("physicalNum", String.valueOf(physicalNum));
//
//                            break;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//                ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(NetcastTVService.this, requestURL, httpMessage, listener);
//                request.send();
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//    }
//
//    @Override
//    public void getCurrentChannel(final ChannelListener listener) {
//        String requestURL = getUDAPRequestURL(UDAP_PATH_DATA, TARGET_CURRENT_CHANNEL);
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                String strObj = (String)response;
//
//                try {
//                    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//                    InputStream stream = new ByteArrayInputStream(strObj.getBytes("UTF-8"));
//                    SAXParser saxParser = saxParserFactory.newSAXParser();
//
//                    NetcastChannelParser parser = new NetcastChannelParser();
//                    saxParser.parse(stream, parser);
//
//                    JSONArray channelArray = parser.getJSONChannelArray();
//
//                    if (channelArray.length() > 0) {
//                        JSONObject rawData = (JSONObject) channelArray.get(0);
//
//                        ChannelInfo channel = NetcastChannelParser.parseRawChannelData(rawData);
//
//                        Util.postSuccess(listener, channel);
//                    }
//                } catch (ParserConfigurationException e) {
//                    e.printStackTrace();
//                } catch (SAXException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
//        request.send();
//    }
//
//    @Override
//    public ServiceSubscription<ChannelListener> subscribeCurrentChannel(final ChannelListener listener) {
//        getCurrentChannel(listener);    // This is for the initial Current TV Channel Info.
//
//        URLServiceSubscription<ChannelListener> request = new URLServiceSubscription<ChannelListener>(this, "ChannelChanged", null, null);
//        request.setHttpMethod(ServiceCommand.TYPE_GET);
//        request.addListener(listener);
//        addSubscription(request);
//
//        return request;
//    }
//
//    @Override
//    public void getProgramInfo(ProgramInfoListener listener) {
//        // Do nothing - Not Supported
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public ServiceSubscription<ProgramInfoListener> subscribeProgramInfo(ProgramInfoListener listener) {
//        // Do nothing - Not Supported
//        Util.postError(listener, ServiceCommandError.notSupported());
//
//        return null;
//    }
//
//    @Override
//    public void getProgramList(ProgramListListener listener) {
//        // Do nothing - Not Supported
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public ServiceSubscription<ProgramListListener> subscribeProgramList(ProgramListListener listener) {
//        // Do nothing - Not Supported
//        Util.postError(listener, ServiceCommandError.notSupported());
//
//        return null;
//    }
//
//    @Override
//    public void set3DEnabled(final boolean enabled, final ResponseListener<Object> listener) {
//        get3DEnabled(new State3DModeListener() {
//
//            @Override
//            public void onSuccess(Boolean isEnabled) {
//                if (enabled != isEnabled) {
//                    sendVirtualKeyCode(NetcastVirtualKeycodes.VIDEO_3D.getCode(), listener);
//                }
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//    }
//
//    @Override
//    public void get3DEnabled(final State3DModeListener listener) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                String strObj = (String) response;
//                String upperStr = strObj.toUpperCase(Locale.ENGLISH);
//
//                Util.postSuccess(listener, upperStr.contains("TRUE"));
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_DATA, TARGET_IS_3D);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
//        request.setHttpMethod(ServiceCommand.TYPE_GET);
//        request.send();
//    }
//
//    @Override
//    public ServiceSubscription<State3DModeListener> subscribe3DEnabled(final State3DModeListener listener) {
//        get3DEnabled(listener);
//
//        URLServiceSubscription<State3DModeListener> request = new URLServiceSubscription<State3DModeListener>(this, TARGET_3D_MODE, null, null);
//        request.setHttpMethod(ServiceCommand.TYPE_GET);
//        request.addListener(listener);
//
//        addSubscription(request);
//
//        return request;
//    }
//
//
//
//    /**************
//    VOLUME
//     **************/
//    @Override
//    public VolumeControl getVolumeControl() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getVolumeControlCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public void volumeUp(ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.VOLUME_UP.getCode(), listener);
//    }
//
//    @Override
//    public void volumeDown(ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.VOLUME_DOWN.getCode(), listener);
//    }
//
//    @Override
//    public void setVolume(float volume, ResponseListener<Object> listener) {
//        if (getDLNAService() != null) {
//            getDLNAService().setVolume(volume, listener);
//        }
//        else
//            Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void getVolume(final VolumeListener listener) {
//
//        getVolumeStatus(new VolumeStatusListener() {
//
//            @Override
//            public void onSuccess(VolumeStatus status) {
//                Util.postSuccess(listener, status.volume);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//
//    }
//
//    @Override
//    public void setMute(final boolean isMute, final ResponseListener<Object> listener) {
//
//        getVolumeStatus(new VolumeStatusListener() {
//
//            @Override
//            public void onSuccess(VolumeStatus status) {
//                if (isMute != status.isMute) {
//                    sendVirtualKeyCode(NetcastVirtualKeycodes.MUTE.getCode(), listener);
//                }
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//
//    }
//
//    @Override
//    public void getMute(final MuteListener listener) {
//
//        getVolumeStatus(new VolumeStatusListener() {
//
//            @Override
//            public void onSuccess(VolumeStatus status) {
//                Util.postSuccess(listener, status.isMute);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//
//    }
//
//    @Override
//    public ServiceSubscription<VolumeListener> subscribeVolume(VolumeListener listener) {
//        if (getDLNAService() != null) {
//            return getDLNAService().subscribeVolume(listener);
//        }
//        else
//            Util.postError(listener, ServiceCommandError.notSupported());
//        return null;
//
//    }
//
//    @Override
//    public ServiceSubscription<MuteListener> subscribeMute(MuteListener listener) {
//        if (getDLNAService() != null) {
//            return getDLNAService().subscribeMute(listener);
//        }
//        else
//            Util.postError(listener, ServiceCommandError.notSupported());
//        return null;
//
//    }
//
//    private void getVolumeStatus(final VolumeStatusListener listener) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                String strObj = (String) response;
//                JSONObject volumeStatus = parseVolumeXmlToJSON(strObj);
//                try {
//                    boolean isMute = (Boolean) volumeStatus.get("mute");
//                    int volume = (Integer) volumeStatus.get("level");
//
//                    Util.postSuccess(listener, new VolumeStatus(isMute, volume));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_DATA, TARGET_VOLUME_INFO);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, null, responseListener);
//        request.setHttpMethod(ServiceCommand.TYPE_GET);
//        request.send();
//    }
//
//    /**************
//    EXTERNAL INPUT
//     **************/
//    @Override
//    public ExternalInputControl getExternalInput() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getExternalInputControlPriorityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public void launchInputPicker(final AppLaunchListener listener) {
//        final String appName = "Input List";
//        final String encodedStr = HttpMessage.encode(appName);
//
//        getApplication(encodedStr, new AppInfoListener() {
//
//            @Override
//            public void onSuccess(final AppInfo appInfo) {
//                Launcher.AppLaunchListener launchListener = new Launcher.AppLaunchListener() {
//
//                    @Override
//                    public void onSuccess(LaunchSession session) {
//                        if (inputPickerSession == null) {
//                            inputPickerSession = session;
//                        }
//
//                        Util.postSuccess(listener, session);
//                    }
//
//                    @Override
//                    public void onError(ServiceCommandError error) {
//                        Util.postError(listener, error);
//                    }
//                };
//
//                launchApplication(appName, appInfo.getId(), null, launchListener);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        });
//    }
//
//    @Override
//    public void closeInputPicker(LaunchSession launchSession, ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.EXIT.getCode(), listener);
//    }
//
//    @Override
//    public void getExternalInputList(ExternalInputListListener listener) {
//        // Do nothing - not Supported
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void setExternalInput(ExternalInputInfo input, ResponseListener<Object> listener) {
//        // Do nothing - not Supported
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//
//    /******************
//    MEDIA PLAYER
//     *****************/
//    @Override
//    public MediaPlayer getMediaPlayer() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getMediaPlayerCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public void getMediaInfo(final MediaInfoListener listener) {
//
//        if (getDLNAService() != null) {
//            getDLNAService().getMediaInfo(listener);
//        }
//        else {
//            if (listener != null)
//                Util.postError(listener, ServiceCommandError.notSupported());
//        }
//    }
//
//    @Override
//    public ServiceSubscription<MediaInfoListener> subscribeMediaInfo(
//            MediaInfoListener listener) {
//        if (getDLNAService() != null) {
//            return getDLNAService().subscribeMediaInfo(listener);
//        }
//        else {
//            if (listener != null)
//                Util.postError(listener, ServiceCommandError.notSupported());
//            return null;
//        }
//    }
//
//    @Override
//    public void displayImage(final String url, final String mimeType, final String title, final String description, final String iconSrc, final MediaPlayer.LaunchListener listener) {
//        if (getDLNAService() != null) {
//            final MediaPlayer.LaunchListener launchListener = new LaunchListener() {
//
//                @Override
//                public void onError(ServiceCommandError error) {
//                    if (listener != null)
//                        Util.postError(listener, error);
//                }
//
//                @Override
//                public void onSuccess(MediaLaunchObject object) {
//                    object.launchSession.setAppId(SMART_SHARE);
//                    object.launchSession.setAppName(SMART_SHARE);
//
//                    object.mediaControl = NetcastTVService.this.getMediaControl();
//
//                    if (listener != null)
//                        Util.postSuccess(listener, object);
//                }
//            };
//
//            getDLNAService().displayImage(url, mimeType, title, description, iconSrc, launchListener);
//        }
//        else {
//            System.err.println("DLNA Service is not ready yet");
//        }
//    }
//
//    @Override
//    public void displayImage(MediaInfo mediaInfo, LaunchListener listener) {
//        String mediaUrl = null;
//        String mimeType = null;
//        String title = null;
//        String desc = null;
//        String iconSrc = null;
//
//        if (mediaInfo != null) {
//            mediaUrl = mediaInfo.getUrl();
//            mimeType = mediaInfo.getMimeType();
//            title = mediaInfo.getTitle();
//            desc = mediaInfo.getDescription();
//
//            if (mediaInfo.getImages() != null && mediaInfo.getImages().size() > 0) {
//                ImageInfo imageInfo = mediaInfo.getImages().get(0);
//                iconSrc = imageInfo.getUrl();
//            }
//        }
//
//        displayImage(mediaUrl, mimeType, title, desc, iconSrc, listener);
//    }
//
//    @Override
//    public void playMedia(String url, String mimeType, String title, String description, String iconSrc, boolean shouldLoop, MediaPlayer.LaunchListener listener) {
//        MediaInfo mediaInfo = new MediaInfo.Builder(url, mimeType)
//                .setTitle(title)
//                .setDescription(description)
//                .setIcon(iconSrc)
//                .build();
//        playMedia(mediaInfo, shouldLoop, listener);
//    }
//
//    @Override
//    public void playMedia(MediaInfo mediaInfo, boolean shouldLoop, final MediaPlayer.LaunchListener listener) {
//        if (getDLNAService() != null) {
//            final MediaPlayer.LaunchListener launchListener = new LaunchListener() {
//
//                @Override
//                public void onError(ServiceCommandError error) {
//                    if (listener != null)
//                        Util.postError(listener, error);
//                }
//
//                @Override
//                public void onSuccess(MediaLaunchObject object) {
//                    object.launchSession.setAppId(SMART_SHARE);
//                    object.launchSession.setAppName(SMART_SHARE);
//
//                    object.mediaControl = NetcastTVService.this.getMediaControl();
//
//                    if (listener != null)
//                        Util.postSuccess(listener, object);
//                }
//            };
//
//            getDLNAService().playMedia(mediaInfo, shouldLoop, launchListener);
//        }
//        else {
//            System.err.println("DLNA Service is not ready yet");
//        }
//    }
//
//    @Override
//    public void closeMedia(LaunchSession launchSession, ResponseListener<Object> listener) {
//        if (getDLNAService() == null) {
//            Util.postError(listener, new ServiceCommandError(0, "Service is not connected", null));
//            return;
//        }
//
//        getDLNAService().closeMedia(launchSession, listener);
//    }
//
//    /******************
//    MEDIA CONTROL
//     *****************/
//    @Override
//    public MediaControl getMediaControl() {
//        if (DiscoveryManager.getInstance().getPairingLevel() == PairingLevel.OFF)
//            return this.getDLNAService();
//        else
//            return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getMediaControlCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public void play(ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.PLAY.getCode(), listener);
//    }
//
//    @Override
//    public void pause(ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.PAUSE.getCode(), listener);
//    }
//
//    @Override
//    public void stop(final ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.STOP.getCode(), listener);
//    }
//
//    @Override
//    public void rewind(ResponseListener<Object> listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void fastForward(ResponseListener<Object> listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void previous(ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.SKIP_BACKWARD.getCode(), listener);
//    }
//
//    @Override
//    public void next(ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.SKIP_FORWARD.getCode(), listener);
//    }
//
//    @Override
//    public void seek(long position, ResponseListener<Object> listener) {
//        if (getDLNAService() != null) {
//            getDLNAService().seek(position, listener);
//        } else {
//            if (listener != null)
//                Util.postError(listener, ServiceCommandError.notSupported());
//        }
//    }
//
//    @Override
//    public void getDuration(DurationListener listener) {
//        if (getDLNAService() != null) {
//            getDLNAService().getDuration(listener);
//        } else {
//            if (listener != null)
//                Util.postError(listener, ServiceCommandError.notSupported());
//        }
//    }
//
//    @Override
//    public void getPosition(PositionListener listener) {
//        if (getDLNAService() != null) {
//            getDLNAService().getPosition(listener);
//        } else {
//            if (listener != null)
//                Util.postError(listener, ServiceCommandError.notSupported());
//        }
//    }
//
//    @Override
//    public void getPlayState(PlayStateListener listener) {
//        if (getDLNAService() != null) {
//            getDLNAService().getPlayState(listener);
//        }
//        else {
//            if (listener != null)
//                Util.postError(listener, ServiceCommandError.notSupported());
//        }
//
//    }
//
//    @Override
//    public ServiceSubscription<PlayStateListener> subscribePlayState(PlayStateListener listener) {
//        if (getDLNAService() != null) {
//            return getDLNAService().subscribePlayState(listener);
//        }
//        else Util.postError(listener, ServiceCommandError.notSupported());
//        return null;
//
//    }
//
//    @Override
//    public void button(String str) {
////        WebOSTVMouseSocketConnection webOSTVMouseSocketConnection = this.mouseSocket;
////        if (webOSTVMouseSocketConnection != null) {
////            webOSTVMouseSocketConnection.button(str);
////        } else {
////            connectMouse(new WebOSTVMouseSocketConnection.WebOSTVMouseSocketListener() {
////                /* class com.connectsdk.service.WebOSTVService.AnonymousClass23 */
////
////                @Override // com.connectsdk.service.webos.WebOSTVMouseSocketConnection.WebOSTVMouseSocketListener
////                public void onConnected() {
////                    WebOSTVService.this.mouseSocket.button(str);
////                }
////            });
////        }
//    }
//
//    /**************
//    MOUSE CONTROL
//     **************/
//    @Override
//    public MouseControl getMouseControl() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getMouseControlCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    private void setMouseCursorVisible(boolean visible, ResponseListener<Object> listener) {
//        String requestURL = getUDAPRequestURL(UDAP_PATH_EVENT);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "CursorVisible");
//        params.put("value", visible? "true" : "false");
//        params.put("mode", "auto");
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_EVENT, params);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, listener);
//        request.send();
//    }
//
//    @Override
//    public void connectMouse() {
//        ResponseListener<Object> listener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                Log.d(Util.T, "Netcast TV's mouse has been connected");
//
//                mMouseDistance = new PointF(0, 0);
//                mMouseIsMoving = false;
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Log.w(Util.T, "Netcast TV's mouse connection has been failed");
//            }
//        };
//
//        setMouseCursorVisible(true, listener);
//    }
//
//    @Override
//    public void disconnectMouse() {
//        setMouseCursorVisible(false, null);
//    }
//
//    @Override
//    public void click() {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Log.w(Util.T, "Netcast TV's mouse click has been failed");
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "HandleTouchClick");
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        request.send();
//    }
//
//    @Override
//    public void move(double dx, double dy) {
//        mMouseDistance.x += dx;
//        mMouseDistance.y += dy;
//
//        if (!mMouseIsMoving)
//        {
//            mMouseIsMoving = true;
//            this.moveMouse();
//        }
//    }
//
//    private void moveMouse() {
//        String requestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
//
//        int x = (int)mMouseDistance.x;
//        int y = (int)mMouseDistance.y;
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "HandleTouchMove");
//        params.put("x", String.valueOf(x));
//        params.put("y", String.valueOf(y));
//
//        mMouseDistance.x = mMouseDistance.y = 0;
//
//        final NetcastTVService mouseService = this;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                if (mMouseDistance.x > 0 || mMouseDistance.y > 0)
//                    mouseService.moveMouse();
//                else
//                    mMouseIsMoving = false;
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Log.w(Util.T, "Netcast TV's mouse move has failed");
//
//                mMouseIsMoving = false;
//            }
//        };
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        request.send();
//    }
//
//    @Override
//    public void move(PointF diff) {
//        move(diff.x, diff.y);
//    }
//
//    @Override
//    public void scroll(double dx, double dy) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Log.w(Util.T, "Netcast TV's mouse scroll has been failed");
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "HandleTouchWheel");
//        if (dy > 0)
//            params.put("value", "up");
//        else
//            params.put("value", "down");
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_COMMAND, params);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        request.send();
//    }
//
//    @Override
//    public void scroll(PointF diff) {
//        scroll(diff.x, diff.y);
//    }
//
//
//    /**************
//    KEYBOARD CONTROL
//     **************/
//    @Override
//    public TextInputControl getTextInputControl() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getTextInputControlCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public ServiceSubscription<TextInputStatusListener> subscribeTextInputStatus(final TextInputStatusListener listener) {
//        keyboardString = new StringBuilder();
//
//        URLServiceSubscription<TextInputStatusListener> request = new URLServiceSubscription<TextInputStatusListener>(this, "KeyboardVisible", null, null);
//        request.addListener(listener);
//
//        addSubscription(request);
//
//        return request;
//    }
//
//    @Override
//    public void sendText(final String input) {
//        Log.d(Util.T, "Add to Queue: " + input);
//        keyboardString.append(input);
//        handleKeyboardInput("Editing", keyboardString.toString());
//    }
//
//    @Override
//    public void sendEnter() {
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Log.w(Util.T, "Netcast TV's enter key has been failed");
//            }
//        };
//        handleKeyboardInput("EditEnd", keyboardString.toString());
//        sendVirtualKeyCode(NetcastVirtualKeycodes.RED.getCode(), responseListener);       // Send RED Key to enter the "ENTER" button
//    }
//
//    @Override
//    public void sendDelete() {
//        if (keyboardString.length() > 1) {
//            keyboardString.deleteCharAt(keyboardString.length()-1);
//        }
//        else {
//            keyboardString = new StringBuilder();
//        }
//
//        handleKeyboardInput("Editing", keyboardString.toString());
//    }
//
//    private ResponseListener<String> mTextChangedListener = new ResponseListener<String>() {
//
//        @Override
//        public void onError(ServiceCommandError error) { }
//
//        @Override
//        public void onSuccess(String newValue) {
//            keyboardString = new StringBuilder(newValue);
//        }
//    };
//
//    private void handleKeyboardInput(final String state, final String buffer) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Log.w(Util.T, "Netcast TV's keyboard input has been failed");
//            }
//        };
//
//        String requestURL = getUDAPRequestURL(UDAP_PATH_EVENT);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "TextEdited");
//        params.put("state", state);
//        params.put("value", buffer);
//
//        String httpMessage = getUDAPMessageBody(UDAP_API_EVENT, params);
//
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, requestURL, httpMessage, responseListener);
//        request.send();
//    }
//
//
//    /**************
//    KEY CONTROL
//     **************/
//    @Override
//    public KeyControl getKeyControl() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getKeyControlCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public void up(final ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.KEY_UP.getCode(), listener);
//    }
//
//    @Override
//    public void down(final ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.KEY_DOWN.getCode(), listener);
//    }
//
//    @Override
//    public void left(final ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.KEY_LEFT.getCode(), listener);
//    }
//
//    @Override
//    public void right(final ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.KEY_RIGHT.getCode(), listener);
//    }
//
//    @Override
//    public void ok(final ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.OK.getCode(), listener);
//    }
//
//    @Override
//    public void back(final ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.BACK.getCode(), listener);
//    }
//
//    @Override
//    public void home(final ResponseListener<Object> listener) {
//        sendVirtualKeyCode(NetcastVirtualKeycodes.HOME.getCode(), listener);
//    }
//
//
//    /**************
//    POWER CONTROL
//     **************/
//    @Override
//    public PowerControl getPowerControl() {
//        return this;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getPowerControlCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public void powerOff(ResponseListener<Object> listener) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Log.w(Util.T, "Netcast TV's power off has been failed");
//            }
//        };
//
//        sendVirtualKeyCode(NetcastVirtualKeycodes.POWER.getCode(), responseListener);
//    }
//
//    @Override
//    public void powerOn(ResponseListener<Object> listener) {
//        if (listener != null)
//            listener.onError(ServiceCommandError.notSupported());
//    }
//
//    private JSONObject parseVolumeXmlToJSON(String data) {
//        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//        try {
//            InputStream stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
//
//            SAXParser saxParser = saxParserFactory.newSAXParser();
//            NetcastVolumeParser handler = new NetcastVolumeParser();
//            saxParser.parse(stream, handler);
//
//            return handler.getVolumeStatus();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private int parseAppNumberXmlToJSON(String data) {
//        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//        try {
//            InputStream stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
//
//            SAXParser saxParser = saxParserFactory.newSAXParser();
//            NetcastAppNumberParser handler = new NetcastAppNumberParser();
//            saxParser.parse(stream, handler);
//
//            return handler.getApplicationNumber();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    private JSONArray parseApplicationsXmlToJSON(String data) {
//        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//        try {
//            InputStream stream = new ByteArrayInputStream(data.getBytes("UTF-8"));
//
//            SAXParser saxParser = saxParserFactory.newSAXParser();
//            NetcastApplicationsParser handler = new NetcastApplicationsParser();
//            saxParser.parse(stream, handler);
//
//            return handler.getApplications();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public String getHttpMessageForHandleKeyInput(final int keycode) {
//        String strKeycode = String.valueOf(keycode);
//
//        Map <String,String> params = new HashMap<String,String>();
//        params.put("name", "HandleKeyInput");
//        params.put("value", strKeycode);
//
//        return getUDAPMessageBody(UDAP_API_COMMAND, params);
//    }
//
//    @Override
//    public void sendKeyCode(KeyCode keycode, ResponseListener<Object> listener) {
//        switch (keycode) {
//            case NUM_0:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_0.getCode(), listener);
//                break;
//            case NUM_1:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_1.getCode(), listener);
//                break;
//            case NUM_2:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_2.getCode(), listener);
//                break;
//            case NUM_3:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_3.getCode(), listener);
//                break;
//            case NUM_4:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_4.getCode(), listener);
//                break;
//            case NUM_5:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_5.getCode(), listener);
//                break;
//            case NUM_6:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_6.getCode(), listener);
//                break;
//            case NUM_7:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_7.getCode(), listener);
//                break;
//            case NUM_8:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_8.getCode(), listener);
//                break;
//            case NUM_9:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.NUMBER_9.getCode(), listener);
//                break;
//            case DASH:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.DASH.getCode(), listener);
//                break;
//            case ENTER:
//                sendVirtualKeyCode(NetcastVirtualKeycodes.OK.getCode(), listener);
//                break;
//            default:
//                Util.postError(listener, new ServiceCommandError(0, "The keycode is not available", null));
//        }
//    }
//
//    private void sendVirtualKeyCode(final int keycode, final ResponseListener<Object> listener) {
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                try {
//                    Thread.sleep(150);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                String requestURL = getUDAPRequestURL(UDAP_PATH_COMMAND);
//                String httpMessage = getHttpMessageForHandleKeyInput(keycode);
//
//                ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(NetcastTVService.this, requestURL, httpMessage, listener);
//                request.send();
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        setMouseCursorVisible(false, responseListener);
//    }
//
//    private String getUDAPRequestURL(String path) {
//        return getUDAPRequestURL(path, null);
//    }
//
//    private String getUDAPRequestURL(String path, String target) {
//        return getUDAPRequestURL(path, target, null);
//    }
//
//    private String getUDAPRequestURL(String path, String target, String type) {
//        return getUDAPRequestURL(path, target, type, null, null);
//    }
//
//    private String getUDAPRequestURL(String path, String target, String type, String index, String number) {
//        // Type Values
//        // 1: List of all apps
//        // 2: List of apps in the Premium category
//        // 3: List of apps in the My Apps category
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("http://");
//        sb.append(serviceDescription.getIpAddress());
//        sb.append(":");
//        sb.append(serviceDescription.getPort());
//        sb.append(path);
//
//        if (target != null) {
//            sb.append("?target=");
//            sb.append(target);
//
//            if (type != null) {
//                sb.append("&type=");
//                sb.append(type);
//            }
//
//            if (index != null) {
//                sb.append("&index=");
//                sb.append(index);
//            }
//
//            if (number != null) {
//                sb.append("&number=");
//                sb.append(number);
//            }
//        }
//
//        return sb.toString();
//    }
//
//    private String getUDAPMessageBody(String api, Map<String, String> params) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
//        sb.append("<envelope>");
//        sb.append("<api type=\"").append(api).append("\">");
//
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//
//            sb.append(createNode(key, value));
//        }
//
//        sb.append("</api>");
//        sb.append("</envelope>");
//
//        return sb.toString();
//    }
//
//    private String createNode(String tag, String value) {
//        return "<" + tag + ">" + value + "</" + tag + ">";
//    }
//
//    public String decToHex(String dec) {
//        if (dec != null && dec.length() > 0) {
//            try {
//                return decToHex(Long.parseLong(dec.trim()));
//            } catch (RuntimeException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//    public String decToHex(long dec) {
//        return String.format("%016x",dec);
//    }
//
//    @Override
//    public void sendCommand(final ServiceCommand<?> mCommand) {
//        Util.runInBackground(new Runnable() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public void run() {
//                final ServiceCommand<ResponseListener<Object>> command = (ServiceCommand<ResponseListener<Object>>) mCommand;
//                Object payload = command.getPayload();
//
//                try {
//                    HttpConnection connection = HttpConnection.newInstance(URI.create(command.getTarget()));
//                    connection.setHeader(HttpMessage.USER_AGENT, HttpMessage.UDAP_USER_AGENT);
//                    connection.setHeader(HttpMessage.CONTENT_TYPE_HEADER, HttpMessage.CONTENT_TYPE_TEXT_XML);
//                    if (payload != null && command.getHttpMethod().equalsIgnoreCase(ServiceCommand.TYPE_POST)) {
//                        connection.setMethod(HttpConnection.Method.POST);
//                        connection.setPayload(payload.toString());
//                    }
//                    connection.execute();
//                    int code = connection.getResponseCode();
//                    Log.d("", "RESP " + code);
//                    if (code == 200) {
//                        Util.postSuccess(command.getResponseListener(), connection.getResponseString());
//                    } else {
//                        Util.postError(command.getResponseListener(), ServiceCommandError.getError(code));
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Util.postError(command.getResponseListener(), new ServiceCommandError(0, e.getMessage(), null));
//                }
//            }
//        });
//    }
//
//    private void addSubscription(URLServiceSubscription<?> subscription) {
//        subscriptions.add(subscription);
//
//        if (httpServer != null)
//            httpServer.setSubscriptions(subscriptions);
//    }
//
//    @Override
//    public void unsubscribe(URLServiceSubscription<?> subscription) {
//        subscriptions.remove(subscription);
//
//        if (httpServer != null)
//            httpServer.setSubscriptions(subscriptions);
//    }
//
////    @Override
////    public LaunchSession decodeLaunchSession(String type, JSONObject obj) throws JSONException {
////        if ("netcasttv".equals(type)) {
////            LaunchSession launchSession = LaunchSession.launchSessionFromJSONObject(obj);
////            launchSession.setService(this);
////
////            return launchSession;
////        }
////        return null;
////    }
//
//    @Override
//    public void updateCapabilities() {
//        List<String> capabilities = new ArrayList<String>();
//
//        if (DiscoveryManager.getInstance().getPairingLevel() == PairingLevel.ON) {
//            Collections.addAll(capabilities, TextInputControl.Capabilities);
//            Collections.addAll(capabilities, MouseControl.Capabilities);
//            Collections.addAll(capabilities, KeyControl.Capabilities);
//            Collections.addAll(capabilities, MediaPlayer.Capabilities);
//
//            capabilities.add(PowerControl.Off);
//
//            capabilities.add(Play);
//            capabilities.add(Pause);
//            capabilities.add(Stop);
//            capabilities.add(Duration);
//            capabilities.add(Position);
//            capabilities.add(Seek);
//
//            capabilities.add(Application);
//            capabilities.add(Application_Close);
//            capabilities.add(Application_List);
//            capabilities.add(Browser);
//            capabilities.add(Hulu);
//            capabilities.add(Netflix);
//            capabilities.add(Netflix_Params);
//            capabilities.add(YouTube);
//            capabilities.add(YouTube_Params);
//            capabilities.add(AppStore);
//
//            capabilities.add(Channel_Up);
//            capabilities.add(Channel_Down);
//            capabilities.add(Channel_Get);
//            capabilities.add(Channel_List);
//            capabilities.add(Channel_Subscribe);
//            capabilities.add(Get_3D);
//            capabilities.add(Set_3D);
//            capabilities.add(Subscribe_3D);
//
//            capabilities.add(Picker_Launch);
//            capabilities.add(Picker_Close);
//
//            capabilities.add(Volume_Get);
//            capabilities.add(Volume_Up_Down);
//            capabilities.add(Mute_Get);
//            capabilities.add(Mute_Set);
//
//            if (serviceDescription.getModelNumber().equals("4.0")) {
//                capabilities.add(AppStore_Params);
//            }
//        } else {
//            Collections.addAll(capabilities, MediaPlayer.Capabilities);
//            capabilities.add(Play);
//            capabilities.add(Pause);
//            capabilities.add(Stop);
//
//            capabilities.add(YouTube);
//            capabilities.add(YouTube_Params);
//        }
//
//        capabilities.add(MediaPlayer.Subtitle_SRT);
//        setCapabilities(capabilities);
//    }
//
//    public DLNAService getDLNAService() {
//        if (dlnaService == null) {
//            DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
//            ConnectableDevice device = discoveryManager.getAllDevices().get(serviceDescription.getIpAddress());
//
//            if (device != null) {
//                DLNAService foundService = null;
//
//                for (DeviceService service: device.getServices()) {
//                    if (DLNAService.class.isAssignableFrom(service.getClass())) {
//                        foundService = (DLNAService)service;
//                        break;
//                    }
//                }
//
//                dlnaService = foundService;
//            }
//        }
//
//        return dlnaService;
//    }
//
//    public DIALService getDIALService() {
//        if (dialService == null) {
//            DiscoveryManager discoveryManager = DiscoveryManager.getInstance();
//            ConnectableDevice device = discoveryManager.getAllDevices().get(serviceDescription.getIpAddress());
//
//            if (device != null) {
//                DIALService foundService = null;
//
//                for (DeviceService service: device.getServices()) {
//                    if (DIALService.class.isAssignableFrom(service.getClass())) {
//                        foundService = (DIALService)service;
//                        break;
//                    }
//                }
//
//                dialService = foundService;
//            }
//        }
//
//        return dialService;
//    }
//}

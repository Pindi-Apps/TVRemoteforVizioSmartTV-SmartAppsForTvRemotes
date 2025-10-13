/*
 * DeviceService
 * Connect SDK
 *
 * Copyright (c) 2014 LG Electronics.
 * Created by Hyun Kook Khang on 19 Jan 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.connectsdk.service;


import android.util.SparseArray;

import com.connectsdk.core.Util;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryFilter;
import com.connectsdk.etc.helper.DeviceServiceReachability;
import com.connectsdk.service.capability.CapabilityMethods;
import com.connectsdk.service.capability.ExternalInputControl;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.WebAppLauncher;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommand;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.connectsdk.service.command.URLServiceSubscription;
import com.connectsdk.service.config.ServiceConfig;
import com.connectsdk.service.config.ServiceDescription;
import com.connectsdk.service.sessions.LaunchSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class DeviceService implements DeviceServiceReachability.DeviceServiceReachabilityListener, ServiceCommand.ServiceCommandProcessor {
    public static final String KEY_CLASS = "class";
    public static final String KEY_CONFIG = "config";
    public static final String KEY_DESC = "description";
    private ServiceCommand.ServiceCommandProcessor commandProcessor;
    protected boolean connected = false;
    protected DeviceServiceListener listener;
    List<String> mCapabilities;
    protected DeviceServiceReachability mServiceReachability;
    PairingType pairingType = PairingType.NONE;
    public SparseArray<ServiceCommand<? extends Object>> requests = new SparseArray<>();
    ServiceConfig serviceConfig;
    ServiceDescription serviceDescription;

    public interface DeviceServiceListener {
        void onCapabilitiesUpdated(DeviceService deviceService, List<String> list, List<String> list2);

        void onConnectionFailure(DeviceService deviceService, Error error);

        void onConnectionRequired(DeviceService deviceService);

        void onConnectionSuccess(DeviceService deviceService);

        void onDisconnect(DeviceService deviceService, Error error);

        void onPairingFailed(DeviceService deviceService, Error error);

        void onPairingRequired(DeviceService deviceService, PairingType pairingType, Object obj);

        void onPairingSuccess(DeviceService deviceService);
    }

    public enum PairingType {
        NONE,
        FIRST_SCREEN,
        PIN_CODE,
        MIXED
    }

    public static DiscoveryFilter discoveryFilter() {
        return null;
    }

    public void cancelPairing() {
    }

    public void connect() {
    }

    public LaunchSession decodeLaunchSession(String str, JSONObject jSONObject) throws JSONException {
        return null;
    }

    public void disconnect() {
    }

    public boolean isConnectable() {
        return false;
    }

    public boolean isConnected() {
        return true;
    }

    @Override
    // com.connectsdk.etc.helper.DeviceServiceReachability.DeviceServiceReachabilityListener
    public void onLoseReachability(DeviceServiceReachability deviceServiceReachability) {
    }

    @Override // com.connectsdk.service.command.ServiceCommand.ServiceCommandProcessor
    public void sendCommand(ServiceCommand<?> serviceCommand) {
    }

    public void sendPairingKey(String str) {
    }

    public void setPairingType(PairingType pairingType2) {
    }

    @Override // com.connectsdk.service.command.ServiceCommand.ServiceCommandProcessor
    public void unsubscribe(ServiceSubscription<?> serviceSubscription) {
    }

    @Override // com.connectsdk.service.command.ServiceCommand.ServiceCommandProcessor
    public void unsubscribe(URLServiceSubscription<?> uRLServiceSubscription) {
    }

    /* access modifiers changed from: protected */
    public void updateCapabilities() {
    }

    public DeviceService(ServiceDescription serviceDescription2, ServiceConfig serviceConfig2) {
        this.serviceDescription = serviceDescription2;
        this.serviceConfig = serviceConfig2;
        this.mCapabilities = new ArrayList();
        updateCapabilities();
    }

    public DeviceService(ServiceConfig serviceConfig2) {
        this.serviceConfig = serviceConfig2;
        this.mCapabilities = new ArrayList();
        updateCapabilities();
    }

    /* access modifiers changed from: package-private */
    public ServiceCommand.ServiceCommandProcessor getCommandProcessor() {
        ServiceCommand.ServiceCommandProcessor serviceCommandProcessor = this.commandProcessor;
        return serviceCommandProcessor == null ? this : serviceCommandProcessor;
    }

    /* access modifiers changed from: package-private */
    public void setCommandProcessor(ServiceCommand.ServiceCommandProcessor serviceCommandProcessor) {
        this.commandProcessor = serviceCommandProcessor;
    }

    public static DeviceService getService(JSONObject jSONObject) {
        try {
            String optString = jSONObject.optString("class");
            if (optString.equalsIgnoreCase("DLNAService") || optString.equalsIgnoreCase(CastService.ID)) {
                return null;
            }
            Constructor<?> constructor = Class.forName(DeviceService.class.getPackage().getName() + "." + optString).getConstructor(ServiceDescription.class, ServiceConfig.class);
            JSONObject optJSONObject = jSONObject.optJSONObject("config");
            ServiceConfig config = optJSONObject != null ? ServiceConfig.getConfig(optJSONObject) : null;
            JSONObject optJSONObject2 = jSONObject.optJSONObject("description");
            ServiceDescription description = optJSONObject2 != null ? ServiceDescription.getDescription(optJSONObject2) : null;
            if (config != null) {
                if (description != null) {
                    return (DeviceService) constructor.newInstance(description, config);
                }
            }
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return null;
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            return null;
        } catch (InstantiationException e4) {
            e4.printStackTrace();
            return null;
        } catch (IllegalAccessException e5) {
            e5.printStackTrace();
            return null;
        } catch (InvocationTargetException e6) {
            e6.printStackTrace();
            return null;
        }
    }

    public static DeviceService getService(Class<? extends DeviceService> cls, ServiceConfig serviceConfig2) {
        try {
            return (DeviceService) cls.getConstructor(ServiceConfig.class).newInstance(serviceConfig2);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return null;
        } catch (InstantiationException e3) {
            e3.printStackTrace();
            return null;
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
            return null;
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
            return null;
        }
    }

    public static DeviceService getService(Class<? extends DeviceService> cls, ServiceDescription serviceDescription2, ServiceConfig serviceConfig2) {
        try {
            return (DeviceService) cls.getConstructor(ServiceDescription.class, ServiceConfig.class).newInstance(serviceDescription2, serviceConfig2);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return null;
        } catch (InstantiationException e3) {
            e3.printStackTrace();
            return null;
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
            return null;
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
            return null;
        }
    }

    public PairingType getPairingType() {
        return this.pairingType;
    }

    public <T extends CapabilityMethods> T getAPI(Class<?> cls) {
        if (cls.isAssignableFrom(getClass())) {
            return (T) ((CapabilityMethods) this);
        }
        return null;
    }

    public CapabilityMethods.CapabilityPriorityLevel getPriorityLevel(Class<? extends CapabilityMethods> cls) {
        return CapabilityMethods.CapabilityPriorityLevel.NOT_SUPPORTED;
    }

    /* access modifiers changed from: protected */
    public void reportConnected(boolean z) {
        DeviceServiceListener deviceServiceListener = this.listener;
        if (deviceServiceListener != null) {
            if (deviceServiceListener instanceof ConnectableDevice) {
                deviceServiceListener.onConnectionSuccess(this);
            } else {
                Util.runOnUI(new Runnable() {
                    /* class com.connectsdk.service.DeviceService.AnonymousClass1 */

                    public void run() {
                        if (DeviceService.this.listener != null) {
                            DeviceService.this.listener.onConnectionSuccess(DeviceService.this);
                        }
                    }
                });
            }
        }
    }

    public List<String> getCapabilities() {
        return this.mCapabilities;
    }

    /* access modifiers changed from: protected */
    public void setCapabilities(List<String> list) {
        List<String> list2 = this.mCapabilities;
        this.mCapabilities = list;
        final ArrayList arrayList = new ArrayList();
        for (String str : list2) {
            if (!list.contains(str)) {
                arrayList.add(str);
            }
        }
        final ArrayList arrayList2 = new ArrayList();
        for (String str2 : list) {
            if (!list2.contains(str2)) {
                arrayList2.add(str2);
            }
        }
        if (this.listener != null) {
            Util.runOnUI(new Runnable() {
                /* class com.connectsdk.service.DeviceService.AnonymousClass2 */

                public void run() {
                    DeviceService.this.listener.onCapabilitiesUpdated(DeviceService.this, arrayList2, arrayList);
                }
            });
        }
    }

    public boolean hasCapability(String str) {
        Matcher matcher = CapabilityMethods.ANY_PATTERN.matcher(str);
        if (!matcher.find()) {
            return this.mCapabilities.contains(str);
        }
        String group = matcher.group();
        for (String str2 : this.mCapabilities) {
            if (str2.contains(group)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyCapability(String... strArr) {
        for (String str : strArr) {
            if (hasCapability(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCapabilities(List<String> list) {
        String[] strArr = new String[list.size()];
        list.toArray(strArr);
        return hasCapabilities(strArr);
    }

    public boolean hasCapabilities(String... strArr) {
        for (String str : strArr) {
            if (!hasCapability(str)) {
                return false;
            }
        }
        return true;
    }

    public void setServiceDescription(ServiceDescription serviceDescription2) {
        this.serviceDescription = serviceDescription2;
    }

    public ServiceDescription getServiceDescription() {
        return this.serviceDescription;
    }

    public void setServiceConfig(ServiceConfig serviceConfig2) {
        this.serviceConfig = serviceConfig2;
    }

    public ServiceConfig getServiceConfig() {
        return this.serviceConfig;
    }

    public JSONObject toJSONObject() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("class", getClass().getSimpleName());
            jSONObject.put("description", this.serviceDescription.toJSONObject());
            jSONObject.put("config", this.serviceConfig.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public String getServiceName() {
        return this.serviceDescription.getServiceID();
    }

    public DeviceServiceListener getListener() {
        return this.listener;
    }

    public void setListener(DeviceServiceListener deviceServiceListener) {
        this.listener = deviceServiceListener;
    }

    public void closeLaunchSession(LaunchSession launchSession, ResponseListener<Object> responseListener) {
        if (launchSession == null) {
            Util.postError(responseListener, new ServiceCommandError(0, "You must provide a valid LaunchSession", null));
            return;
        }
        DeviceService service = launchSession.getService();
        if (service == null) {
            Util.postError(responseListener, new ServiceCommandError(0, "There is no service attached to this launch session", null));
            return;
        }
        int i = AnonymousClass7.$SwitchMap$com$connectsdk$service$sessions$LaunchSession$LaunchSessionType[launchSession.getSessionType().ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        Util.postError(responseListener, new ServiceCommandError(0, "This DeviceService does not know ho to close this LaunchSession", null));
                    } else if (service instanceof WebAppLauncher) {
                        ((WebAppLauncher) service).closeWebApp(launchSession, responseListener);
                    }
                } else if (service instanceof ExternalInputControl) {
                    ((ExternalInputControl) service).closeInputPicker(launchSession, responseListener);
                }
            } else if (service instanceof MediaPlayer) {
                ((MediaPlayer) service).closeMedia(launchSession, responseListener);
            }
        } else if (service instanceof Launcher) {
            ((Launcher) service).closeApp(launchSession, responseListener);
        }
    }

    /* renamed from: com.connectsdk.service.DeviceService$7  reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$com$connectsdk$service$sessions$LaunchSession$LaunchSessionType;

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|12) */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            int[] iArr = new int[LaunchSession.LaunchSessionType.values().length];
            $SwitchMap$com$connectsdk$service$sessions$LaunchSession$LaunchSessionType = iArr;
            iArr[LaunchSession.LaunchSessionType.App.ordinal()] = 1;
            $SwitchMap$com$connectsdk$service$sessions$LaunchSession$LaunchSessionType[LaunchSession.LaunchSessionType.Media.ordinal()] = 2;
            $SwitchMap$com$connectsdk$service$sessions$LaunchSession$LaunchSessionType[LaunchSession.LaunchSessionType.ExternalInputPicker.ordinal()] = 3;
            $SwitchMap$com$connectsdk$service$sessions$LaunchSession$LaunchSessionType[LaunchSession.LaunchSessionType.WebApp.ordinal()] = 4;
            $SwitchMap$com$connectsdk$service$sessions$LaunchSession$LaunchSessionType[LaunchSession.LaunchSessionType.Unknown.ordinal()] = 5;
        }
    }

    public void addCapability(final String str) {
        if (str != null && str.length() != 0 && !this.mCapabilities.contains(str)) {
            this.mCapabilities.add(str);
            Util.runOnUI(new Runnable() {
                /* class com.connectsdk.service.DeviceService.AnonymousClass3 */

                public void run() {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(str);
                    if (DeviceService.this.listener != null) {
                        DeviceService.this.listener.onCapabilitiesUpdated(DeviceService.this, arrayList, new ArrayList());
                    }
                }
            });
        }
    }

    public void addCapabilities(final List<String> list) {
        if (list != null) {
            for (String str : list) {
                if (!(str == null || str.length() == 0 || this.mCapabilities.contains(str))) {
                    this.mCapabilities.add(str);
                }
            }
            Util.runOnUI(new Runnable() {
                /* class com.connectsdk.service.DeviceService.AnonymousClass4 */

                public void run() {
                    if (DeviceService.this.listener != null) {
                        DeviceService.this.listener.onCapabilitiesUpdated(DeviceService.this, list, new ArrayList());
                    }
                }
            });
        }
    }

    public void addCapabilities(String... strArr) {
        addCapabilities(Arrays.asList(strArr));
    }

    public void removeCapability(final String str) {
        if (str != null) {
            this.mCapabilities.remove(str);
            Util.runOnUI(new Runnable() {
                /* class com.connectsdk.service.DeviceService.AnonymousClass5 */

                public void run() {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(str);
                    if (DeviceService.this.listener != null) {
                        DeviceService.this.listener.onCapabilitiesUpdated(DeviceService.this, new ArrayList(), arrayList);
                    }
                }
            });
        }
    }

    public void removeCapabilities(final List<String> list) {
        if (list != null) {
            for (String str : list) {
                this.mCapabilities.remove(str);
            }
            Util.runOnUI(new Runnable() {
                /* class com.connectsdk.service.DeviceService.AnonymousClass6 */

                public void run() {
                    if (DeviceService.this.listener != null) {
                        DeviceService.this.listener.onCapabilitiesUpdated(DeviceService.this, new ArrayList(), list);
                    }
                }
            });
        }
    }

    public void removeCapabilities(String... strArr) {
        removeCapabilities(Arrays.asList(strArr));
    }
}


//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.regex.Matcher;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.util.SparseArray;
//
//import com.connectsdk.core.Util;
//import com.connectsdk.device.ConnectableDevice;
//import com.connectsdk.discovery.DiscoveryFilter;
//import com.connectsdk.etc.helper.DeviceServiceReachability;
//import com.connectsdk.etc.helper.DeviceServiceReachability.DeviceServiceReachabilityListener;
//import com.connectsdk.service.capability.CapabilityMethods;
//import com.connectsdk.service.capability.CapabilityMethods.CapabilityPriorityLevel;
//import com.connectsdk.service.capability.ExternalInputControl;
//import com.connectsdk.service.capability.Launcher;
//import com.connectsdk.service.capability.MediaPlayer;
//import com.connectsdk.service.capability.WebAppLauncher;
//import com.connectsdk.service.capability.listeners.ResponseListener;
//import com.connectsdk.service.command.ServiceCommand;
//import com.connectsdk.service.command.ServiceCommand.ServiceCommandProcessor;
//import com.connectsdk.service.command.ServiceCommandError;
//import com.connectsdk.service.command.ServiceSubscription;
//import com.connectsdk.service.command.URLServiceSubscription;
//import com.connectsdk.service.config.ServiceConfig;
//import com.connectsdk.service.config.ServiceDescription;
//import com.connectsdk.service.sessions.LaunchSession;
//
///**
// * ###Overview
// * From a high-level perspective, DeviceService completely abstracts the functionality of a particular service/protocol (webOS TV, Netcast TV, Chromecast, Roku, DIAL, etc).
// *
// * ###In Depth
// * DeviceService is an abstract class that is meant to be extended. You shouldn't ever use DeviceService directly, unless extending it to provide support for an additional service/protocol.
// *
// * Immediately after discovery of a DeviceService, DiscoveryManager will set the DeviceService's Listener to the ConnectableDevice that owns the DeviceService. You should not change the Listener unless you intend to manage the lifecycle of that service. The DeviceService will proxy all of its Listener method calls through the ConnectableDevice's ConnectableDeviceListener.
// *
// * ####Connection & Pairing
// * Your ConnectableDevice object will let you know if you need to connect or pair to any services.
// *
// * ####Capabilities
// * All DeviceService objects have a group of capabilities. These capabilities can be implemented by any object, and that object will be returned when you call the DeviceService's capability methods (launcher, mediaPlayer, volumeControl, etc).
// */
//public class DeviceService implements DeviceServiceReachabilityListener, ServiceCommandProcessor {
//
//    /**
//     * Enumerates available pairing types. It is used by a DeviceService for implementing pairing
//     * strategy.
//     */
//    public enum PairingType {
//        /**
//         * DeviceService doesn't require pairing
//         */
//        NONE,
//
//        /**
//         * In this mode user must confirm pairing on the first screen device (e.g. an alert on a TV)
//         */
//        FIRST_SCREEN,
//
//        /**
//         * In this mode user must enter a pin code from a mobile device and send it to the first
//         * screen device
//         */
//        PIN_CODE,
//
//        /**
//         * In this mode user can either enter a pin code from a mobile device or confirm
//         * pairing on the TV
//         */
//        MIXED,
//    }
//
//    // @cond INTERNAL
//    public static final String KEY_CLASS = "class";
//    public static final String KEY_CONFIG = "config";
//    public static final String KEY_DESC = "description";
//
//    protected PairingType pairingType = PairingType.NONE;
//
//    protected ServiceDescription serviceDescription;
//
//    protected ServiceConfig serviceConfig;
//
//    protected DeviceServiceReachability mServiceReachability;
//    protected boolean connected = false;
//
//    private ServiceCommandProcessor commandProcessor;
//    // @endcond
//
//    /**
//     * An array of capabilities supported by the DeviceService. This array may change based off a number of factors.
//     * - DiscoveryManager's pairingLevel value
//     * - Connect SDK framework version
//     * - First screen device OS version
//     * - First screen device configuration (apps installed, settings, etc)
//     * - Physical region
//     */
//    List<String> mCapabilities;
//
//    // @cond INTERNAL
//    protected DeviceServiceListener listener;
//
//    public SparseArray<ServiceCommand<? extends Object>> requests = new SparseArray<ServiceCommand<? extends Object>>();
//
//    public DeviceService(ServiceDescription serviceDescription, ServiceConfig serviceConfig) {
//        this.serviceDescription = serviceDescription;
//        this.serviceConfig = serviceConfig;
//
//        mCapabilities = new ArrayList<String>();
//
//        updateCapabilities();
//    }
//
//    public DeviceService(ServiceConfig serviceConfig) {
//        this.serviceConfig = serviceConfig;
//
//        mCapabilities = new ArrayList<String>();
//
//        updateCapabilities();
//    }
//
//    ServiceCommandProcessor getCommandProcessor() {
//        return commandProcessor == null ? this : commandProcessor;
//    }
//
//    void setCommandProcessor(ServiceCommandProcessor commandProcessor) {
//        this.commandProcessor = commandProcessor;
//    }
//
//    @SuppressWarnings("unchecked")
//    public static DeviceService getService(JSONObject json) {
//        Class<DeviceService> newServiceClass;
//
//        try {
//            String className = json.optString(KEY_CLASS);
//
//            if (className.equalsIgnoreCase("DLNAService"))
//                return null;
//
//            if (className.equalsIgnoreCase("Chromecast"))
//                return null;
//
//            newServiceClass = (Class<DeviceService>) Class.forName(DeviceService.class.getPackage().getName() + "." + className);
//            Constructor<DeviceService> constructor = newServiceClass.getConstructor(ServiceDescription.class, ServiceConfig.class);
//
//            JSONObject jsonConfig = json.optJSONObject(KEY_CONFIG);
//            ServiceConfig serviceConfig = null;
//            if (jsonConfig != null)
//                serviceConfig = ServiceConfig.getConfig(jsonConfig);
//
//            JSONObject jsonDescription = json.optJSONObject(KEY_DESC);
//            ServiceDescription serviceDescription = null;
//            if (jsonDescription != null)
//                serviceDescription = ServiceDescription.getDescription(jsonDescription);
//
//            if (serviceConfig == null || serviceDescription == null)
//                return null;
//
//            return constructor.newInstance(serviceDescription, serviceConfig);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    public static DeviceService getService(Class<? extends DeviceService> clazz, ServiceConfig serviceConfig) {
//        try {
//            Constructor<? extends DeviceService> constructor = clazz.getConstructor(ServiceConfig.class);
//
//            return constructor.newInstance(serviceConfig);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    public static DeviceService getService(Class<? extends DeviceService> clazz, ServiceDescription serviceDescription, ServiceConfig serviceConfig) {
//        try {
//            Constructor<? extends DeviceService> constructor = clazz.getConstructor(ServiceDescription.class, ServiceConfig.class);
//
//            return constructor.newInstance(serviceDescription, serviceConfig);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    public PairingType getPairingType() {
//        return pairingType;
//    }
//
//    public void setPairingType(PairingType pairingType) {
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T extends CapabilityMethods> T getAPI(Class<?> clazz) {
//        if (clazz.isAssignableFrom(this.getClass())) {
//            return (T) this;
//        }
//        else
//            return null;
//    }
//
//    public CapabilityMethods.CapabilityPriorityLevel getPriorityLevel(Class<? extends CapabilityMethods> clazz) {
//        return CapabilityPriorityLevel.NOT_SUPPORTED;
//    }
//
//    public static DiscoveryFilter discoveryFilter() {
//        return null;
//    }
//    // @endcond
//
//    /**
//     * Will attempt to connect to the DeviceService. The failure/success will be reported back to the DeviceServiceListener. If the connection attempt reveals that pairing is required, the DeviceServiceListener will also be notified in that event.
//     */
//    public void connect() {
//
//    }
//
//    /**
//     * Will attempt to disconnect from the DeviceService. The failure/success will be reported back to the DeviceServiceListener.
//     */
//    public void disconnect() {
//
//    }
//
//    /** Whether the DeviceService is currently connected */
//    public boolean isConnected() {
//        return true;
//    }
//
//    public boolean isConnectable() {
//        return false;
//    }
//
//    /** Explicitly cancels pairing in services that require pairing. In some services, this will hide a prompt that is displaying on the device. */
//    public void cancelPairing() {
//
//    }
//
//    protected void reportConnected(boolean ready) {
//        if (listener == null)
//            return;
//
//        // only run callback on main thread if the callback is leaving the SDK
//        if (listener instanceof ConnectableDevice)
//            listener.onConnectionSuccess(this);
//        else {
//            Util.runOnUI(new Runnable() {
//                @Override
//                public void run() {
//                    if (listener != null)
//                        listener.onConnectionSuccess(DeviceService.this);
//                }
//            });
//        }
//    }
//
//    /**
//     * Will attempt to pair with the DeviceService with the provided pairingData. The failure/success will be reported back to the DeviceServiceListener.
//     *
//     * @param pairingKey Data to be used for pairing. The type of this parameter will vary depending on what type of pairing is required, but is likely to be a string (pin code, pairing key, etc).
//     */
//    public void sendPairingKey(String pairingKey) {
//
//    }
//
//    // @cond INTERNAL
//
//    public void unsubscribe(URLServiceSubscription<?> subscription) {
//
//    }
//
//    public void unsubscribe(ServiceSubscription<?> subscription) {
//
//    }
//
//    public void sendCommand(ServiceCommand<?> command) {
//
//    }
//
//    // @endcond
//
//    public List<String> getCapabilities() {
//        return mCapabilities;
//    }
//
//    protected void updateCapabilities() { }
//
//    protected void setCapabilities(List<String> newCapabilities) {
//        List<String> oldCapabilities = mCapabilities;
//
//        mCapabilities = newCapabilities;
//
//        List<String> _lostCapabilities = new ArrayList<String>();
//
//        for (String capability : oldCapabilities) {
//            if (!newCapabilities.contains(capability))
//                _lostCapabilities.add(capability);
//        }
//
//        List<String> _addedCapabilities = new ArrayList<String>();
//
//        for (String capability : newCapabilities) {
//            if (!oldCapabilities.contains(capability))
//                _addedCapabilities.add(capability);
//        }
//
//        final List<String> lostCapabilities = _lostCapabilities;
//        final List<String> addedCapabilities = _addedCapabilities;
//
//        if (this.listener != null) {
//            Util.runOnUI(new Runnable() {
//
//                @Override
//                public void run() {
//                    listener.onCapabilitiesUpdated(DeviceService.this, addedCapabilities, lostCapabilities);
//                }
//            });
//        }
//    }
//
//    /**
//     * Test to see if the capabilities array contains a given capability. See the individual Capability classes for acceptable capability values.
//     *
//     * It is possible to append a wildcard search term `.Any` to the end of the search term. This method will return true for capabilities that match the term up to the wildcard.
//     *
//     * Example: `Launcher.App.Any`
//     *
//     * @param capability Capability to test against
//     */
//    public boolean hasCapability(String capability) {
//        Matcher m = CapabilityMethods.ANY_PATTERN.matcher(capability);
//
//        if (m.find()) {
//            String match = m.group();
//            for (String item : this.mCapabilities) {
//                if (item.contains(match)) {
//                    return true;
//                }
//            }
//
//            return false;
//        }
//
//        return mCapabilities.contains(capability);
//    }
//
//    /**
//     * Test to see if the capabilities array contains at least one capability in a given set of capabilities. See the individual Capability classes for acceptable capability values.
//     *
//     * See hasCapability: for a description of the wildcard feature provided by this method.
//     *
//     * @param capabilities Set of capabilities to test against
//     */
//    public boolean hasAnyCapability(String... capabilities) {
//        for (String capability : capabilities) {
//            if (hasCapability(capability))
//                return true;
//        }
//
//        return false;
//    }
//
//    /**
//     * Test to see if the capabilities array contains a given set of capabilities. See the individual Capability classes for acceptable capability values.
//     *
//     * See hasCapability: for a description of the wildcard feature provided by this method.
//     *
//     * @param capabilities List of capabilities to test against
//     */
//    public boolean hasCapabilities(List<String> capabilities) {
//        String[] arr = new String[capabilities.size()];
//        capabilities.toArray(arr);
//        return hasCapabilities(arr);
//    }
//
//    /**
//     * Test to see if the capabilities array contains a given set of capabilities. See the individual Capability classes for acceptable capability values.
//     *
//     * See hasCapability: for a description of the wildcard feature provided by this method.
//     *
//     * @param capabilities Set of capabilities to test against
//     */
//    public boolean hasCapabilities(String... capabilities) {
//        boolean hasCaps = true;
//
//        for (String capability: capabilities) {
//            if (!hasCapability(capability)) {
//                hasCaps = false;
//                break;
//            }
//        }
//
//        return hasCaps;
//    }
//
//    // @cond INTERNAL
//    public void setServiceDescription(ServiceDescription serviceDescription) {
//        this.serviceDescription = serviceDescription;
//    }
//    // @endcond
//
//    public ServiceDescription getServiceDescription() {
//        return serviceDescription;
//    }
//
//    // @cond INTERNAL
//    public void setServiceConfig(ServiceConfig serviceConfig) {
//        this.serviceConfig = serviceConfig;
//    }
//    // @endcond
//
//    public ServiceConfig getServiceConfig() {
//        return serviceConfig;
//    }
//
//    public JSONObject toJSONObject() {
//        JSONObject jsonObj = new JSONObject();
//
//        try {
//            jsonObj.put(KEY_CLASS, getClass().getSimpleName());
//            jsonObj.put("description", serviceDescription.toJSONObject());
//            jsonObj.put("config", serviceConfig.toJSONObject());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return jsonObj;
//    }
//
//    /** Name of the DeviceService (webOS, Chromecast, etc) */
//    public String getServiceName() {
//        return serviceDescription.getServiceID();
//    }
//
//    // @cond INTERNAL
//    /**
//     * Create a LaunchSession from a serialized JSON object.
//     * May return null if the session was not the one that created the session.
//     *
//     * Intended for internal use.
//     */
//    public LaunchSession decodeLaunchSession(String type, JSONObject sessionObj) throws JSONException {
//        return null;
//    }
//
//    public DeviceServiceListener getListener() {
//        return listener;
//    }
//
//    public void setListener(DeviceServiceListener listener) {
//        this.listener = listener;
//    }
//    // @endcond
//
//    /**
//     * Closes the session on the first screen device. Depending on the sessionType, the associated service will have different ways of handling the close functionality.
//     *
//     * @param launchSession LaunchSession to close
//     * @param listener (optional) listener to be called on success/failure
//     */
//    public void closeLaunchSession(LaunchSession launchSession, ResponseListener<Object> listener) {
//        if (launchSession == null) {
//            Util.postError(listener, new ServiceCommandError(0, "You must provide a valid LaunchSession", null));
//            return;
//        }
//
//        DeviceService service = launchSession.getService();
//        if (service == null) {
//            Util.postError(listener, new ServiceCommandError(0, "There is no service attached to this launch session", null));
//            return;
//        }
//
//        switch (launchSession.getSessionType()) {
//        case App:
//            if (service instanceof Launcher)
//                ((Launcher) service).closeApp(launchSession, listener);
//            break;
//        case Media:
//            if (service instanceof MediaPlayer)
//                ((MediaPlayer) service).closeMedia(launchSession, listener);
//            break;
//        case ExternalInputPicker:
//            if (service instanceof ExternalInputControl)
//                ((ExternalInputControl) service).closeInputPicker(launchSession, listener);
//            break;
//        case WebApp:
//            if (service instanceof WebAppLauncher)
//                ((WebAppLauncher) service).closeWebApp(launchSession, listener);
//            break;
//        case Unknown:
//        default:
//            Util.postError(listener, new ServiceCommandError(0, "This DeviceService does not know ho to close this LaunchSession", null));
//            break;
//        }
//    }
//
//    // @cond INTERNAL
//    public void addCapability(final String capability) {
//        if (capability == null || capability.length() == 0 || this.mCapabilities.contains(capability))
//            return;
//
//        this.mCapabilities.add(capability);
//
//        Util.runOnUI(new Runnable() {
//
//            @Override
//            public void run() {
//                List<String> added = new ArrayList<String>();
//                added.add(capability);
//
//                if (listener != null)
//                    listener.onCapabilitiesUpdated(DeviceService.this, added, new ArrayList<String>());
//            }
//        });
//    }
//
//    public void addCapabilities(final List<String> capabilities) {
//        if (capabilities == null)
//            return;
//
//        for (String capability : capabilities) {
//            if (capability == null || capability.length() == 0 || mCapabilities.contains(capability))
//                continue;
//
//            mCapabilities.add(capability);
//        }
//
//        Util.runOnUI(new Runnable() {
//
//            @Override
//            public void run() {
//
//                if (listener != null)
//                    listener.onCapabilitiesUpdated(DeviceService.this, capabilities, new ArrayList<String>());
//            }
//        });
//    }
//
//    public void addCapabilities(String... capabilities) {
//        addCapabilities(Arrays.asList(capabilities));
//    }
//
//    public void removeCapability(final String capability) {
//        if (capability == null)
//            return;
//
//        this.mCapabilities.remove(capability);
//
//        Util.runOnUI(new Runnable() {
//
//            @Override
//            public void run() {
//                List<String> removed = new ArrayList<String>();
//                removed.add(capability);
//
//                if (listener != null)
//                    listener.onCapabilitiesUpdated(DeviceService.this, new ArrayList<String>(), removed);
//            }
//        });
//    }
//
//    public void removeCapabilities(final List<String> capabilities) {
//        if (capabilities == null)
//            return;
//
//        for (String capability : capabilities) {
//            mCapabilities.remove(capability);
//        }
//
//        Util.runOnUI(new Runnable() {
//
//            @Override
//            public void run() {
//
//                if (listener != null)
//                    listener.onCapabilitiesUpdated(DeviceService.this, new ArrayList<String>(), capabilities);
//            }
//        });
//    }
//
//    public void removeCapabilities(String... capabilities) {
//        removeCapabilities(Arrays.asList(capabilities));
//    }
//
//    //  Unused by default.
//    @Override public void onLoseReachability(DeviceServiceReachability reachability) { }
//    // @endcond
//
//    public interface DeviceServiceListener {
//
//        /*!
//         * If the DeviceService requires an active connection (websocket, pairing, etc) this method will be called.
//         *
//         * @param service DeviceService that requires connection
//         */
//        public void onConnectionRequired(DeviceService service);
//
//        /*!
//         * After the connection has been successfully established, and after pairing (if applicable), this method will be called.
//         *
//         * @param service DeviceService that was successfully connected
//         */
//        public void onConnectionSuccess(DeviceService service);
//
//        /*!
//         * There are situations in which a DeviceService will update the capabilities it supports and propagate these changes to the DeviceService. Such situations include:
//         * - on discovery, DIALService will reach out to detect if certain apps are installed
//         * - on discovery, certain DeviceServices need to reach out for version & region information
//         *
//         * For more information on this particular method, see ConnectableDeviceDelegate's connectableDevice:capabilitiesAdded:removed: method.
//         *
//         * @param service DeviceService that has experienced a change in capabilities
//         * @param added List<String> of capabilities that are new to the DeviceService
//         * @param removed List<String> of capabilities that the DeviceService has lost
//         */
//        public void onCapabilitiesUpdated(DeviceService service, List<String> added, List<String> removed);
//
//        /*!
//         * This method will be called on any disconnection. If error is nil, then the connection was clean and likely triggered by the responsible DiscoveryProvider or by the user.
//         *
//         * @param service DeviceService that disconnected
//         * @param error Error with a description of any errors causing the disconnect. If this value is nil, then the disconnect was clean/expected.
//         */
//        public void onDisconnect(DeviceService service, Error error);
//
//        /*!
//         * Will be called if the DeviceService fails to establish a connection.
//         *
//         * @param service DeviceService which has failed to connect
//         * @param error Error with a description of the failure
//         */
//        public void onConnectionFailure(DeviceService service, Error error);
//
//        /*!
//         * If the DeviceService requires pairing, valuable data will be passed to the delegate via this method.
//         *
//         * @param service DeviceService that requires pairing
//         * @param pairingType PairingType that the DeviceService requires
//         * @param pairingData Any data that might be required for the pairing process, will usually be nil
//         */
//        public void onPairingRequired(DeviceService service, PairingType pairingType, Object pairingData);
//
//        /*!
//         * This method will be called upon pairing success. On pairing success, a connection to the DeviceService will be attempted.
//         *
//         * @property service DeviceService that has successfully completed pairing
//         */
//        public void onPairingSuccess(DeviceService service);
//
//        /*!
//         * If there is any error in pairing, this method will be called.
//         *
//         * @param service DeviceService that has failed to complete pairing
//         * @param error Error with a description of the failure
//         */
//        public void onPairingFailed(DeviceService service, Error error);
//    }
//}

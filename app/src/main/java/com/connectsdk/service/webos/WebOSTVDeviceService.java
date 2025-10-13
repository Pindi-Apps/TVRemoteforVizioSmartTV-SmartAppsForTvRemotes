/*
 * WebOSTVDeviceService
 * Connect SDK
 *
 * Copyright (c) 2018 LG Electronics.
 * Created by Sudeep Mukherjee on 25 Oct 2018
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
package com.connectsdk.service.webos;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.amazon.whisperlink.util.WhisperLinkUtil;
import com.connectsdk.core.AppInfo;
import com.connectsdk.core.ChannelInfo;
import com.connectsdk.core.ProgramInfo;
import com.connectsdk.core.ProgramList;
import com.connectsdk.core.Util;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.CastService;
import com.connectsdk.service.DLNAService;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.PowerControl;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommand;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.connectsdk.service.command.URLServiceSubscription;
import com.connectsdk.service.config.ServiceConfig;
import com.connectsdk.service.config.ServiceDescription;
import com.connectsdk.service.sessions.LaunchSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class WebOSTVDeviceService extends DeviceService implements PowerControl, MediaControl {
    static String APP_STATE = "ssap://system.launcher/getAppState";
    static String CHANNEL = "ssap://tv/getCurrentChannel";
    static String CHANNEL_LIST = "ssap://tv/getChannelList";
    static String FOREGROUND_APP = "ssap://com.webos.applicationManager/getForegroundAppInfo";
    static String MUTE = "ssap://audio/getMute";
    static String PROGRAM = "ssap://tv/getChannelProgramInfo";
    static String VOLUME = "ssap://audio/getVolume";
    static String VOLUME_STATUS = "ssap://audio/getStatus";
    public static final String[] kWebOSTVServiceOpenPermissions = {"LAUNCH", "LAUNCH_WEBAPP", "APP_TO_APP", "CONTROL_AUDIO", "CONTROL_INPUT_MEDIA_PLAYBACK", "UPDATE_FROM_REMOTE_APP"};
    public static final String[] kWebOSTVServicePersonalActivityPermissions = {"CONTROL_INPUT_TEXT", "CONTROL_MOUSE_AND_KEYBOARD", "READ_CURRENT_CHANNEL", "READ_RUNNING_APPS"};
    public static final String[] kWebOSTVServiceProtectedPermissions = {"CONTROL_POWER", "READ_INSTALLED_APPS", "CONTROL_DISPLAY", "CONTROL_INPUT_JOYSTICK", "CONTROL_INPUT_MEDIA_RECORDING", "CONTROL_INPUT_TV", "READ_INPUT_DEVICE_LIST", "READ_NETWORK_STATE", "READ_TV_CHANNEL_LIST", "WRITE_NOTIFICATION_TOAST", "CONTROL_BLUETOOTH", "CHECK_BLUETOOTH_DEVICE", "CONTROL_USER_INFO", "CONTROL_TIMER_INFO", "READ_SETTINGS", "CONTROL_TV_SCREEN"};
    protected PairingType pairingType = PairingType.NONE;
    protected ServiceConfig serviceConfig;
    protected ServiceDescription serviceDescription;

    public interface WebOSTVServicePermission {
        public static final WebOSTVServicePermission[] OPEN = {Open.LAUNCH, Open.LAUNCH_WEB, Open.APP_TO_APP, Open.CONTROL_AUDIO, Open.CONTROL_INPUT_MEDIA_PLAYBACK};
        public static final WebOSTVServicePermission[] PERSONAL_ACTIVITY = {PersonalActivity.CONTROL_INPUT_TEXT, PersonalActivity.CONTROL_MOUSE_AND_KEYBOARD, PersonalActivity.READ_CURRENT_CHANNEL, PersonalActivity.READ_RUNNING_APPS};
        public static final WebOSTVServicePermission[] PROTECTED = {Protected.CONTROL_POWER, Protected.READ_INSTALLED_APPS, Protected.CONTROL_DISPLAY, Protected.CONTROL_INPUT_JOYSTICK, Protected.CONTROL_INPUT_MEDIA_RECORDING, Protected.CONTROL_INPUT_TV, Protected.READ_INPUT_DEVICE_LIST, Protected.READ_NETWORK_STATE, Protected.READ_TV_CHANNEL_LIST, Protected.WRITE_NOTIFICATION_TOAST, Protected.CONTROL_BLUETOOTH, Protected.CHECK_BLUETOOTH_DEVICE, Protected.CONTROL_TV_SCREEN};

        public enum Open implements WebOSTVServicePermission {
            LAUNCH,
            LAUNCH_WEB,
            APP_TO_APP,
            CONTROL_AUDIO,
            CONTROL_INPUT_MEDIA_PLAYBACK
        }

        public enum PersonalActivity implements WebOSTVServicePermission {
            CONTROL_INPUT_TEXT,
            CONTROL_MOUSE_AND_KEYBOARD,
            READ_CURRENT_CHANNEL,
            READ_RUNNING_APPS
        }

        public enum Protected implements WebOSTVServicePermission {
            CONTROL_POWER,
            READ_INSTALLED_APPS,
            CONTROL_DISPLAY,
            CONTROL_INPUT_JOYSTICK,
            CONTROL_INPUT_MEDIA_RECORDING,
            CONTROL_INPUT_TV,
            READ_INPUT_DEVICE_LIST,
            READ_NETWORK_STATE,
            READ_TV_CHANNEL_LIST,
            WRITE_NOTIFICATION_TOAST,
            CONTROL_BLUETOOTH,
            CHECK_BLUETOOTH_DEVICE,
            CONTROL_TV_SCREEN
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public MediaControl getMediaControl() {
        return null;
    }

    @Override // com.connectsdk.service.capability.PowerControl
    public PowerControl getPowerControl() {
        return this;
    }

    /* access modifiers changed from: protected */
    public DeviceService getDLNAService() {
//        if (AllRemoteManager.Companion.getConnectedTV() == null) {
//            return null;
//        }
        return null;
    }

    public WebOSTVDeviceService(ServiceDescription serviceDescription2, ServiceConfig serviceConfig2) {
        super(serviceDescription2, serviceConfig2);
        this.serviceDescription = serviceDescription2;
        this.serviceConfig = serviceConfig2;
    }

    public ServiceCommand<ResponseListener<Object>> getCurrentSWInfo(final ResponseListener<Object> responseListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand = new ServiceCommand<>(this, "ssap://com.webos.service.update/getCurrentSWInformation", null, true, new ResponseListener<Object>() {
            /* class com.connectsdk.service.webos.WebOSTVDeviceService.AnonymousClass1 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                Util.postSuccess(responseListener, (JSONObject) obj);
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(responseListener, serviceCommandError);
            }
        });
        serviceCommand.send();
        return serviceCommand;
    }

    @Override // com.connectsdk.service.DeviceService
    public void setPairingType(PairingType pairingType2) {
        this.pairingType = pairingType2;
    }

    /* access modifiers changed from: protected */
    public ServiceCommand<ResponseListener<Object>> getMuteStatus(boolean z, final VolumeControl.MuteListener muteListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand;
        ResponseListener r5 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.webos.WebOSTVDeviceService.AnonymousClass2 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                try {
                    Util.postSuccess(muteListener, Boolean.valueOf(((Boolean) ((JSONObject) obj).get(CastService.CAST_SERVICE_MUTE_SUBSCRIPTION_NAME)).booleanValue()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(muteListener, serviceCommandError);
            }
        };
        if (z) {
            serviceCommand = new URLServiceSubscription<>(this, MUTE, null, true, r5);
        } else {
            serviceCommand = new ServiceCommand<>(this, MUTE, null, true, r5);
        }
        serviceCommand.send();
        return serviceCommand;
    }

    /* access modifiers changed from: protected */
    public ServiceCommand<ResponseListener<Object>> getVolumeStatus(boolean z, final VolumeControl.VolumeStatusListener volumeStatusListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand;
        ResponseListener r5 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.webos.WebOSTVDeviceService.AnonymousClass3 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                try {
                    JSONObject jSONObject = (JSONObject) obj;
                    boolean booleanValue = ((Boolean) jSONObject.get(CastService.CAST_SERVICE_MUTE_SUBSCRIPTION_NAME)).booleanValue();
                    int i = 0;
                    if (jSONObject.has("volume")) {
                        i = ((Integer) jSONObject.get("volume")).intValue();
                    } else if (jSONObject.has("volumeStatus")) {
                        i = ((Integer) jSONObject.getJSONObject("volumeStatus").get("volume")).intValue();
                    }
                    double d = (double) i;
                    Double.isNaN(d);
                    Util.postSuccess(volumeStatusListener, new VolumeControl.VolumeStatus(booleanValue, (float) (d / 100.0d)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(volumeStatusListener, serviceCommandError);
            }
        };
        if (z) {
            serviceCommand = new URLServiceSubscription<>(this, VOLUME_STATUS, null, true, r5);
        } else {
            serviceCommand = new ServiceCommand<>(this, VOLUME_STATUS, null, true, r5);
        }
        serviceCommand.send();
        return serviceCommand;
    }

    /* access modifiers changed from: protected */
    public ProgramInfo parseRawProgramInfo(JSONObject jSONObject) {
        ProgramInfo programInfo = new ProgramInfo();
        programInfo.setRawData(jSONObject);
        String optString = jSONObject.optString("programId");
        String optString2 = jSONObject.optString("programName");
        ChannelInfo parseRawChannelData = parseRawChannelData(jSONObject);
        programInfo.setId(optString);
        programInfo.setName(optString2);
        programInfo.setChannelInfo(parseRawChannelData);
        return programInfo;
    }

    /* access modifiers changed from: protected */
    public ChannelInfo parseRawChannelData(JSONObject jSONObject) {
        int i;
        int i2;
        ChannelInfo channelInfo = new ChannelInfo();
        channelInfo.setRawData(jSONObject);
        try {
            String str = null;
            String str2 = !jSONObject.isNull("channelName") ? (String) jSONObject.get("channelName") : null;
            if (!jSONObject.isNull("channelId")) {
                str = (String) jSONObject.get("channelId");
            }
            String optString = jSONObject.optString("channelNumber");
            if (!jSONObject.isNull("majorNumber")) {
                i = ((Integer) jSONObject.get("majorNumber")).intValue();
            } else {
                i = parseMajorNumber(optString);
            }
            if (!jSONObject.isNull("minorNumber")) {
                i2 = ((Integer) jSONObject.get("minorNumber")).intValue();
            } else {
                i2 = parseMinorNumber(optString);
            }
            channelInfo.setName(str2);
            channelInfo.setId(str);
            channelInfo.setNumber(optString);
            channelInfo.setMajorNumber(i);
            channelInfo.setMinorNumber(i2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return channelInfo;
    }

    /* access modifiers changed from: protected */
    public int parseMinorNumber(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        String[] split = str.split("-");
        return Integer.valueOf(split[split.length - 1]).intValue();
    }

    /* access modifiers changed from: protected */
    public int parseMajorNumber(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(str.split("-")[0]).intValue();
    }

    /* access modifiers changed from: protected */
    public ServiceCommand<ResponseListener<Object>> getCurrentChannel(boolean z, final TVControl.ChannelListener channelListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand;
        ResponseListener r5 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.webos.WebOSTVDeviceService.AnonymousClass4 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                Util.postSuccess(channelListener, WebOSTVDeviceService.this.parseRawChannelData((JSONObject) obj));
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(channelListener, serviceCommandError);
            }
        };
        if (z) {
            serviceCommand = new URLServiceSubscription<>(this, CHANNEL, null, true, r5);
        } else {
            serviceCommand = new ServiceCommand<>(this, CHANNEL, null, true, r5);
        }
        serviceCommand.send();
        return serviceCommand;
    }

    /* access modifiers changed from: protected */
    public ServiceCommand<ResponseListener<Object>> getChannelList(boolean z, final TVControl.ChannelListListener channelListListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand;
        ResponseListener r5 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.webos.WebOSTVDeviceService.AnonymousClass5 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                try {
                    ArrayList arrayList = new ArrayList();
                    JSONArray jSONArray = (JSONArray) ((JSONObject) obj).get("channelList");
                    for (int i = 0; i < jSONArray.length(); i++) {
                        arrayList.add(WebOSTVDeviceService.this.parseRawChannelData((JSONObject) jSONArray.get(i)));
                    }
                    Util.postSuccess(channelListListener, arrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(channelListListener, serviceCommandError);
            }
        };
        if (z) {
            serviceCommand = new URLServiceSubscription<>(this, CHANNEL_LIST, null, true, r5);
        } else {
            serviceCommand = new ServiceCommand<>(this, CHANNEL_LIST, null, true, r5);
        }
        serviceCommand.send();
        return serviceCommand;
    }

    /* access modifiers changed from: protected */
    public ServiceCommand<ResponseListener<Object>> getChannelCurrentProgramInfo(boolean z, final TVControl.ProgramInfoListener programInfoListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand;
        ResponseListener r5 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.webos.WebOSTVDeviceService.AnonymousClass6 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                Util.postSuccess(programInfoListener, WebOSTVDeviceService.this.parseRawProgramInfo((JSONObject) obj));
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(programInfoListener, serviceCommandError);
            }
        };
        if (z) {
            serviceCommand = new URLServiceSubscription<>(this, "ssap://tv/getChannelCurrentProgramInfo", null, true, r5);
        } else {
            serviceCommand = new ServiceCommand<>(this, "ssap://tv/getChannelCurrentProgramInfo", null, true, r5);
        }
        serviceCommand.send();
        return serviceCommand;
    }

    /* access modifiers changed from: protected */
    public ServiceCommand<ResponseListener<Object>> getProgramList(boolean z, final TVControl.ProgramListListener programListListener) {
        ServiceCommand<ResponseListener<Object>> serviceCommand;
        ResponseListener r5 = new ResponseListener<Object>() {
            /* class com.connectsdk.service.webos.WebOSTVDeviceService.AnonymousClass7 */

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                try {
                    JSONObject jSONObject = (JSONObject) obj;
                    Util.postSuccess(programListListener, new ProgramList(WebOSTVDeviceService.this.parseRawChannelData((JSONObject) jSONObject.get(WhisperLinkUtil.CHANNEL_TAG)), (JSONArray) jSONObject.get("programList")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(programListListener, serviceCommandError);
            }
        };
        if (z) {
            serviceCommand = new URLServiceSubscription<>(this, PROGRAM, null, true, r5);
        } else {
            serviceCommand = new ServiceCommand<>(this, PROGRAM, null, true, r5);
        }
        serviceCommand.send();
        return serviceCommand;
    }


    protected ServiceCommand<Launcher.AppInfoListener> getRunningApp(boolean isSubscription, final Launcher.AppInfoListener listener) {
        ServiceCommand<Launcher.AppInfoListener> request;

        ResponseListener<Object> responseListener = new ResponseListener<Object>() {

            @Override
            public void onSuccess(Object response) {
                final JSONObject jsonObj = (JSONObject) response;
                AppInfo app = new AppInfo() {{
                    setId(jsonObj.optString("appId"));
                    setName(jsonObj.optString("appName"));
                    setRawData(jsonObj);
                }};

                Util.postSuccess(listener, app);
            }

            @Override
            public void onError(ServiceCommandError error) {
                Util.postError(listener, error);
            }
        };
        if (isSubscription)
            request = new URLServiceSubscription<Launcher.AppInfoListener>(this, FOREGROUND_APP, null, true, responseListener);
        else
            request = new ServiceCommand<Launcher.AppInfoListener>(this, FOREGROUND_APP, null, true, responseListener);

        request.send();

        return request;
    }

    public ServiceCommand<Launcher.AppStateListener> getAppState(boolean z, LaunchSession launchSession, final Launcher.AppStateListener appStateListener) {
        ServiceCommand<Launcher.AppStateListener> serviceCommand;
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("id", launchSession.getAppId());
            jSONObject.put("sessionId", launchSession.getSessionId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ResponseListener r5 = new ResponseListener<Object>() {

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(appStateListener, serviceCommandError);
            }

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                JSONObject jSONObject = (JSONObject) obj;
                try {
                    Util.postSuccess(appStateListener, new Launcher.AppState(jSONObject.getBoolean("running"), jSONObject.getBoolean("visible")));
                } catch (JSONException e) {
                    Util.postError(appStateListener, new ServiceCommandError(0, "Malformed JSONObject", null));
                    e.printStackTrace();
                }
            }
        };
        if (z) {
            serviceCommand = new URLServiceSubscription<>(this, APP_STATE, jSONObject, true, r5);
        } else {
            serviceCommand = new ServiceCommand<>(this, APP_STATE, jSONObject, true, r5);
        }
        serviceCommand.send();
        return serviceCommand;
    }

    public void sendToast(JSONObject jSONObject, ResponseListener<Object> responseListener) {
        if (!jSONObject.has("iconData")) {
            Context context = DiscoveryManager.getInstance().getContext();
            try {
                Drawable applicationIcon = context.getPackageManager().getApplicationIcon(context.getPackageName());
                if (applicationIcon != null) {
                    Bitmap bitmap = ((BitmapDrawable) applicationIcon).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    jSONObject.put("iconData", new String(Base64.encode(byteArrayOutputStream.toByteArray(), 2)));
                    jSONObject.put("iconExtension", "png");
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        new ServiceCommand(this, "palm://system.notifications/createToast", jSONObject, true, responseListener).send();
    }

    public ServiceCommand<VolumeControl.VolumeListener> getVolume(boolean z, final VolumeControl.VolumeListener volumeListener) {
        ServiceCommand<VolumeControl.VolumeListener> serviceCommand;
        ResponseListener r5 = new ResponseListener<Object>() {

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
                try {
                    JSONObject jSONObject = (JSONObject) obj;
                    int i = 0;
                    if (jSONObject.has("volume")) {
                        i = ((Integer) jSONObject.get("volume")).intValue();
                    } else if (jSONObject.has("volumeStatus")) {
                        i = ((Integer) jSONObject.getJSONObject("volumeStatus").get("volume")).intValue();
                    }
                    double d = (double) i;
                    Double.isNaN(d);
                    Util.postSuccess(volumeListener, Float.valueOf((float) (d / 100.0d)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
                Util.postError(volumeListener, serviceCommandError);
            }
        };
        if (z) {
            serviceCommand = new URLServiceSubscription<>(this, VOLUME, null, true, r5);
        } else {
            serviceCommand = new ServiceCommand<>(this, VOLUME, null, true, r5);
        }
        serviceCommand.send();
        return serviceCommand;
    }

    @Override // com.connectsdk.service.capability.PowerControl
    public CapabilityPriorityLevel getPowerControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.PowerControl
    public void powerOff(ResponseListener<Object> responseListener) {
        new ServiceCommand(this, "ssap://system/turnOff", null, true, new ResponseListener<Object>() {
            /* class com.connectsdk.service.webos.WebOSTVDeviceService.AnonymousClass11 */

            @Override // com.connectsdk.service.capability.listeners.ErrorListener
            public void onError(ServiceCommandError serviceCommandError) {
            }

            @Override // com.connectsdk.service.capability.listeners.ResponseListener
            public void onSuccess(Object obj) {
            }
        }).send();
    }

    @Override // com.connectsdk.service.capability.PowerControl
    public void powerOn(ResponseListener<Object> responseListener) {
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public CapabilityPriorityLevel getMediaControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void play(ResponseListener<Object> responseListener) {
        new ServiceCommand(this, "ssap://media.controls/play", null, true, responseListener).send();
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void pause(ResponseListener<Object> responseListener) {
        new ServiceCommand(this, "ssap://media.controls/pause", null, true, responseListener).send();
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void stop(ResponseListener<Object> responseListener) {
        new ServiceCommand(this, "ssap://media.controls/stop", null, true, responseListener).send();
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void rewind(ResponseListener<Object> responseListener) {
        new ServiceCommand(this, "ssap://media.controls/rewind", null, true, responseListener).send();
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void fastForward(ResponseListener<Object> responseListener) {
        new ServiceCommand(this, "ssap://media.controls/fastForward", null, true, responseListener).send();
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void previous(ResponseListener<Object> responseListener) {
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void next(ResponseListener<Object> responseListener) {
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void seek(long j, ResponseListener<Object> responseListener) {
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getDuration(DurationListener durationListener) {
        Util.postError(durationListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getPosition(PositionListener positionListener) {
        Util.postError(positionListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getPlayState(PlayStateListener playStateListener) {
        Util.postError(playStateListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public ServiceSubscription<PlayStateListener> subscribePlayState(PlayStateListener playStateListener) {
        Util.postError(playStateListener, ServiceCommandError.notSupported());
        return null;
    }
}

//
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.util.Base64;
//
//import com.connectsdk.core.AppInfo;
//import com.connectsdk.core.ChannelInfo;
//import com.connectsdk.core.ProgramInfo;
//import com.connectsdk.core.ProgramList;
//import com.connectsdk.core.Util;
//import com.connectsdk.device.ConnectableDevice;
//import com.connectsdk.discovery.DiscoveryManager;
//import com.connectsdk.service.DeviceService;
//import com.connectsdk.service.capability.Launcher;
//import com.connectsdk.service.capability.MediaControl;
//import com.connectsdk.service.capability.PowerControl;
//import com.connectsdk.service.capability.TVControl;
//import com.connectsdk.service.capability.VolumeControl;
//import com.connectsdk.service.capability.listeners.ResponseListener;
//import com.connectsdk.service.command.ServiceCommand;
//import com.connectsdk.service.command.ServiceCommandError;
//import com.connectsdk.service.command.ServiceSubscription;
//import com.connectsdk.service.command.URLServiceSubscription;
//import com.connectsdk.service.config.ServiceConfig;
//import com.connectsdk.service.config.ServiceDescription;
//import com.connectsdk.service.sessions.LaunchSession;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.util.ArrayList;
//import java.util.Map;
//
//public class WebOSTVDeviceService extends DeviceService implements PowerControl, MediaControl {
//
//    static String MUTE = "ssap://audio/getMute";
//    static String VOLUME_STATUS = "ssap://audio/getStatus";
//    static String CHANNEL = "ssap://remote/getCurrentChannel";
//    static String CHANNEL_LIST = "ssap://remote/getChannelList";
//    static String PROGRAM = "ssap://remote/getChannelProgramInfo";
//    static String FOREGROUND_APP = "ssap://com.webos.applicationManager/getForegroundAppInfo";
//    static String APP_STATE = "ssap://system.launcher/getAppState";
//    static String VOLUME = "ssap://audio/getVolume";
//
//    public interface WebOSTVServicePermission {
//        public enum Open implements WebOSTVServicePermission {
//            LAUNCH,
//            LAUNCH_WEB,
//            APP_TO_APP,
//            CONTROL_AUDIO,
//            CONTROL_INPUT_MEDIA_PLAYBACK
//        }
//
//        public static final WebOSTVServicePermission[] OPEN = {
//                Open.LAUNCH,
//                Open.LAUNCH_WEB,
//                Open.APP_TO_APP,
//                Open.CONTROL_AUDIO,
//                Open.CONTROL_INPUT_MEDIA_PLAYBACK
//        };
//
//        public enum Protected implements WebOSTVServicePermission {
//            CONTROL_POWER,
//            READ_INSTALLED_APPS,
//            CONTROL_DISPLAY,
//            CONTROL_INPUT_JOYSTICK,
//            CONTROL_INPUT_MEDIA_RECORDING,
//            CONTROL_INPUT_TV,
//            READ_INPUT_DEVICE_LIST,
//            READ_NETWORK_STATE,
//            READ_TV_CHANNEL_LIST,
//            WRITE_NOTIFICATION_TOAST,
//            CONTROL_BLUETOOTH,
//            CHECK_BLUETOOTH_DEVICE,
//            CONTROL_TV_SCREEN
//        }
//
//        public static final WebOSTVServicePermission[] PROTECTED = {
//                Protected.CONTROL_POWER,
//                Protected.READ_INSTALLED_APPS,
//                Protected.CONTROL_DISPLAY,
//                Protected.CONTROL_INPUT_JOYSTICK,
//                Protected.CONTROL_INPUT_MEDIA_RECORDING,
//                Protected.CONTROL_INPUT_TV,
//                Protected.READ_INPUT_DEVICE_LIST,
//                Protected.READ_NETWORK_STATE,
//                Protected.READ_TV_CHANNEL_LIST,
//                Protected.WRITE_NOTIFICATION_TOAST,
//                Protected.CONTROL_BLUETOOTH,
//                Protected.CHECK_BLUETOOTH_DEVICE,
//                Protected.CONTROL_TV_SCREEN
//        };
//
//        public enum PersonalActivity implements WebOSTVServicePermission {
//            CONTROL_INPUT_TEXT,
//            CONTROL_MOUSE_AND_KEYBOARD,
//            READ_CURRENT_CHANNEL,
//            READ_RUNNING_APPS
//        }
//
//        public static final WebOSTVServicePermission[] PERSONAL_ACTIVITY = {
//                PersonalActivity.CONTROL_INPUT_TEXT,
//                PersonalActivity.CONTROL_MOUSE_AND_KEYBOARD,
//                PersonalActivity.READ_CURRENT_CHANNEL,
//                PersonalActivity.READ_RUNNING_APPS
//        };
//    }
//
//    public final static String[] kWebOSTVServiceOpenPermissions = {
//            "LAUNCH",
//            "LAUNCH_WEBAPP",
//            "APP_TO_APP",
//            "CONTROL_AUDIO",
//            "CONTROL_INPUT_MEDIA_PLAYBACK",
//            "UPDATE_FROM_REMOTE_APP"
//    };
//
//    public final static String[] kWebOSTVServiceProtectedPermissions = {
//            "CONTROL_POWER",
//            "READ_INSTALLED_APPS",
//            "CONTROL_DISPLAY",
//            "CONTROL_INPUT_JOYSTICK",
//            "CONTROL_INPUT_MEDIA_RECORDING",
//            "CONTROL_INPUT_TV",
//            "READ_INPUT_DEVICE_LIST",
//            "READ_NETWORK_STATE",
//            "READ_TV_CHANNEL_LIST",
//            "WRITE_NOTIFICATION_TOAST",
//            "CONTROL_BLUETOOTH",
//            "CHECK_BLUETOOTH_DEVICE",
//            "CONTROL_USER_INFO",
//            "CONTROL_TIMER_INFO",
//            "READ_SETTINGS",
//            "CONTROL_TV_SCREEN"
//    };
//
//    public final static String[] kWebOSTVServicePersonalActivityPermissions = {
//            "CONTROL_INPUT_TEXT",
//            "CONTROL_MOUSE_AND_KEYBOARD",
//            "READ_CURRENT_CHANNEL",
//            "READ_RUNNING_APPS"
//    };
//
//    protected DeviceService.PairingType pairingType = PairingType.PIN_CODE;
//
//    protected DeviceService getDLNAService() {
//        Map<String, ConnectableDevice> allDevices = DiscoveryManager.getInstance().getAllDevices();
//        ConnectableDevice device = null;
//        DeviceService service = null;
//
//        if (allDevices != null && allDevices.size() > 0)
//            device = allDevices.get(this.serviceDescription.getIpAddress());
//
//        if (device != null)
//            service = device.getServiceByName("DLNA");
//
//        return service;
//    }
//
//    public WebOSTVDeviceService(ServiceDescription serviceDescription, ServiceConfig serviceConfig) {
//        super(serviceDescription, serviceConfig);
//        this.serviceDescription = serviceDescription;
//        this.serviceConfig=serviceConfig;
//    }
//
//    public ServiceCommand<ResponseListener<Object>> getCurrentSWInfo(final ResponseListener<Object> listener) {
//        String uri = "ssap://com.webos.service.update/getCurrentSWInformation";
//
//        ServiceCommand<ResponseListener<Object>> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                JSONObject jsonObj = (JSONObject) response;
//                Util.postSuccess(listener, (JSONObject) response);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        request = new ServiceCommand<ResponseListener<Object>>(this, uri, null, true, responseListener);
//        request.send();
//
//        return request;
//    }
//
//    public void setPairingType(PairingType pairingType) {
//        this.pairingType = pairingType;
//    }
//
//    protected ServiceCommand<ResponseListener<Object>> getMuteStatus(boolean isSubscription, final VolumeControl.MuteListener listener) {
//        ServiceCommand<ResponseListener<Object>> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                try {
//                    JSONObject jsonObj = (JSONObject)response;
//                    boolean isMute = (Boolean) jsonObj.get("mute");
//                    Util.postSuccess(listener, isMute);
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
//        if (isSubscription)
//            request = new URLServiceSubscription<ResponseListener<Object>>(this, MUTE, null, true, responseListener);
//        else
//            request = new ServiceCommand<ResponseListener<Object>>(this, MUTE, null, true, responseListener);
//
//        request.send();
//
//        return request;
//    }
//
//    protected ServiceCommand<ResponseListener<Object>> getVolumeStatus(boolean isSubscription, final VolumeControl.VolumeStatusListener listener) {
//        ServiceCommand<ResponseListener<Object>> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                try {
//                    JSONObject jsonObj = (JSONObject) response;
//                    boolean isMute = (Boolean) jsonObj.get("mute");
//                    int iVolume = 0;
//                    if (jsonObj.has("volume"))
//                    {
//                        iVolume = (Integer) jsonObj.get("volume");
//                    }
//                    else if (jsonObj.has("volumeStatus"))
//                    {
//                        iVolume = (Integer) (jsonObj.getJSONObject("volumeStatus")).get("volume");
//                    }
//                    float fVolume = (float) (iVolume / 100.0);
//
//                    Util.postSuccess(listener, new VolumeControl.VolumeStatus(isMute, fVolume));
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
//        if (isSubscription)
//            request = new URLServiceSubscription<ResponseListener<Object>>(this, VOLUME_STATUS, null, true, responseListener);
//        else
//            request = new ServiceCommand<ResponseListener<Object>>(this, VOLUME_STATUS, null, true, responseListener);
//
//        request.send();
//
//        return request;
//    }
//
//    protected ProgramInfo parseRawProgramInfo(JSONObject programRawData) {
//        String programId;
//        String programName;
//
//        ProgramInfo programInfo = new ProgramInfo();
//        programInfo.setRawData(programRawData);
//
//        programId = programRawData.optString("programId");
//        programName = programRawData.optString("programName");
//        ChannelInfo channelInfo = parseRawChannelData(programRawData);
//
//        programInfo.setId(programId);
//        programInfo.setName(programName);
//        programInfo.setChannelInfo(channelInfo);
//
//        return programInfo;
//    }
//
//    protected ChannelInfo parseRawChannelData(JSONObject channelRawData) {
//        String channelName = null;
//        String channelId = null;
//        String channelNumber = null;
//        int minorNumber;
//        int majorNumber;
//
//        ChannelInfo channelInfo = new ChannelInfo();
//        channelInfo.setRawData(channelRawData);
//
//        try {
//            if (!channelRawData.isNull("channelName"))
//                channelName = (String) channelRawData.get("channelName");
//
//            if (!channelRawData.isNull("channelId"))
//                channelId = (String) channelRawData.get("channelId");
//
//            channelNumber = channelRawData.optString("channelNumber");
//
//            if (!channelRawData.isNull("majorNumber"))
//                majorNumber = (Integer) channelRawData.get("majorNumber");
//            else
//                majorNumber = parseMajorNumber(channelNumber);
//
//            if (!channelRawData.isNull("minorNumber"))
//                minorNumber = (Integer) channelRawData.get("minorNumber");
//            else
//                minorNumber = parseMinorNumber(channelNumber);
//
//            channelInfo.setName(channelName);
//            channelInfo.setId(channelId);
//            channelInfo.setNumber(channelNumber);
//            channelInfo.setMajorNumber(majorNumber);
//            channelInfo.setMinorNumber(minorNumber);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return channelInfo;
//    }
//
//    protected int parseMinorNumber(String channelNumber) {
//        if (channelNumber != null && !channelNumber.isEmpty()) {
//            String tokens[] = channelNumber.split("-");
//            return Integer.valueOf(tokens[tokens.length-1]);
//        }
//        else
//            return 0;
//    }
//
//    protected int parseMajorNumber(String channelNumber) {
//        if (channelNumber != null && !channelNumber.isEmpty()) {
//            String tokens[] = channelNumber.split("-");
//            return Integer.valueOf(tokens[0]);
//        }
//        else
//            return 0;
//    }
//
//    protected ServiceCommand<ResponseListener<Object>> getCurrentChannel(boolean isSubscription, final TVControl.ChannelListener listener) {
//        ServiceCommand<ResponseListener<Object>> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                JSONObject jsonObj = (JSONObject) response;
//                ChannelInfo channel = parseRawChannelData(jsonObj);
//
//                Util.postSuccess(listener, channel);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        if (isSubscription) {
//            request = new URLServiceSubscription<ResponseListener<Object>>(this, CHANNEL, null, true, responseListener);
//        }
//        else
//            request = new ServiceCommand<ResponseListener<Object>>(this, CHANNEL, null, true, responseListener);
//
//        request.send();
//
//        return request;
//    }
//
//    protected ServiceCommand<ResponseListener<Object>> getChannelList(boolean isSubscription, final TVControl.ChannelListListener listener) {
//        ServiceCommand<ResponseListener<Object>> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                try {
//                    JSONObject jsonObj = (JSONObject)response;
//                    ArrayList<ChannelInfo> list = new ArrayList<ChannelInfo>();
//
//                    JSONArray array = (JSONArray) jsonObj.get("channelList");
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject object = (JSONObject) array.get(i);
//
//                        ChannelInfo channel = parseRawChannelData(object);
//                        list.add(channel);
//                    }
//
//                    Util.postSuccess(listener, list);
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
//        if (isSubscription)
//            request = new URLServiceSubscription<ResponseListener<Object>>(this, CHANNEL_LIST, null, true, responseListener);
//        else
//            request = new ServiceCommand<ResponseListener<Object>>(this, CHANNEL_LIST, null, true, responseListener);
//
//        request.send();
//
//        return request;
//    }
//
//    protected ServiceCommand<ResponseListener<Object>> getChannelCurrentProgramInfo(boolean isSubscription, final TVControl.ProgramInfoListener listener) {
//        String uri ="ssap://remote/getChannelCurrentProgramInfo";
//
//        ServiceCommand<ResponseListener<Object>> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                JSONObject jsonObj = (JSONObject)response;
//                ProgramInfo programInfo = parseRawProgramInfo(jsonObj);
//
//                Util.postSuccess(listener, programInfo);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//
//        if (isSubscription)
//            request = new URLServiceSubscription<ResponseListener<Object>>(this, uri, null, true, responseListener);
//        else
//            request = new ServiceCommand<ResponseListener<Object>>(this, uri, null, true, responseListener);
//
//        request.send();
//
//        return request;
//    }
//
//    protected ServiceCommand<ResponseListener<Object>> getProgramList(boolean isSubscription, final TVControl.ProgramListListener listener) {
//        ServiceCommand<ResponseListener<Object>> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                try {
//                    JSONObject jsonObj = (JSONObject) response;
//                    JSONObject jsonChannel = (JSONObject) jsonObj.get("channel");
//                    ChannelInfo channel = parseRawChannelData(jsonChannel);
//                    JSONArray programList = (JSONArray) jsonObj.get("programList");
//
//                    Util.postSuccess(listener, new ProgramList(channel, programList));
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
//        if (isSubscription)
//            request = new URLServiceSubscription<ResponseListener<Object>>(this, PROGRAM, null, true, responseListener);
//        else
//            request = new ServiceCommand<ResponseListener<Object>>(this, PROGRAM, null, true, responseListener);
//
//        request.send();
//
//        return request;
//    }
//
//    protected ServiceCommand<Launcher.AppInfoListener> getRunningApp(boolean isSubscription, final Launcher.AppInfoListener listener) {
//        ServiceCommand<Launcher.AppInfoListener> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//                final JSONObject jsonObj = (JSONObject) response;
//                AppInfo app = new AppInfo() {{
//                    setId(jsonObj.optString("appId"));
//                    setName(jsonObj.optString("appName"));
//                    setRawData(jsonObj);
//                }};
//
//                Util.postSuccess(listener, app);
//            }
//
//            @Override
//            public void onError(ServiceCommandError error) {
//                Util.postError(listener, error);
//            }
//        };
//        if (isSubscription)
//            request = new URLServiceSubscription<Launcher.AppInfoListener>(this, FOREGROUND_APP, null, true, responseListener);
//        else
//            request = new ServiceCommand<Launcher.AppInfoListener>(this, FOREGROUND_APP, null, true, responseListener);
//
//        request.send();
//
//        return request;
//    }
//
//    protected ServiceCommand<Launcher.AppStateListener> getAppState(boolean subscription, LaunchSession launchSession, final Launcher.AppStateListener listener) {
//        ServiceCommand<Launcher.AppStateListener> request;
//        JSONObject params = new JSONObject();
//
//        try {
////            params.put("appId", launchSession.getAppId());
//            params.put("id", launchSession.getAppId());
//            params.put("sessionId", launchSession.getSessionId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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
//                JSONObject json = (JSONObject) object;
//                try {
//                    Util.postSuccess(listener, new Launcher.AppState(json.getBoolean("running"), json.getBoolean("visible")));
//                } catch (JSONException e) {
//                    Util.postError(listener, new ServiceCommandError(0, "Malformed JSONObject", null));
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        if (subscription) {
//            request = new URLServiceSubscription<Launcher.AppStateListener>(this, APP_STATE, params, true, responseListener);
//        } else {
//            request = new ServiceCommand<Launcher.AppStateListener>(this, APP_STATE, params, true, responseListener);
//        }
//
//        request.send();
//
//        return request;
//    }
//
//    protected void sendToast(JSONObject payload, ResponseListener<Object> listener) {
//        if (!payload.has("iconData"))
//        {
//            Context context = DiscoveryManager.getInstance().getContext();
//
//            try {
//                Drawable drawable = context.getPackageManager().getApplicationIcon(context.getPackageName());
//
//                if(drawable != null) {
//                    BitmapDrawable bitDw = ((BitmapDrawable) drawable);
//                    Bitmap bitmap = bitDw.getBitmap();
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//
//                    byte[] bitmapByte = stream.toByteArray();
//                    bitmapByte = Base64.encode(bitmapByte,Base64.NO_WRAP);
//                    String bitmapData = new String(bitmapByte);
//
//                    payload.put("iconData", bitmapData);
//                    payload.put("iconExtension", "png");
//                }
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        String uri = "palm://system.notifications/createToast";
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, uri, payload, true, listener);
//        request.send();
//    }
//
//    protected ServiceCommand<VolumeControl.VolumeListener> getVolume(boolean isSubscription, final VolumeControl.VolumeListener listener) {
//        ServiceCommand<VolumeControl.VolumeListener> request;
//
//        ResponseListener<Object> responseListener = new ResponseListener<Object>() {
//
//            @Override
//            public void onSuccess(Object response) {
//
//                try {
//                    JSONObject jsonObj = (JSONObject)response;
//                    int iVolume = 0;
//                    if (jsonObj.has("volume"))
//                    {
//                        iVolume = (Integer) jsonObj.get("volume");
//                    }
//                    else if (jsonObj.has("volumeStatus"))
//                    {
//                        iVolume = (Integer) (jsonObj.getJSONObject("volumeStatus")).get("volume");
//                    }
//
//                    float fVolume = (float) (iVolume / 100.0);
//
//                    Util.postSuccess(listener, fVolume);
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
//        if (isSubscription)
//            request = new URLServiceSubscription<VolumeControl.VolumeListener>(this, VOLUME, null, true, responseListener);
//        else
//            request = new ServiceCommand<VolumeControl.VolumeListener>(this, VOLUME, null, true, responseListener);
//
//        request.send();
//
//        return request;
//    }
//
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
//
//            }
//        };
//
//        String uri = "ssap://system/turnOff";
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, uri, null, true, responseListener);
//
//        request.send();
//    }
//
//    @Override
//    public void powerOn(ResponseListener<Object> listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public MediaControl getMediaControl() {
//        return null;
//    }
//
//    @Override
//    public CapabilityPriorityLevel getMediaControlCapabilityLevel() {
//        return CapabilityPriorityLevel.HIGH;
//    }
//
//    @Override
//    public void play(ResponseListener<Object> listener) {
//        String uri = "ssap://media.controls/play";
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, uri, null, true, listener);
//
//        request.send();
//    }
//
//    @Override
//    public void pause(ResponseListener<Object> listener) {
//        String uri = "ssap://media.controls/pause";
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, uri, null, true, listener);
//
//        request.send();
//    }
//
//    @Override
//    public void stop(ResponseListener<Object> listener) {
//        String uri = "ssap://media.controls/stop";
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, uri, null, true, listener);
//
//        request.send();
//    }
//
//    @Override
//    public void rewind(ResponseListener<Object> listener) {
//        String uri = "ssap://media.controls/rewind";
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, uri, null, true, listener);
//
//        request.send();
//    }
//
//    @Override
//    public void fastForward(ResponseListener<Object> listener) {
//        String uri = "ssap://media.controls/fastForward";
//        ServiceCommand<ResponseListener<Object>> request = new ServiceCommand<ResponseListener<Object>>(this, uri, null, true, listener);
//
//        request.send();
//    }
//
//    @Override
//    public void previous(ResponseListener<Object> listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void next(ResponseListener<Object> listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void seek(long position, ResponseListener<Object> listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void getDuration(DurationListener listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void getPosition(PositionListener listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public void getPlayState(PlayStateListener listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//    }
//
//    @Override
//    public ServiceSubscription<PlayStateListener> subscribePlayState(PlayStateListener listener) {
//        Util.postError(listener, ServiceCommandError.notSupported());
//
//        return null;
//    }
//}

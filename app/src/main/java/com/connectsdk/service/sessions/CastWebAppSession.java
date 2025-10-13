package com.connectsdk.service.sessions;

import android.util.Log;

import com.connectsdk.core.Util;
import com.connectsdk.service.CastService;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.URLServiceSubscription;
import com.connectsdk.service.google_cast.CastServiceChannel;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.IOException;

public class CastWebAppSession extends WebAppSession {
    private CastServiceChannel castServiceChannel;
    private ApplicationMetadata metadata;
    private CastService service;

    @Override // com.connectsdk.service.sessions.WebAppSession, com.connectsdk.service.capability.MediaPlayer
    public MediaPlayer getMediaPlayer() {
        return this;
    }

    public CastWebAppSession(LaunchSession launchSession, DeviceService deviceService) {
        super(launchSession, deviceService);
        this.service = (CastService) deviceService;
    }

    @Override // com.connectsdk.service.sessions.WebAppSession
    public void connect(ResponseListener<Object> responseListener) {
        if (this.castServiceChannel != null) {
            disconnectFromWebApp();
        }
        this.castServiceChannel = new CastServiceChannel(this.launchSession.getAppId(), this);
        try {
            Cast.CastApi.setMessageReceivedCallbacks(this.service.getApiClient(), this.castServiceChannel.getNamespace(), this.castServiceChannel);
            Util.postSuccess(responseListener, null);
        } catch (IOException unused) {
            this.castServiceChannel = null;
            Util.postError(responseListener, new ServiceCommandError(0, "Failed to create channel", null));
        }
    }

    @Override // com.connectsdk.service.sessions.WebAppSession
    public void join(ResponseListener<Object> responseListener) {
        connect(responseListener);
    }

    @Override // com.connectsdk.service.sessions.WebAppSession
    public void disconnectFromWebApp() {
        if (this.castServiceChannel != null) {
            try {
                Cast.CastApi.removeMessageReceivedCallbacks(this.service.getApiClient(), this.castServiceChannel.getNamespace());
                this.castServiceChannel = null;
            } catch (IOException e) {
                Log.e(Util.T, "Exception while removing application", e);
            }
            Cast.CastApi.leaveApplication(this.service.getApiClient());
        }
    }

    public void handleAppClose() {
        for (URLServiceSubscription<?> uRLServiceSubscription : this.service.getSubscriptions()) {
            if (uRLServiceSubscription.getTarget().equalsIgnoreCase(CastService.PLAY_STATE)) {
                for (int i = 0; i < uRLServiceSubscription.getListeners().size(); i++) {
                    Util.postSuccess((ResponseListener) uRLServiceSubscription.getListeners().get(i), PlayStateStatus.Idle);
                }
            }
        }
        if (getWebAppSessionListener() != null) {
            getWebAppSessionListener().onWebAppSessionDisconnect(this);
        }
    }

    @Override // com.connectsdk.service.sessions.WebAppSession
    public void sendMessage(String str, final ResponseListener<Object> responseListener) {
        if (str == null) {
            Util.postError(responseListener, new ServiceCommandError(0, "Cannot send null message", null));
        } else if (this.castServiceChannel == null) {
            Util.postError(responseListener, new ServiceCommandError(0, "Cannot send a message to the web app without first connecting", null));
        } else {
            Cast.CastApi.sendMessage(this.service.getApiClient(), this.castServiceChannel.getNamespace(), str).setResultCallback(new ResultCallback<Status>() {
                /* class com.connectsdk.service.sessions.CastWebAppSession.AnonymousClass1 */

                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Util.postSuccess(responseListener, null);
                    } else {
                        Util.postError(responseListener, new ServiceCommandError(status.getStatusCode(), status.toString(), status));
                    }
                }
            });
        }
    }

    @Override // com.connectsdk.service.sessions.WebAppSession
    public void sendMessage(JSONObject jSONObject, ResponseListener<Object> responseListener) {
        sendMessage(jSONObject.toString(), responseListener);
    }

    @Override // com.connectsdk.service.sessions.WebAppSession
    public void close(ResponseListener<Object> responseListener) {
        this.launchSession.close(responseListener);
    }

    @Override // com.connectsdk.service.sessions.WebAppSession, com.connectsdk.service.capability.MediaPlayer
    public CapabilityPriorityLevel getMediaPlayerCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.sessions.WebAppSession, com.connectsdk.service.capability.MediaPlayer
    public void playMedia(String str, String str2, String str3, String str4, String str5, boolean z, MediaPlayer.LaunchListener launchListener) {
        this.service.playMedia(str, str2, str3, str4, str5, z, launchListener);
    }

    @Override // com.connectsdk.service.sessions.WebAppSession, com.connectsdk.service.capability.MediaPlayer
    public void closeMedia(LaunchSession launchSession, ResponseListener<Object> responseListener) {
        close(responseListener);
    }

    public ApplicationMetadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(ApplicationMetadata applicationMetadata) {
        this.metadata = applicationMetadata;
    }
}

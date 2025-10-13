package com.connectsdk.service;

import com.amazon.whisperplay.fling.media.controller.RemoteMediaPlayer;
import com.amazon.whisperplay.fling.media.service.CustomMediaPlayer;
import com.amazon.whisperplay.fling.media.service.MediaPlayerInfo;
import com.amazon.whisperplay.fling.media.service.MediaPlayerStatus;
import com.connectsdk.core.ImageInfo;
import com.connectsdk.core.MediaInfo;
import com.connectsdk.core.Util;
import com.connectsdk.discovery.DiscoveryFilter;
import com.connectsdk.service.capability.CapabilityMethods;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.FireTVServiceError;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.connectsdk.service.config.ServiceConfig;
import com.connectsdk.service.config.ServiceDescription;
import com.connectsdk.service.sessions.LaunchSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FireTVService extends DeviceService implements MediaPlayer, MediaControl {
    public static final String ID = "FireTV";
    private static final String META_DESCRIPTION = "description";
    private static final String META_ICON_IMAGE = "poster";
    private static final String META_KIND = "kind";
    private static final String META_LABEL = "label";
    private static final String META_MIME_TYPE = "type";
    private static final String META_NOREPLAY = "noreplay";
    private static final String META_SRC = "src";
    private static final String META_SRCLANG = "srclang";
    private static final String META_TITLE = "title";
    private static final String META_TRACKS = "tracks";
    private PlayStateSubscription playStateSubscription;
    private final RemoteMediaPlayer remoteMediaPlayer;

    /* access modifiers changed from: private */
    public interface ConvertResult<Response, Result> {
        Response convert(Result result) throws Exception;
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public MediaControl getMediaControl() {
        return this;
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public MediaPlayer getMediaPlayer() {
        return this;
    }

    @Override // com.connectsdk.service.DeviceService
    public boolean isConnectable() {
        return true;
    }

    public FireTVService(ServiceDescription serviceDescription, ServiceConfig serviceConfig) {
        super(serviceDescription, serviceConfig);
        if (serviceDescription == null || !(serviceDescription.getDevice() instanceof RemoteMediaPlayer)) {
            this.remoteMediaPlayer = null;
        } else {
            this.remoteMediaPlayer = (RemoteMediaPlayer) serviceDescription.getDevice();
        }
    }

    public static DiscoveryFilter discoveryFilter() {
        return new DiscoveryFilter(ID, ID);
    }

    @Override // com.connectsdk.service.DeviceService
    public void connect() {
        super.connect();
        if (this.remoteMediaPlayer != null) {
            this.connected = true;
            reportConnected(this.connected);
        }
    }

    @Override // com.connectsdk.service.DeviceService
    public boolean isConnected() {
        return this.connected;
    }

    @Override // com.connectsdk.service.DeviceService
    public void disconnect() {
        super.disconnect();
        PlayStateSubscription playStateSubscription2 = this.playStateSubscription;
        if (playStateSubscription2 != null) {
            playStateSubscription2.unsubscribe();
            this.playStateSubscription = null;
        }
        this.connected = false;
    }

    /* access modifiers changed from: protected */
    @Override // com.connectsdk.service.DeviceService
    public void updateCapabilities() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(MediaPlayer.MediaInfo_Get);
        arrayList.add(MediaPlayer.Display_Image);
        arrayList.add("MediaPlayer.Play.Audio");
        arrayList.add("MediaPlayer.Play.Video");
        arrayList.add(MediaPlayer.Close);
        arrayList.add(MediaPlayer.MetaData_MimeType);
        arrayList.add(MediaPlayer.MetaData_Thumbnail);
        arrayList.add(MediaPlayer.MetaData_Title);
        arrayList.add(MediaPlayer.Subtitle_WebVTT);
        arrayList.add(MediaControl.Play);
        arrayList.add(MediaControl.Pause);
        arrayList.add(MediaControl.Stop);
        arrayList.add(MediaControl.Seek);
        arrayList.add(MediaControl.Duration);
        arrayList.add(MediaControl.Position);
        arrayList.add(MediaControl.PlayState);
        arrayList.add(MediaControl.PlayState_Subscribe);
        setCapabilities(arrayList);
    }

    @Override // com.connectsdk.service.DeviceService
    public CapabilityPriorityLevel getPriorityLevel(Class<? extends CapabilityMethods> cls) {
        if (cls != null) {
            if (cls.equals(MediaPlayer.class)) {
                return getMediaPlayerCapabilityLevel();
            }
            if (cls.equals(MediaControl.class)) {
                return getMediaControlCapabilityLevel();
            }
        }
        return CapabilityPriorityLevel.NOT_SUPPORTED;
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public CapabilityPriorityLevel getMediaPlayerCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void getMediaInfo(MediaInfoListener mediaInfoListener) {
        try {
            handleAsyncFutureWithConversion(mediaInfoListener, this.remoteMediaPlayer.getMediaInfo(), new ConvertResult<MediaInfo, MediaPlayerInfo>() {
                /* class com.connectsdk.service.FireTVService.AnonymousClass1 */

                public MediaInfo convert(MediaPlayerInfo mediaPlayerInfo) throws JSONException {
                    ArrayList arrayList;
                    JSONObject jSONObject = new JSONObject(mediaPlayerInfo.getMetadata());
                    if (jSONObject.has(FireTVService.META_ICON_IMAGE)) {
                        arrayList = new ArrayList();
                        arrayList.add(new ImageInfo(jSONObject.getString(FireTVService.META_ICON_IMAGE)));
                    } else {
                        arrayList = null;
                    }
                    return new MediaInfo(mediaPlayerInfo.getSource(), jSONObject.getString("type"), jSONObject.getString("title"), jSONObject.getString("description"), arrayList);
                }
            }, "Error getting media info");
        } catch (Exception unused) {
            Util.postError(mediaInfoListener, new FireTVServiceError("Error getting media info"));
        }
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public ServiceSubscription<MediaInfoListener> subscribeMediaInfo(MediaInfoListener mediaInfoListener) {
        Util.postError(mediaInfoListener, ServiceCommandError.notSupported());
        return null;
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void displayImage(String str, String str2, String str3, String str4, String str5, LaunchListener launchListener) {
        setMediaSource(new MediaInfo.Builder(str, str2).setTitle(str3).setDescription(str4).setIcon(str5).build(), launchListener);
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void playMedia(String str, String str2, String str3, String str4, String str5, boolean z, LaunchListener launchListener) {
        setMediaSource(new MediaInfo.Builder(str, str2).setTitle(str3).setDescription(str4).setIcon(str5).build(), launchListener);
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void closeMedia(LaunchSession launchSession, ResponseListener<Object> responseListener) {
        stop(responseListener);
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void displayImage(MediaInfo mediaInfo, LaunchListener launchListener) {
        setMediaSource(mediaInfo, launchListener);
    }

    @Override // com.connectsdk.service.capability.MediaPlayer
    public void playMedia(MediaInfo mediaInfo, boolean z, LaunchListener launchListener) {
        setMediaSource(mediaInfo, launchListener);
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public CapabilityPriorityLevel getMediaControlCapabilityLevel() {
        return CapabilityPriorityLevel.HIGH;
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void play(ResponseListener<Object> responseListener) {
        try {
            handleVoidAsyncFuture(responseListener, this.remoteMediaPlayer.play(), "Error playing");
        } catch (Exception e) {
            Util.postError(responseListener, new FireTVServiceError("Error playing", e));
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void pause(ResponseListener<Object> responseListener) {
        try {
            handleVoidAsyncFuture(responseListener, this.remoteMediaPlayer.pause(), "Error pausing");
        } catch (Exception e) {
            Util.postError(responseListener, new FireTVServiceError("Error pausing", e));
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void stop(ResponseListener<Object> responseListener) {
        try {
            handleVoidAsyncFuture(responseListener, this.remoteMediaPlayer.stop(), "Error stopping");
        } catch (Exception e) {
            Util.postError(responseListener, new FireTVServiceError("Error stopping", e));
        }
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
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void next(ResponseListener<Object> responseListener) {
        Util.postError(responseListener, ServiceCommandError.notSupported());
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void seek(long j, ResponseListener<Object> responseListener) {
        try {
            handleVoidAsyncFuture(responseListener, this.remoteMediaPlayer.seek(CustomMediaPlayer.PlayerSeekMode.Absolute, j), "Error seeking");
        } catch (Exception e) {
            Util.postError(responseListener, new FireTVServiceError("Error seeking", e));
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getDuration(DurationListener durationListener) {
        try {
            handleAsyncFuture(durationListener, this.remoteMediaPlayer.getDuration(), "Error getting duration");
        } catch (Exception e) {
            Util.postError(durationListener, new FireTVServiceError("Error getting duration", e));
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getPosition(PositionListener positionListener) {
        try {
            handleAsyncFuture(positionListener, this.remoteMediaPlayer.getPosition(), "Error getting position");
        } catch (Exception e) {
            Util.postError(positionListener, new FireTVServiceError("Error getting position", e));
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public void getPlayState(PlayStateListener playStateListener) {
        try {
            handleAsyncFutureWithConversion(playStateListener, this.remoteMediaPlayer.getStatus(), new ConvertResult<PlayStateStatus, MediaPlayerStatus>() {
                /* class com.connectsdk.service.FireTVService.AnonymousClass2 */

                public PlayStateStatus convert(MediaPlayerStatus mediaPlayerStatus) {
                    return FireTVService.this.createPlayStateStatusFromFireTVStatus(mediaPlayerStatus);
                }
            }, "Error getting play state");
        } catch (Exception e) {
            Util.postError(playStateListener, new FireTVServiceError("Error getting play state", e));
        }
    }

    @Override // com.connectsdk.service.capability.MediaControl
    public ServiceSubscription<PlayStateListener> subscribePlayState(PlayStateListener playStateListener) {
        PlayStateSubscription playStateSubscription2 = this.playStateSubscription;
        if (playStateSubscription2 == null) {
            PlayStateSubscription playStateSubscription3 = new PlayStateSubscription(playStateListener);
            this.playStateSubscription = playStateSubscription3;
            this.remoteMediaPlayer.addStatusListener(playStateSubscription3);
        } else if (!playStateSubscription2.getListeners().contains(playStateListener)) {
            this.playStateSubscription.addListener((PlayStateListener) playStateListener);
        }
        getPlayState(playStateListener);
        return this.playStateSubscription;
    }

    /* access modifiers changed from: package-private */
    public PlayStateStatus createPlayStateStatusFromFireTVStatus(MediaPlayerStatus mediaPlayerStatus) {
        PlayStateStatus playStateStatus = PlayStateStatus.Unknown;
        if (mediaPlayerStatus == null) {
            return playStateStatus;
        }
        int i = AnonymousClass7.$SwitchMap$com$amazon$whisperplay$fling$media$service$MediaPlayerStatus$MediaState[mediaPlayerStatus.getState().ordinal()];
        if (i == 1) {
            return PlayStateStatus.Buffering;
        }
        if (i == 2) {
            return PlayStateStatus.Playing;
        }
        if (i == 3) {
            return PlayStateStatus.Paused;
        }
        if (i == 4) {
            return PlayStateStatus.Finished;
        }
        if (i != 5) {
            return playStateStatus;
        }
        return PlayStateStatus.Idle;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.connectsdk.service.FireTVService$7  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$com$amazon$whisperplay$fling$media$service$MediaPlayerStatus$MediaState;

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
            int[] iArr = new int[MediaPlayerStatus.MediaState.values().length];
            $SwitchMap$com$amazon$whisperplay$fling$media$service$MediaPlayerStatus$MediaState = iArr;
            iArr[MediaPlayerStatus.MediaState.PreparingMedia.ordinal()] = 1;
            $SwitchMap$com$amazon$whisperplay$fling$media$service$MediaPlayerStatus$MediaState[MediaPlayerStatus.MediaState.Playing.ordinal()] = 2;
            $SwitchMap$com$amazon$whisperplay$fling$media$service$MediaPlayerStatus$MediaState[MediaPlayerStatus.MediaState.Paused.ordinal()] = 3;
            $SwitchMap$com$amazon$whisperplay$fling$media$service$MediaPlayerStatus$MediaState[MediaPlayerStatus.MediaState.Finished.ordinal()] = 4;
            $SwitchMap$com$amazon$whisperplay$fling$media$service$MediaPlayerStatus$MediaState[MediaPlayerStatus.MediaState.NoSource.ordinal()] = 5;
        }
    }

    private String getMetadata(MediaInfo mediaInfo) throws JSONException {
        ImageInfo imageInfo;
        JSONObject jSONObject = new JSONObject();
        if (mediaInfo.getTitle() != null && !mediaInfo.getTitle().isEmpty()) {
            jSONObject.put("title", mediaInfo.getTitle());
        }
        if (mediaInfo.getDescription() != null && !mediaInfo.getDescription().isEmpty()) {
            jSONObject.put("description", mediaInfo.getDescription());
        }
        jSONObject.put("type", mediaInfo.getMimeType());
        if (!(mediaInfo.getImages() == null || mediaInfo.getImages().size() <= 0 || (imageInfo = mediaInfo.getImages().get(0)) == null || imageInfo.getUrl() == null || imageInfo.getUrl().isEmpty())) {
            jSONObject.put(META_ICON_IMAGE, imageInfo.getUrl());
        }
        jSONObject.put("noreplay", true);
        if (mediaInfo.getSubtitleInfo() != null) {
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("kind", "subtitles");
            jSONObject2.put("src", mediaInfo.getSubtitleInfo().getUrl());
            String label = mediaInfo.getSubtitleInfo().getLabel();
            String str = "";
            if (label == null) {
                label = str;
            }
            jSONObject2.put("label", label);
            String language = mediaInfo.getSubtitleInfo().getLanguage();
            if (language != null) {
                str = language;
            }
            jSONObject2.put("srclang", str);
            jSONArray.put(jSONObject2);
            jSONObject.put("tracks", jSONArray);
        }
        return jSONObject.toString();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private MediaLaunchObject createMediaLaunchObject() {
        LaunchSession launchSession = new LaunchSession();
        launchSession.setService(this);
        launchSession.setSessionType(LaunchSession.LaunchSessionType.Media);
        launchSession.setAppId(this.remoteMediaPlayer.getUniqueIdentifier());
        launchSession.setAppName(this.remoteMediaPlayer.getName());
        return new MediaLaunchObject(launchSession, this);
    }

    private void setMediaSource(MediaInfo mediaInfo, LaunchListener launchListener) {
        try {
            handleAsyncFutureWithConversion(launchListener, this.remoteMediaPlayer.setMediaSource(mediaInfo.getUrl(), getMetadata(mediaInfo), true, false), new ConvertResult<MediaLaunchObject, Void>() {
                /* class com.connectsdk.service.FireTVService.AnonymousClass3 */

                public MediaLaunchObject convert(Void r1) {
                    return FireTVService.this.createMediaLaunchObject();
                }
            }, "Error setting media source");
        } catch (Exception e) {
            Util.postError(launchListener, new FireTVServiceError("Error setting media source", e));
        }
    }

    private void handleVoidAsyncFuture(ResponseListener<Object> responseListener, RemoteMediaPlayer.AsyncFuture<Void> asyncFuture, String str) {
        handleAsyncFutureWithConversion(responseListener, asyncFuture, new ConvertResult<Object, Void>() {
            /* class com.connectsdk.service.FireTVService.AnonymousClass4 */

            public Object convert(Void r1) {
                return r1;
            }
        }, str);
    }

    private <T> void handleAsyncFuture(ResponseListener<T> responseListener, RemoteMediaPlayer.AsyncFuture<T> asyncFuture, String str) {
        handleAsyncFutureWithConversion(responseListener, asyncFuture, new ConvertResult<T, T>() {
            /* class com.connectsdk.service.FireTVService.AnonymousClass5 */

            @Override // com.connectsdk.service.FireTVService.ConvertResult
            public T convert(T t) {
                return t;
            }
        }, str);
    }

    private <Response, Result> void handleAsyncFutureWithConversion(final ResponseListener<Response> responseListener, RemoteMediaPlayer.AsyncFuture<Result> asyncFuture, final ConvertResult<Response, Result> convertResult, final String str) {
        if (asyncFuture != null) {
            asyncFuture.getAsync(new RemoteMediaPlayer.FutureListener<Result>() {
                /* class com.connectsdk.service.FireTVService.AnonymousClass6 */

                @Override // com.amazon.whisperplay.fling.media.controller.RemoteMediaPlayer.FutureListener
                public void futureIsNow(Future<Result> future) {
                    try {
                        Util.postSuccess(responseListener, convertResult.convert(future.get()));
                    } catch (ExecutionException e) {
                        Util.postError(responseListener, new FireTVServiceError(str, e.getCause()));
                    } catch (Exception e2) {
                        Util.postError(responseListener, new FireTVServiceError(str, e2));
                    }
                }
            });
        } else {
            Util.postError(responseListener, new FireTVServiceError(str));
        }
    }

    private static abstract class Subscription<Status, Listener extends ResponseListener<Status>> implements ServiceSubscription<Listener> {
        List<Listener> listeners;
        Status prevStatus;

        public Subscription(Listener listener) {
            ArrayList arrayList = new ArrayList();
            this.listeners = arrayList;
            if (listener != null) {
                arrayList.add(listener);
            }
        }

        /* access modifiers changed from: package-private */
        public synchronized void notifyListeners(final Status status) {
            if (!status.equals(this.prevStatus)) {
                Util.runOnUI(new Runnable() {
                    /* class com.connectsdk.service.FireTVService.Subscription.AnonymousClass1 */

                    public void run() {
                        for (Listener listener : Subscription.this.listeners) {
                            listener.onSuccess(status);
                        }
                    }
                });
                this.prevStatus = status;
            }
        }

        public Listener addListener(Listener listener) {
            if (listener != null) {
                this.listeners.add(listener);
            }
            return listener;
        }

        public void removeListener(Listener listener) {
            this.listeners.remove(listener);
        }

        @Override // com.connectsdk.service.command.ServiceSubscription
        public List<Listener> getListeners() {
            return this.listeners;
        }
    }

    /* access modifiers changed from: package-private */
    public class PlayStateSubscription extends Subscription<PlayStateStatus, PlayStateListener> implements CustomMediaPlayer.StatusListener {
        public PlayStateSubscription(PlayStateListener playStateListener) {
            super(playStateListener);
        }

        @Override // com.amazon.whisperplay.fling.media.service.CustomMediaPlayer.StatusListener
        public void onStatusChange(MediaPlayerStatus mediaPlayerStatus, long j) {
            notifyListeners(FireTVService.this.createPlayStateStatusFromFireTVStatus(mediaPlayerStatus));
        }

        @Override // com.connectsdk.service.command.ServiceSubscription
        public void unsubscribe() {
            FireTVService.this.remoteMediaPlayer.removeStatusListener(this);
            FireTVService.this.playStateSubscription = null;
        }
    }
}

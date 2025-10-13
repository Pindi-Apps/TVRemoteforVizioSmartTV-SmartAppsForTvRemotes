package com.connectsdk.discovery.provider;

import static androidx.mediarouter.media.MediaRouter.CALLBACK_FLAG_FORCE_DISCOVERY;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;

import com.connectsdk.core.Util;
import com.connectsdk.discovery.DiscoveryFilter;
import com.connectsdk.discovery.DiscoveryProvider;
import com.connectsdk.discovery.DiscoveryProviderListener;
import com.connectsdk.service.CastService;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.config.ServiceDescription;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;

import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CastDiscoveryProvider implements DiscoveryProvider {

    boolean isRunning = false;
    private Timer removeRoutesTimer;
    private final MediaRouter mMediaRouter;
    private static final long ROUTE_REMOVE_INTERVAL = 6000;
    private MediaRouteSelector mMediaRouteSelector;
    protected MediaRouter.Callback mMediaRouterCallback;
    protected ConcurrentHashMap<String, ServiceDescription> foundServices;
    private final List<String> removedUUID = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<DiscoveryProviderListener> serviceListeners;

    @Override
    public void addDeviceFilter(DiscoveryFilter discoveryFilter) {}

    @Override
    public boolean isEmpty() { return false; }

    @Override
    public void removeDeviceFilter(DiscoveryFilter discoveryFilter) {}

    @Override
    public void setFilters(List<DiscoveryFilter> list) {}

    public CastDiscoveryProvider(Context context) {
        this.mMediaRouter = createMediaRouter(context);
        this.mMediaRouterCallback = new MediaRouterCallback();
        this.foundServices = new ConcurrentHashMap<>(8, 0.75f, 2);
        this.serviceListeners = new CopyOnWriteArrayList<>();
    }

    public MediaRouter createMediaRouter(Context context) {
        return MediaRouter.getInstance(context);
    }

    @Override
    public void start() {
        System.out.println("starting Cast discovery provider");
        if (!this.isRunning) {
            this.isRunning = true;
            if (this.mMediaRouteSelector == null) {
                try {
                    this.mMediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(CastService.getApplicationID())).build();
                } catch (IllegalArgumentException unused) {
                    String str = Util.T;
                    Log.w(str, "Invalid application ID: " + CastService.getApplicationID());
                    unused.printStackTrace();
                    for (DiscoveryProviderListener serviceListener : this.serviceListeners) {
                        serviceListener.onServiceDiscoveryFailed(this, new ServiceCommandError(0, "Invalid application ID: " + CastService.getApplicationID(), null));
                    }
                    return;
                }
            }
            rescan();
        }
    }

    @Override
    public void stop() {
        this.isRunning = false;
        Timer timer = this.removeRoutesTimer;
        if (timer != null) {
            timer.cancel();
            this.removeRoutesTimer = null;
        }
        if (this.mMediaRouter != null) {
            Util.runOnUI(() -> CastDiscoveryProvider.this.mMediaRouter.removeCallback(CastDiscoveryProvider.this.mMediaRouterCallback));
        }
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public void reset() {
        stop();
        this.foundServices.clear();
    }

    @Override
    public void rescan() {
        Util.runOnUI(() -> CastDiscoveryProvider.this.mMediaRouter.addCallback(CastDiscoveryProvider.this.mMediaRouteSelector, CastDiscoveryProvider.this.mMediaRouterCallback, CALLBACK_FLAG_FORCE_DISCOVERY));
    }

    @Override
    public void addListener(DiscoveryProviderListener discoveryProviderListener) {
        this.serviceListeners.add(discoveryProviderListener);
    }

    @Override
    public void removeListener(DiscoveryProviderListener discoveryProviderListener) {
        this.serviceListeners.remove(discoveryProviderListener);
    }

    private class MediaRouterCallback extends MediaRouter.Callback {
        private MediaRouterCallback() {
        }

        @Override
        public void onRouteAdded(@NonNull MediaRouter mediaRouter, @NonNull MediaRouter.RouteInfo routeInfo) {
            super.onRouteAdded(mediaRouter, routeInfo);


            CastDevice fromBundle = CastDevice.getFromBundle(routeInfo.getExtras());
            if (fromBundle == null) {
                Log.w("CastDiscoveryProvider", "CastDevice is null. Skipping route addition.");
                return;
            }

            String deviceId = fromBundle.getDeviceId();
            if (deviceId == null) {
                Log.w("CastDiscoveryProvider", "Device ID is null. Skipping route addition.");
                return;
            }

            Log.i("TAG123", "onRouteAdded: " + deviceId);
            CastDiscoveryProvider.this.removedUUID.remove(deviceId);

            InetAddress ipAddress = fromBundle.getIpAddress();
            if (ipAddress == null) {
                Log.w("CastDiscoveryProvider", "IP Address is null for device: " + deviceId);
                return;
            }

            String hostAddress = ipAddress.getHostAddress();
            ServiceDescription serviceDescription = foundServices.get(deviceId);

            boolean isNewService = false;
            if (serviceDescription == null) {
                isNewService = true;
                serviceDescription = new ServiceDescription(CastService.ID, deviceId, hostAddress);
                serviceDescription.setFriendlyName(fromBundle.getFriendlyName());
                serviceDescription.setModelName(fromBundle.getModelName());
                serviceDescription.setModelNumber(fromBundle.getDeviceVersion());
                serviceDescription.setModelDescription(routeInfo.getDescription());
                serviceDescription.setPort(fromBundle.getServicePort());
                serviceDescription.setServiceID(CastService.ID);
                serviceDescription.setDevice(fromBundle);
            } else {
                boolean hasChanges = false;

                if (!Objects.equals(serviceDescription.getFriendlyName(), fromBundle.getFriendlyName())) {
                    serviceDescription.setFriendlyName(fromBundle.getFriendlyName());
                    hasChanges = true;
                }

                serviceDescription.setDevice(fromBundle);
                serviceDescription.setIpAddress(hostAddress);
                serviceDescription.setLastDetection(new Date().getTime());

                if (hasChanges) {
                    isNewService = true;
                }
            }

            foundServices.put(deviceId, serviceDescription);

            if (isNewService) {
                for (DiscoveryProviderListener listener : serviceListeners) {
                    listener.onServiceAdded(CastDiscoveryProvider.this, serviceDescription);
                }
            }


        }

        @Override
        public void onRouteChanged(@NonNull MediaRouter mediaRouter, @NonNull MediaRouter.RouteInfo routeInfo) {
            super.onRouteChanged(mediaRouter, routeInfo);
            CastDevice castDevice = CastDevice.getFromBundle(routeInfo.getExtras());
            String uuid = null;
            if (castDevice != null){
                uuid = castDevice.getDeviceId();
                ServiceDescription foundService = foundServices.get(uuid);
                boolean listUpdateFlag = false;
                boolean isNew = foundService == null;
                if (isNew) {
                    if (castDevice.getIpAddress() != null) {
                        if (castDevice.getIpAddress().getHostAddress() != null) {
                            foundService = new ServiceDescription(CastService.ID, uuid, Objects.requireNonNull(castDevice.getIpAddress()).getHostAddress());
                            foundService.setFriendlyName(castDevice.getFriendlyName());
                            foundService.setServiceID(CastService.ID);
                            listUpdateFlag = true;
                            isNew = false;

                        }
                    }
                }

                if (!isNew) {

                    if (castDevice.getIpAddress() != null){
                        if (castDevice.getIpAddress().getHostAddress() != null){
                            foundService.setIpAddress(castDevice.getIpAddress().getHostAddress());
                            foundService.setModelName(castDevice.getModelName());
                            foundService.setModelNumber(castDevice.getDeviceVersion());
                            foundService.setModelDescription(routeInfo.getDescription());
                            foundService.setPort(castDevice.getServicePort());
                            foundService.setDevice(castDevice);

                        }


                        if (!foundService.getFriendlyName().equals(castDevice.getFriendlyName())) {
                            foundService.setFriendlyName(castDevice.getFriendlyName());
                            listUpdateFlag = true;
                        }

                        foundService.setLastDetection(new Date().getTime());

                        foundServices.put(uuid, foundService);

                        if (listUpdateFlag) {
                            for (DiscoveryProviderListener listenter: serviceListeners) {
                                listenter.onServiceAdded(CastDiscoveryProvider.this, foundService);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onRoutePresentationDisplayChanged(@NonNull MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            String str = Util.T;
            Log.d(str, "onRoutePresentationDisplayChanged: [" + routeInfo.getName() + "] [" + routeInfo.getDescription() + "]");
            super.onRoutePresentationDisplayChanged(mediaRouter, routeInfo);
        }

        @Override
        public void onRouteRemoved(@NonNull MediaRouter mediaRouter, @NonNull final MediaRouter.RouteInfo routeInfo) {
            super.onRouteRemoved(mediaRouter, routeInfo);
            CastDiscoveryProvider.this.removedUUID.add(Objects.requireNonNull(CastDevice.getFromBundle(routeInfo.getExtras())).getDeviceId());
            if (CastDiscoveryProvider.this.removeRoutesTimer == null) {
                CastDiscoveryProvider.this.removeRoutesTimer = new Timer();
                CastDiscoveryProvider.this.removeRoutesTimer.schedule(new TimerTask() {
                    public void run() {
                        MediaRouterCallback.this.removeServices(routeInfo);
                    }
                }, 3000);
            }
        }

        @Override
        public void onRouteVolumeChanged(@NonNull MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
            String str = Util.T;
            Log.d(str, "onRouteVolumeChanged: [" + routeInfo.getName() + "] [" + routeInfo.getDescription() + "]");
            super.onRouteVolumeChanged(mediaRouter, routeInfo);
        }

        private void removeServices(MediaRouter.RouteInfo routeInfo) {
            for (String str : CastDiscoveryProvider.this.removedUUID) {
                final ServiceDescription serviceDescription = CastDiscoveryProvider.this.foundServices.get(str);
                if (serviceDescription != null) {
                    String str2 = Util.T;
                    Log.d(str2, "Service [" + routeInfo.getName() + "] has been removed");
                    Util.runOnUI(() -> {
                        for (DiscoveryProviderListener serviceListener : CastDiscoveryProvider.this.serviceListeners) {
                            serviceListener.onServiceRemoved(CastDiscoveryProvider.this, serviceDescription);
                        }
                    });
                    foundServices.remove(str);
                }
            }
            removedUUID.clear();
        }
    }
}

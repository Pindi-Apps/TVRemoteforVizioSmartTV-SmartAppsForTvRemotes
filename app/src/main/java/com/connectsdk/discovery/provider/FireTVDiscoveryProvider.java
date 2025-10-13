package com.connectsdk.discovery.provider;

import android.content.Context;

import com.amazon.whisperplay.fling.media.controller.DiscoveryController;
import com.amazon.whisperplay.fling.media.controller.RemoteMediaPlayer;
import com.connectsdk.core.Util;
import com.connectsdk.discovery.DiscoveryFilter;
import com.connectsdk.discovery.DiscoveryProvider;
import com.connectsdk.discovery.DiscoveryProviderListener;
import com.connectsdk.service.FireTVService;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.config.ServiceDescription;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FireTVDiscoveryProvider implements DiscoveryProvider {
    private DiscoveryController discoveryController;
    DiscoveryController.IDiscoveryListener fireTVListener;
    ConcurrentHashMap<String, ServiceDescription> foundServices;
    private boolean isRunning;
    CopyOnWriteArrayList<DiscoveryProviderListener> serviceListeners;

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void addDeviceFilter(DiscoveryFilter discoveryFilter) {
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void removeDeviceFilter(DiscoveryFilter discoveryFilter) {
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void setFilters(List<DiscoveryFilter> list) {
    }

    public FireTVDiscoveryProvider(Context context) {
        this(new DiscoveryController(context));
    }

    public FireTVDiscoveryProvider(DiscoveryController discoveryController2) {
        this.foundServices = new ConcurrentHashMap<>();
        this.serviceListeners = new CopyOnWriteArrayList<>();
        this.discoveryController = discoveryController2;
        this.fireTVListener = new FireTVDiscoveryListener();
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void start() {
        System.out.println("starting Fire Tv discovery provider");

        if (!this.isRunning) {
            this.discoveryController.start(this.fireTVListener);
            this.isRunning = true;
        }
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void stop() {
        if (this.isRunning) {
            this.discoveryController.stop();
            this.isRunning = false;
        }
        for (ServiceDescription serviceDescription : this.foundServices.values()) {
            notifyListenersThatServiceLost(serviceDescription);
        }
        this.foundServices.clear();
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void restart() {
        stop();
        start();
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void rescan() {
        restart();
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void reset() {
        this.foundServices.clear();
        stop();
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void addListener(DiscoveryProviderListener discoveryProviderListener) {
        this.serviceListeners.add(discoveryProviderListener);
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public void removeListener(DiscoveryProviderListener discoveryProviderListener) {
        this.serviceListeners.remove(discoveryProviderListener);
    }

    @Override // com.connectsdk.discovery.DiscoveryProvider
    public boolean isEmpty() {
        return this.foundServices.isEmpty();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void notifyListenersThatServiceAdded(final ServiceDescription serviceDescription) {
        Util.runOnUI(new Runnable() {
            /* class com.connectsdk.discovery.provider.FireTVDiscoveryProvider.AnonymousClass1 */

            public void run() {
                Iterator<DiscoveryProviderListener> it = FireTVDiscoveryProvider.this.serviceListeners.iterator();
                while (it.hasNext()) {
                    it.next().onServiceAdded(FireTVDiscoveryProvider.this, serviceDescription);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void notifyListenersThatServiceLost(final ServiceDescription serviceDescription) {
        Util.runOnUI(new Runnable() {
            /* class com.connectsdk.discovery.provider.FireTVDiscoveryProvider.AnonymousClass2 */

            public void run() {
                Iterator<DiscoveryProviderListener> it = FireTVDiscoveryProvider.this.serviceListeners.iterator();
                while (it.hasNext()) {
                    it.next().onServiceRemoved(FireTVDiscoveryProvider.this, serviceDescription);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void notifyListenersThatDiscoveryFailed(final ServiceCommandError serviceCommandError) {
        Util.runOnUI(new Runnable() {
            /* class com.connectsdk.discovery.provider.FireTVDiscoveryProvider.AnonymousClass3 */

            public void run() {
                Iterator<DiscoveryProviderListener> it = FireTVDiscoveryProvider.this.serviceListeners.iterator();
                while (it.hasNext()) {
                    it.next().onServiceDiscoveryFailed(FireTVDiscoveryProvider.this, serviceCommandError);
                }
            }
        });
    }

    class FireTVDiscoveryListener implements DiscoveryController.IDiscoveryListener {
        FireTVDiscoveryListener() {
        }

        @Override // com.amazon.whisperplay.fling.media.controller.DiscoveryController.IDiscoveryListener
        public void playerDiscovered(RemoteMediaPlayer remoteMediaPlayer) {
            if (remoteMediaPlayer != null) {
                String uniqueIdentifier = remoteMediaPlayer.getUniqueIdentifier();
                ServiceDescription serviceDescription = FireTVDiscoveryProvider.this.foundServices.get(uniqueIdentifier);
                if (serviceDescription == null) {
                    ServiceDescription serviceDescription2 = new ServiceDescription();
                    updateServiceDescription(serviceDescription2, remoteMediaPlayer);
                    FireTVDiscoveryProvider.this.foundServices.put(uniqueIdentifier, serviceDescription2);
                    FireTVDiscoveryProvider.this.notifyListenersThatServiceAdded(serviceDescription2);
                    return;
                }
                updateServiceDescription(serviceDescription, remoteMediaPlayer);
            }
        }

        @Override // com.amazon.whisperplay.fling.media.controller.DiscoveryController.IDiscoveryListener
        public void playerLost(RemoteMediaPlayer remoteMediaPlayer) {
            ServiceDescription serviceDescription;
            if (remoteMediaPlayer != null && (serviceDescription = FireTVDiscoveryProvider.this.foundServices.get(remoteMediaPlayer.getUniqueIdentifier())) != null) {
                FireTVDiscoveryProvider.this.notifyListenersThatServiceLost(serviceDescription);
                FireTVDiscoveryProvider.this.foundServices.remove(remoteMediaPlayer.getUniqueIdentifier());
            }
        }

        @Override // com.amazon.whisperplay.fling.media.controller.DiscoveryController.IDiscoveryListener
        public void discoveryFailure() {
            FireTVDiscoveryProvider.this.notifyListenersThatDiscoveryFailed(new ServiceCommandError("FireTV discovery failure"));
        }

        private void updateServiceDescription(ServiceDescription serviceDescription, RemoteMediaPlayer remoteMediaPlayer) {
            String uniqueIdentifier = remoteMediaPlayer.getUniqueIdentifier();
            serviceDescription.setDevice(remoteMediaPlayer);
            serviceDescription.setFriendlyName(remoteMediaPlayer.getName());
            serviceDescription.setIpAddress(uniqueIdentifier);
            serviceDescription.setServiceID(FireTVService.ID);
            serviceDescription.setUUID(uniqueIdentifier);
        }
    }
}

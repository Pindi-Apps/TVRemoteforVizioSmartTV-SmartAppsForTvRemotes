package com.tv.remote.smarttv.control.smartcast.vizio.general

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.connectsdk.discovery.DiscoveryManager
import com.connectsdk.service.DIALService
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.tv.remote.smarttv.control.smartcast.vizio.utils.general.WebHost
import java.util.Date
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.ConnectionActivity

class ApplicationClass : MultiDexApplication(), Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {


    private var serverStarted = false
    private var webHost: WebHost? = null
    private var currentActivity:Activity? = null
    private lateinit var appOpenAdManager: AppOpenAdManager


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }


    override fun onCreate() {
        super<MultiDexApplication>.onCreate()
        // MobileAds is initialized in SplashActivity, off the main thread, only after consent allows it.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        appOpenAdManager = AppOpenAdManager()
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        webHost = WebHost(this)
        if (isNetworkAvailable(this)) {
            serverStarted = true
            webHost?.continueServer()
        } else {
            serverStarted = false
        }

        DIALService.registerApp("Levak")
        DiscoveryManager.init(this)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    override fun onTerminate() {
        webHost?.endServer()
        super.onTerminate()
    }

    fun getHost(): WebHost? {
        if (webHost == null) {
            webHost = WebHost.getInstance(this)
        }

        if (!serverStarted) {
            serverStarted = true
            webHost?.continueServer()
        }

        return webHost
    }

    fun adAvailable() = appOpenAdManager.isAdAvailable()

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (currentActivity !is ConnectionActivity) {
            currentActivity?.let {
                if (appOpenAdManager.isAdAvailable()) {
                    showAdIfAvailable(it, object : OnShowAdCompleteListener{
                        override fun onShowAdComplete() {
                            loadAd(it)
                        }
                    })
                } else {
                    loadAd(it)
                }
            }
        } else {
            if (!appOpenAdManager.isAdAvailable()) {
                currentActivity?.let { loadAd(it) }
            }
        }
    }

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityStarted(p0: Activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = p0
        }
    }

    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    fun loadAd(activity: Activity) {
        appOpenAdManager.loadAd(activity)
    }

    private inner class AppOpenAdManager {
        private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false
        var isShowingAd = false

        private var loadTime: Long = 0

        fun loadAd(context: Context) {
            if (isLoadingAd || isAdAvailable() || !googleMobileAdsConsentManager.canRequestAds) {
                return
            }

            isLoadingAd = true
            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                getString(R.string.admobOpenAd),
                request,
                object : AppOpenAd.AppOpenAdLoadCallback() {

                    override fun onAdLoaded(ad: AppOpenAd) {
                        appOpenAd = ad
                        isLoadingAd = false
                        loadTime = Date().time
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        isLoadingAd = false
                    }
                },
            )
        }

        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference: Long = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        fun isAdAvailable(): Boolean {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
        }

        fun showAdIfAvailable(activity: Activity) {
            showAdIfAvailable(
                activity,
                object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {}
                },
            )
        }

        /**
         * Show the ad if one isn't already showing.
         * @param activity the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
            if (isShowingAd) {
                return
            }

            if (!isAdAvailable()) {
                onShowAdCompleteListener.onShowAdComplete()
                if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
                }
                return
            }

            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadAd(activity)
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false

                    onShowAdCompleteListener.onShowAdComplete()
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadAd(activity)
                    }
                }

                override fun onAdShowedFullScreenContent() {}
            }
            isShowingAd = true
            appOpenAd?.show(activity)
        }
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }
}
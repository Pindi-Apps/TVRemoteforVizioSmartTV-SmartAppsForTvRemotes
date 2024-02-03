package com.tv.remote.smarttv.control.smartcast.vizio.general

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.connectsdk.discovery.DiscoveryManager
import com.connectsdk.service.DIALService
import com.google.android.gms.ads.MobileAds
import com.tv.remote.smarttv.control.smartcast.vizio.utils.general.WebHost

class ApplicationClass : MultiDexApplication() {


    private var webHost: WebHost? = null
    private var serverStarted = false


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }


    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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




}
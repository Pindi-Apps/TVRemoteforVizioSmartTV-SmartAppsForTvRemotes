package com.tv.remote.smarttv.control.smartcast.vizio.utils.extension

import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Vibrator
import androidx.multidex.MultiDexApplication

class ApplicationClass : MultiDexApplication() {
//    private var webHost: WebHost? = null

    private var serverStarted = false

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null



//    fun getMediaPlayer(): MediaPlayer? {
//        if (mediaPlayer == null) {
//            mediaPlayer = MediaPlayer.create(this, R.raw.touch)
//        }
//        return mediaPlayer
//    }


    fun getVibrator(): Vibrator? {
        if (vibrator == null) {
            vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        return vibrator
    }




    override fun onCreate() {
        super.onCreate()
//        MobileAds.initialize(this)
//        DIALService.registerApp("Levak")
//        DiscoveryManager.init(this)
//        webHost = WebHost(this)
        }

//    override fun onTerminate() {
//        webHost?.endServer()
//        super.onTerminate()
//    }

//    fun getHost(): WebHost? {
//        if (webHost == null) {
//            webHost = WebHost.getInstance(this)
//        }
//        if (!serverStarted) {
//            serverStarted = true
//            webHost?.continueServer()
//        }
//        return webHost
//    }



    }


    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

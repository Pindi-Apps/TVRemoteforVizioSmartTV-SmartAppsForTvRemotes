package com.tv.remote.smarttv.control.smartcast.vizio.base


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.tv.remote.smarttv.control.smartcast.vizio.R
import tvremote.forroku.smart.tvremotecontrol.db.SessionManager
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow


abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    lateinit var binding: B

    var sessionManager: SessionManager? = null
//    var sessionManager: SessionManager? = null

    protected fun showProgressDialog(dialog: (Dialog) -> Unit) {
        val wifiDialog = Dialog(this)
        try {
            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (ne: Exception) {
            ne.printStackTrace()
        }
        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        wifiDialog.setContentView(R.layout.progress_dialog)
        dialog.invoke(wifiDialog)
    }



//    protected fun showAdDialog(activity: Activity, dialog: (Dialog) -> Unit){
//        val adDialog = Dialog(activity)
//        try {
//            adDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        adDialog.setCancelable(false)
//        adDialog.setContentView(R.layout.ad_loading_dialog)
//        dialog.invoke(adDialog)
//    }


    protected fun storagePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }


    protected fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

//    protected fun progressConnectDialog(activity: Activity, alert: (AlertDialog) -> Unit) {
//        val dialogBuilder = AlertDialog.Builder(activity)
//        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val mBinding = inflater.inflate(R.layout.connected_succesfully_dialog, null)
//        mBinding.findViewById<LottieAnimationView>(R.id.add).gone()
//        mBinding.findViewById<LottieAnimationView>(R.id.progress).visible()
//        dialogBuilder.setView(mBinding.rootView)
//        val alertDialog = dialogBuilder.create()
//        try {
//            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            alertDialog.window?.setDimAmount(0.7F)
//        } catch (exc: Exception) {
//            exc.printStackTrace()
//        }
//        alertDialog.setCancelable(false)
//        alert.invoke(alertDialog)
//    }

//    protected fun connectDialog(activity: Activity) {
//        runOnUiThread {
//            val dialogBuilder = AlertDialog.Builder(activity)
//            val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val mBinding = inflater.inflate(R.layout.connected_succesfully_dialog, null)
//            dialogBuilder.setView(mBinding.rootView)
//            val alertDialog = dialogBuilder.create()
//            try {
//                alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                alertDialog.window?.setDimAmount(0.7F)
//            } catch (exc: Exception) {
//                exc.printStackTrace()
//            }
//            alertDialog.setCancelable(false)
//
//            if (activity.isFinishing.not()) {
//                alertDialog.show()
//            }
//
//            Timer().schedule(timerTask {
//                if (activity.isFinishing.not()) {
//                    if (alertDialog.isShowing) {
//                        alertDialog.dismiss()
//                    }
//                    runOnUiThread {
//                        showToast(getString(R.string.connected_successfully))
//                        activity.finish()
//                    }
//                }
//            }, 2000);
//        }
//    }
//    protected fun connectWifi() {
//        val wifiDialog = Dialog(this)
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        wifiDialog.setContentView(R.layout.network_not_availble)
//        wifiDialog.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
//            wifiDialog.dismiss()
//        }
//        wifiDialog.findViewById<MaterialButton>(R.id.btnConnect).setOnClickListener {
//            try {
//                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
//                wifiDialog.dismiss()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        wifiDialog.show()
//    }


//    protected  fun adLoadingDialog(adDialog: (Dialog) -> Unit){
//        val dialog = Dialog(this)
//        try {
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        dialog.setContentView(R.layout.ad_loading_dialog)
//        dialog.setCancelable(false)
//        adDialog.invoke(dialog)
//    }


//    protected fun showDialog(activity: Activity, dialog: (Dialog) -> Unit) {
//        val wifiDialog = Dialog(activity)
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        wifiDialog.setContentView(R.layout.filter_lodaing)
//        dialog.invoke(wifiDialog)
//    }

//    protected fun connectedWifi(){
//        val wifiDialog=Dialog(this)
//
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//        }
//        catch (ne:Exception){
//            ne.printStackTrace()
//        }
//        wifiDialog.setContentView(R.layout.wifi_notconnected_dialge)
//        wifiDialog.findViewById<MaterialCardView>(R.id.wifiOpen).setOnClickListener {
//            try {
//                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
//                wifiDialog.dismiss()
//            }
//            catch (e:Exception){
//                e.printStackTrace()
//            }
//        }
//        wifiDialog.show()
//    }



//    protected fun showConnectedDialog(dialog: (Dialog, DiCastConnectedBinding) -> Unit){
//        val wifiDialog=Dialog(this)
//
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//        }
//        catch (ne:Exception){
//            ne.printStackTrace()
//        }
//        val castBinding = DiCastConnectedBinding.inflate(layoutInflater)
//        wifiDialog.setContentView(castBinding.root)
//        dialog.invoke(wifiDialog , castBinding)
//    }



//    protected fun disconnectRemote(){
//        val disconectDialog=Dialog(this)
//
//        try {
//            disconectDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        }
//        catch (ne:Exception){
//            ne.printStackTrace()
//        }
//        disconectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        disconectDialog.setContentView(R.layout.wifi_notconnected_dialge)
//        disconectDialog.findViewById<MaterialCardView>(R.id.yesBtn).setOnClickListener {
//            finish()
//        }
//        disconectDialog.findViewById<MaterialCardView>(R.id.noBtn).setOnClickListener {
//            disconectDialog.dismiss()
//            finish()
//        }
//
//        disconectDialog.show()
//    }




//    protected fun connectionWifi() {
//        val wifiDialog = Dialog(this)
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        wifiDialog.setContentView(R.layout.wifi_notfound_design)
//        wifiDialog.findViewById<MaterialCardView>(R.id.wifiRemote).setOnSingleClickListener {
//            try {
//                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
//                wifiDialog.dismiss()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        wifiDialog.show()
//    }


//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(newBase)
//        val sessionManager: SharedPreference = SharedPreference.getInstance()
//        MyContextWrapper.wrap(newBase, sessionManager.getLanguage(newBase) ?: "en")
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        sessionManager = SessionManager(this)
//        permissionArray = arrayOf(
//            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
        initViews()
        actions()
    }

    fun goToNext(activity: Class<out Activity>) {
        this.startActivity(Intent(this, activity))
    }


    fun goToNextWithData(activity: Class<out Activity>, string: String) {
        val intent = Intent(this, activity)
        intent.putExtra("key", string)
        this.startActivity(intent)
    }

    fun goToNextWithData(activity: Class<out Activity>, position: Int) {
        val intent = Intent(this, activity)
        intent.putExtra("pos", position)
        startActivity(intent)
    }


    fun goToNextWithFinish(activity: Class<out Activity>) {
        this.startActivity(Intent(this, activity))
        this.finish()
    }

    fun openLink(link: String) {
        try {
            this.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    protected fun isWifiAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork
            if (nw == null) {
                false
            } else {
                val capabilities = connectivityManager.getNetworkCapabilities(nw)
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo
            nwInfo?.isConnected ?: false
        }
    }
//    protected fun showProgressDialog(dialog: (Dialog) -> Unit) {
//        val wifiDialog = Dialog(this)
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        wifiDialog.setContentView(R.layout.progress_dialog)
//        dialog.invoke(wifiDialog)
//    }

//    protected fun openOptionDialog() {
//        val dialog = OptionDialog.getInstance()
//        if (supportFragmentManager.findFragmentByTag(OptionDialog.NAME) != null) {
//            ((supportFragmentManager.findFragmentByTag(OptionDialog.NAME) as OptionDialog).dismiss())
//        }
//        try {
//            dialog.show(supportFragmentManager, OptionDialog.NAME)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            if (dialog.isAdded)
//                dialog.dismiss()
//        }
//    }

//    protected fun showDialog(activity: Activity, dialog: (Dialog) -> Unit) {
//        val wifiDialog = Dialog(activity)
//        try {
//            wifiDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } catch (ne: Exception) {
//            ne.printStackTrace()
//        }
//        wifiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        wifiDialog.setContentView(R.layout.filter_loading)
//        dialog.invoke(wifiDialog)
//    }

    open fun getFileSize(size: Long): String? {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }

//    protected fun closeMediaDialog() {
//        exitsDialog("are you sure you want to exut",R.drawable.exit)
//
//    }

//    fun exitsDialog(text:String,icon:Int){
//        var dialoge=Dialog(this)
//        dialoge.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialoge.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialoge.setContentView(R.layout.exit_dialog)
//        dialoge.show()
//        dialoge.findViewById<ImageView>(R.id.imageChange).setImageResource(icon)
//        dialoge.findViewById<TextView>(R.id.textChange).text=text
//        dialoge.findViewById<MaterialCardView>(R.id.yesBtn).setOnClickListener {
//            dialoge.dismiss()
//            finish()
//        }
//        dialoge.findViewById<MaterialCardView>(R.id.noBtn).setOnClickListener {
//            dialoge.dismiss()
//        }
//
//    }


//    fun disconnectDialog(boolean: (Boolean) -> Unit){
//        val dialoge = Dialog(this)
//        try {
//            dialoge.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialoge.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        dialoge.setContentView(R.layout.exit_dialog)
//        dialoge.show()
//        dialoge.findViewById<ImageView>(R.id.imageChange).loadImage(R.drawable.castsamsung , this)
//        dialoge.findViewById<TextView>(R.id.textChange).text= "Are you sure you want to Disconnect this Cast/Remote Device?"
//        dialoge.findViewById<MaterialCardView>(R.id.yesBtn).setOnClickListener {
//            dialoge.dismiss()
//            boolean.invoke(true)
//        }
//        dialoge.findViewById<MaterialCardView>(R.id.noBtn).setOnClickListener {
//            dialoge.dismiss()
//        }
//
//    }





    abstract fun getViewBinding(): B
    abstract fun initViews()
    abstract fun actions()


}
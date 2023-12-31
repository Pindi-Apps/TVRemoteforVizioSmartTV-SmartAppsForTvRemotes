package com.tv.remote.smarttv.control.smartcast.vizio.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.device.ConnectableDeviceListener
import com.connectsdk.discovery.CapabilityFilter
import com.connectsdk.discovery.DiscoveryManager
import com.connectsdk.discovery.DiscoveryManagerListener
import com.connectsdk.discovery.DiscoveryProvider
import com.connectsdk.service.DeviceService
import com.connectsdk.service.capability.MediaControl
import com.connectsdk.service.capability.MediaPlayer
import com.connectsdk.service.capability.VolumeControl
import com.connectsdk.service.command.ServiceCommandError
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.RvConnection
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.RvConnectionModel
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseActivity
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityCastSearchBinding
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.invisible
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.showToast
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.visible

class CastSearchActivity : BaseActivity<ActivityCastSearchBinding>() , DiscoveryManagerListener  {




    private var deviceList = ArrayList<RvConnectionModel>()
    private var trackerHandler: Handler? = null
    private var trackerRunnable: Runnable? = null
    private var rvAdapter : RvConnection? = null
    private var pinDialog : AlertDialog? = null
    private var pinCode: AlertDialog? = null



    private fun checkList() {
        if (deviceList.size == 0) {
            binding.listdevices.invisible()
            return
        }
        binding.listdevices.visible()
    }

    private fun timeOutHandler() {
        trackerHandler = Handler(Looper.getMainLooper())
        trackerRunnable = Runnable {
            try {
                if (deviceList.size == 0) {
                    checkList()
                    recreate()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        trackerHandler?.postDelayed(trackerRunnable!!, 20000)
    }




    override fun getViewBinding(): ActivityCastSearchBinding {
        return ActivityCastSearchBinding.inflate(layoutInflater)
    }


    internal object TokenGenerator {
        var pairingType: IntArray

        init {
            val iArr = IntArray(DeviceService.PairingType.values().size)
            pairingType = iArr
            iArr[DeviceService.PairingType.FIRST_SCREEN.ordinal] = 1
            pairingType[DeviceService.PairingType.PIN_CODE.ordinal] = 2
            pairingType[DeviceService.PairingType.MIXED.ordinal] = 3
            try {
                pairingType[DeviceService.PairingType.NONE.ordinal] = 4
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }
    }




    private val deviceListener: ConnectableDeviceListener = object : ConnectableDeviceListener {
        override fun onCapabilityUpdated(
            connectableDevice: ConnectableDevice, list: List<String>, list2: List<String>
        ) {
        }

        override fun onConnectionFailed(
            connectableDevice: ConnectableDevice, serviceCommandError: ServiceCommandError
        ) {
        }

        override fun onDeviceDisconnected(connectableDevice: ConnectableDevice) {}
        override fun onPairingRequired(
            connectableDevice: ConnectableDevice?,
            deviceService: DeviceService,
            pairingType: DeviceService.PairingType
        ) {
            val token = TokenGenerator.pairingType[pairingType.ordinal]
            if (token == 1) {
                pinDialog =
                    AlertDialog.Builder(this@CastSearchActivity).setTitle("Pairing with Smart Vizio Tv")
                        .setMessage("Confirmation for connection Vizio Tv")
                        .setPositiveButton("Okay", null as DialogInterface.OnClickListener?)
                        .setNegativeButton(
                            "Cancel"
                        ) { _, _ -> }.create()

                if (!this@CastSearchActivity.isFinishing) {
                    this@CastSearchActivity.pinDialog?.show()
                }
            } else if (token == 2 || token == 3) {
                val editText = EditText(this@CastSearchActivity)
                editText.inputType = 1
                editText.requestFocus()
                val inputMethodManager =
                    this@CastSearchActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                this@CastSearchActivity.pinCode = AlertDialog.Builder(this@CastSearchActivity)
                    .setTitle("Enter Pairing Code view on your screen").setView(editText)
                    .setPositiveButton(
                        "Ok"
                    ) { _, _ ->
                        if (connectableDevice != null) {
                            connectableDevice.sendPairingKey(editText.text.toString()
                                .trim { it <= ' ' })
                            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
                        }
                    }.setNegativeButton(
                        "Cancel"
                    ) { _: DialogInterface?, _: Int ->
                        inputMethodManager.hideSoftInputFromWindow(
                            editText.windowToken, 0
                        )
                    }.create()
                if (!this@CastSearchActivity.isFinishing) {
                    this@CastSearchActivity.pinCode?.show()
                    inputMethodManager.toggleSoftInput(2, 0)
                }
            }
        }


        override fun onDeviceReady(connectableDevice: ConnectableDevice) {
            try {
                if (this@CastSearchActivity.pinDialog != null && this@CastSearchActivity.pinDialog?.isShowing == true && !this@CastSearchActivity.isFinishing) {
                    this@CastSearchActivity.pinDialog?.dismiss()
                }
                if (this@CastSearchActivity.pinCode != null && this@CastSearchActivity.pinCode?.isShowing == true && !this@CastSearchActivity.isFinishing) {
                    this@CastSearchActivity.pinCode?.dismiss()
                }
            } catch (unused: Exception) {
                unused.printStackTrace()
            }
            AppConstant.connectableDevice = connectableDevice
            showToast("Connected Successfully")
            this@CastSearchActivity.finish()

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onDeviceAdded(manager: DiscoveryManager?, device: ConnectableDevice?) {
        val model = RvConnectionModel(
            name = device?.friendlyName,
            text = "",
            series = device?.modelName,
            deviceIp = device?.ipAddress,
            connectableDevice = device
        )
        Handler(Looper.getMainLooper()).postDelayed({
            if (!this.deviceList.contains(model)) {
                this.deviceList.add(model)
                this.rvAdapter?.notifyDataSetChanged()
                this.checkList()
            }
        }, 1000)
    }

    override fun onDeviceUpdated(manager: DiscoveryManager?, device: ConnectableDevice?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeviceRemoved(manager: DiscoveryManager?, device: ConnectableDevice?) {
        val it: Iterator<RvConnectionModel> = deviceList.iterator()
        while (it.hasNext()) {
            val next = it.next()
            if (next.name == device?.friendlyName) {
                deviceList.remove(next)
                rvAdapter?.notifyDataSetChanged()
                checkList()
                return
            }
        }
    }

    override fun onDiscoveryFailed(manager: DiscoveryManager?, error: ServiceCommandError?) {

    }




    override fun initViews() {
        rvAdapter = RvConnection(deviceList, this) {
            if (it != -1 && it >= 0 && it < this.deviceList.size) {
                val deviceModel: RvConnectionModel = deviceList[it]
                deviceModel.connectableDevice?.addListener(this.deviceListener)
                deviceModel.connectableDevice?.setPairingType(DeviceService.PairingType.PIN_CODE)
                deviceModel.connectableDevice?.connect()
            }

        }
        binding.listdevices.apply {
            adapter = this@CastSearchActivity.rvAdapter
            layoutManager = LinearLayoutManager(this@CastSearchActivity)
        }

        checkList()
        timeOutHandler()
        startSearch()

    }


    private fun getDevices(): HashMap<String, String>? {
        val devicesList = HashMap<String, String>()
//        devicesList["com.connectsdk.service.WebOSTVService"] =
//            "com.connectsdk.discovery.provider.SSDPDiscoveryProvider"
//        devicesList["com.connectsdk.service.NetcastTVService"] = "com.connectsdk.discovery.provider.SSDPDiscoveryProvider"
//        devicesList["com.connectsdk.service.DLNAService"] = "com.connectsdk.discovery.provider.SSDPDiscoveryProvider"
//        devicesList["com.connectsdk.service.DIALService"] = "com.connectsdk.discovery.provider.SSDPDiscoveryProvider"
        devicesList["com.connectsdk.service.RokuService"] = "com.connectsdk.discovery.provider.SSDPDiscoveryProvider"
        devicesList["com.connectsdk.service.CastService"] = "com.connectsdk.discovery.provider.CastDiscoveryProvider"
//        devicesList["com.connectsdk.service.AirPlayService"] = "com.connectsdk.discovery.provider.ZeroconfDiscoveryProvider"
//        devicesList["com.connectsdk.service.FireTVService"] = "com.connectsdk.discovery.provider.FireTVDiscoveryProvider"


        return devicesList
    }


    private fun startSearch() {
        try {
            getDevices()?.forEach { map ->
                try {
                    DiscoveryManager.getInstance().registerDeviceService(
                        Class.forName(map.key) as Class<DeviceService?>,
                        Class.forName(map.value) as Class<DiscoveryProvider?>
                    )
                } catch (exception: java.lang.Exception) {
                    exception.printStackTrace()
                }
            }
            val videoFilter = CapabilityFilter(
                MediaPlayer.Display_Video, MediaControl.Any, VolumeControl.Volume_Up_Down
            )

            val imageCapabilities = CapabilityFilter(
                MediaPlayer.Display_Image
            )
            DiscoveryManager.getInstance().setCapabilityFilters(videoFilter, imageCapabilities)
            DiscoveryManager.getInstance().pairingLevel = DiscoveryManager.PairingLevel.ON
            DiscoveryManager.getInstance().addListener(this)
            DiscoveryManager.getInstance().start()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }





    override fun onDestroy() {
        trackerRunnable?.let { trackerHandler?.removeCallbacks(it) }
        super.onDestroy()
    }

    override fun actions() {

    }


}
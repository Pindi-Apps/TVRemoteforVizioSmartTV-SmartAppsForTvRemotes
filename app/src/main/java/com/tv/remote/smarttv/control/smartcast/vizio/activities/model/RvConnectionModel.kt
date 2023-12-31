package com.tv.remote.smarttv.control.smartcast.vizio.activities.model

import com.connectsdk.device.ConnectableDevice
import java.io.Serial
import java.io.Serializable

data class RvConnectionModel(
    var name: String? = null,
    var text: String? = null,
    var series: String? = null,
    var deviceIp: String? = null,
    var connectableDevice: ConnectableDevice? = null
) : Serializable
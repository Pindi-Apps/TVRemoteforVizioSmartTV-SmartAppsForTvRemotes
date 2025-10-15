package com.tv.remote.smarttv.control.smartcast.vizio.activities

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.tv.remote.smarttv.control.smartcast.vizio.BuildConfig
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseActivity
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.NavLayoutBinding
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.loadImage
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.openWifiSetting
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.shareText

class SideMenuActivity : BaseActivity<NavLayoutBinding>() {

    override fun getViewBinding(): NavLayoutBinding {
        return NavLayoutBinding.inflate(layoutInflater)
    }

    override fun initViews() {

    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(this)) {
            binding.ivWifi.loadImage(R.drawable.ic_wifi_connected, this)
            binding.tvWifiStatus.text = "Wifi connected"
        } else {
            binding.ivWifi.loadImage(R.drawable.ic_wifi_not_connected, this)
            binding.tvWifiStatus.text = "Wifi Disconnected"
        }

    }

    override fun actions() {


        binding.llWifi.setOnClickListener {
            openWifiSetting()
        }

        binding.ivMenu.setOnClickListener {
            this@SideMenuActivity.finish()
        }

        binding.navRate.setOnClickListener { v ->

            try {


                this@SideMenuActivity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                    )
                )

            }catch (exception : Exception){
                exception.printStackTrace()
            }

        }
        binding.navMoreApps.setOnClickListener { v ->
           try {
                this@SideMenuActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.moreAppsLink))))
            }catch (exception : Exception){
                exception.printStackTrace()
            }
        }

        binding.llShare.setOnClickListener {
            shareText()
        }

        binding.ivPrivacy.setOnClickListener {
            openLink(getString(R.string.privacyPolicyLink))
        }

        binding.navHome.setOnClickListener {
            this@SideMenuActivity.finish()
        }


    }

}
package com.tv.remote.smarttv.control.smartcast.vizio.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.Window
import androidx.recyclerview.widget.GridLayoutManager
import com.connectsdk.core.MediaInfo
import com.connectsdk.service.capability.MediaControl
import com.connectsdk.service.capability.MediaPlayer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.GalleryAdapter
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseActivity
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityPhotosCastBinding
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.BottomSheetDesignBinding
import com.tv.remote.smarttv.control.smartcast.vizio.general.ApplicationClass
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.gone
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.loadImage
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.visible

class PhotosCastActivity : BaseActivity<ActivityPhotosCastBinding>() {

    private var listPhotos = ArrayList<GalleryImagesModel>()
    private var currentIndex: Int? = null
    private var mediaPlayer: MediaPlayer? = null
    private var countDownTimer: CountDownTimer? = null


    override fun getViewBinding(): ActivityPhotosCastBinding {
        return ActivityPhotosCastBinding.inflate(layoutInflater)
    }


    override fun initViews() {
        currentIndex = intent.extras?.getInt("pos", 0)
        listPhotos.clear()
        listPhotos.addAll(AppConstant.photosList)
        mediaPlayer = AppConstant.connectableDevice?.getCapability(MediaPlayer::class.java)
        binding.playBtn.visible()
        loadImage()
    }

    override fun actions() {
        binding.llPlay.setOnClickListener {
            if (countDownTimer == null){
                binding.playBtn.gone()
                binding.pauseBtn.visible()
                countDownTimer = object : CountDownTimer(50000000, 4000) {

                    override fun onTick(millisUntilFinished: Long) {
                        if (this@PhotosCastActivity.isFinishing.not()) {
                            currentIndex?.let { index ->
                                if (listPhotos.isNotEmpty()) {
                                    if (index < listPhotos.size) {
                                        currentIndex = index.plus(1)
                                        loadImage()
                                    }
                                }
                            }
                        }
                    }

                    override fun onFinish() {
                        if (this@PhotosCastActivity.isFinishing.not()) {
                            binding.playBtn.visible()
                            binding.pauseBtn.gone()
                        }
                    }

                }.start()
            }else{
                countDownTimer?.cancel()
                binding.playBtn.visible()
                binding.pauseBtn.gone()
            }
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.playListBtn.setOnClickListener {
            val bottomSheet = BottomSheetDesignBinding.inflate(layoutInflater)
            val bottom = BottomSheetDialog(this, R.style.MyTransparentBottomSheetDialogTheme)
            bottom.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            bottom.requestWindowFeature(Window.FEATURE_NO_TITLE)
            bottom.setContentView(bottomSheet.root)
            bottomSheet.bottomSheetRv.apply {
                adapter = GalleryAdapter(this@PhotosCastActivity, listPhotos) {
                    currentIndex = it
                    loadImage()
                    this@PhotosCastActivity.runOnUiThread {
                        bottom.dismiss()
                    }
                }
                layoutManager = GridLayoutManager(this@PhotosCastActivity, 3)
            }
            bottom.show()
        }
        binding.Nextbtn.setOnClickListener {
            countDownTimer?.cancel()
            binding.playBtn.visible()
            binding.pauseBtn.gone()
            currentIndex?.let { index ->
                if (listPhotos.isNotEmpty()) {
                    if (index < listPhotos.size) {
                        currentIndex = index.plus(1)
                        loadImage()
                    }
                }
            }
        }
        binding.previousBtn.setOnClickListener {
            countDownTimer?.cancel()
            binding.playBtn.visible()
            binding.pauseBtn.gone()
            currentIndex?.let { index ->
                if (listPhotos.isNotEmpty()) {
                    if (index < listPhotos.size) {
                        if (index >= 1) {
                            currentIndex = index.minus(1)
                            loadImage()
                        }
                    }
                }
            }
        }
        binding.stopBtn.setOnClickListener {
            if (AppConstant.connectableDevice != null){
                AppConstant.connectableDevice?.getCapability(MediaControl::class.java)?.stop(null)
            }
        }
        binding.castBtn.setOnClickListener {
            disconnectCast {
                AppConstant.connectableDevice?.getCapability(MediaControl::class.java)
                    ?.stop(null)
                AppConstant.connectableDevice?.disconnect()
                AppConstant.connectableDevice = null
                finish()
            }
        }


    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }

    private fun loadImage() {
        if (isNetworkAvailable(this)) {
            currentIndex?.let { index ->
                if (listPhotos.isNotEmpty()) {
                    if (index < listPhotos.size) {
                        val model = listPhotos[index]
                        binding.cardImage.loadImage(model.picturepath, this)
                        if (AppConstant.connectableDevice != null) {
                            (application as? ApplicationClass)?.getHost()
                                ?.putFile(model.picturepath)?.let { link ->
                                    val mediaInfo = MediaInfo.Builder(link, "image/png")
                                        .setTitle(model.pictureName ?: "Image")
                                        .setDescription(model.picturepath ?: "Local Image")
                                        .setIcon(AppConstant.iconUrl).build()
                                    mediaPlayer?.displayImage(mediaInfo, null)
                                }
                        } else {
                            finish()
                        }
                    }
                }
            }

        } else {
            AppConstant.connectableDevice?.disconnect()
            AppConstant.connectableDevice = null
            connectWifi()
        }

    }


}
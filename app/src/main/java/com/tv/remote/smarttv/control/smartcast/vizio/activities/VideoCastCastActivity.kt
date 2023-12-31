package com.tv.remote.smarttv.control.smartcast.vizio.activities

import android.app.Dialog
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.provider.MediaStore
import android.view.Window
import androidx.recyclerview.widget.GridLayoutManager
import com.connectsdk.core.MediaInfo
import com.connectsdk.core.SubtitleInfo
import com.connectsdk.service.capability.MediaControl
import com.connectsdk.service.capability.MediaPlayer
import com.connectsdk.service.capability.VolumeControl
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.GalleryAdapter
import com.tv.remote.smarttv.control.smartcast.vizio.activities.background.AsyncTaskForFiles
import com.tv.remote.smarttv.control.smartcast.vizio.activities.fragments.PhotosFragment
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseActivity
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityPhotosCastBinding
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityVideoCastBinding
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.BottomSheetDesignBinding
import com.tv.remote.smarttv.control.smartcast.vizio.general.ApplicationClass
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.gone
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.loadImage
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.visible
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File

class VideoCastCastActivity : BaseActivity<ActivityVideoCastBinding>() {

    private var listPhotos = ArrayList<GalleryImagesModel>()
    private var currentIndex: Int? = null
    private var mediaPlayer: MediaPlayer? = null
    private var subtitleBuilder: SubtitleInfo.Builder? = null

    private var isMute: Boolean = false


    override fun getViewBinding(): ActivityVideoCastBinding {
        return ActivityVideoCastBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        currentIndex = intent.extras?.getInt("pos", 0)
        listPhotos.clear()
        listPhotos.addAll(AppConstant.videoList)
        mediaPlayer = AppConstant.connectableDevice?.getCapability(MediaPlayer::class.java)
        subtitleBuilder = SubtitleInfo.Builder(
            if (AppConstant.connectableDevice?.hasCapability(MediaPlayer.Subtitle_WebVTT) == true) "http://connectsdk.com/ConnectSDK.vtt" else "http://connectsdk.com/ConnectSDK.srt"
        )
        subtitleBuilder?.setLabel("English")?.setLanguage("en")
        binding.pauseBtn.visible()
        loadImage()
    }

    override fun actions() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.playListBtn.setOnClickListener {
            val bottomSheet = BottomSheetDesignBinding.inflate(layoutInflater)
            val bottom = BottomSheetDialog(this, R.style.MyTransparentBottomSheetDialogTheme)
            try {
                bottom.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                bottom.requestWindowFeature(Window.FEATURE_NO_TITLE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bottom.setContentView(bottomSheet.root)
            bottomSheet.bottomSheetRv.apply {
                adapter = GalleryAdapter(this@VideoCastCastActivity, listPhotos) {
                    currentIndex = it
                    loadImage()
                    this@VideoCastCastActivity.runOnUiThread {
                        bottom.dismiss()
                    }
                }
                layoutManager = GridLayoutManager(this@VideoCastCastActivity, 3)
            }
            bottom.show()
        }
        binding.Nextbtn.setOnClickListener {
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

        binding.playBtn.setOnClickListener {
            binding.playBtn.gone()
            binding.pauseBtn.visible()
            AppConstant.connectableDevice?.getCapability(MediaControl::class.java)?.play(null)
        }

        binding.castBtn.setOnClickListener {
            disconnectCast {
                AppConstant.connectableDevice?.getCapability(MediaControl::class.java)?.stop(null)
                AppConstant.connectableDevice?.disconnect()
                AppConstant.connectableDevice = null
                finish()
            }
        }

        binding.pauseBtn.setOnClickListener {
            binding.pauseBtn.gone()
            binding.playBtn.visible()
            AppConstant.connectableDevice?.getCapability(MediaControl::class.java)?.pause(null)
        }

        binding.stopBtn.setOnClickListener {
            AppConstant.connectableDevice?.getCapability(MediaControl::class.java)?.stop(null)
            finish()
        }

        binding.muteBtn.setOnClickListener {
            isMute = !isMute
            AppConstant.connectableDevice?.getCapability(VolumeControl::class.java)
                ?.setMute(isMute, null)
        }

        binding.volUpBtn.setOnClickListener {
            AppConstant.connectableDevice?.getCapability(VolumeControl::class.java)?.volumeUp(null)
        }

        binding.volDownBtn.setOnClickListener {
            AppConstant.connectableDevice?.getCapability(VolumeControl::class.java)
                ?.volumeDown(null)
        }


    }


    private fun loadImage() {
        if (isNetworkAvailable(this)) {
            if (AppConstant.connectableDevice != null) {
                currentIndex?.let { index ->
                    if (listPhotos.isNotEmpty()) {
                        if (index < listPhotos.size) {
                            val model = listPhotos[index]
                            binding.cardImage.loadImage(model.picturepath, this)
                            (application as? ApplicationClass)?.getHost()
                                ?.putFile(model.picturepath)?.let { link ->
                                    val mediaInfo = MediaInfo.Builder(
                                        link, "video/mp4"
                                    ).setTitle(model.picturepath?:"Video").setDescription(model.picturepath?:"Local Video")
                                        .setIcon(AppConstant.iconUrl)
                                        .setSubtitleInfo(subtitleBuilder?.build()!!).build()
                                    mediaPlayer?.playMedia(mediaInfo, false, null)
                                }
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
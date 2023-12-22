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
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.loadImage
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File

class VideoCastCastActivity : BaseActivity<ActivityVideoCastBinding>() {

    private var listPhotos = ArrayList<GalleryImagesModel>()
    private var currentIndex: Int? = null
    override fun getViewBinding(): ActivityVideoCastBinding {
        return ActivityVideoCastBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        currentIndex = intent.extras?.getInt("pos", 0)
        listPhotos.clear()
        listPhotos.addAll(AppConstant.videoList)
        loadImage()
    }

    override fun actions() {
        binding.icBack.setOnClickListener {
            finish()
        }
        binding.playListBtn.setOnClickListener {
            val bottomSheet = BottomSheetDesignBinding.inflate(layoutInflater)
            val bottom = BottomSheetDialog(this,R.style.MyTransparentBottomSheetDialogTheme)
            bottom.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            bottom.requestWindowFeature(Window.FEATURE_NO_TITLE)
            bottom.setContentView(bottomSheet.root)

//             GalleryAdapter(this, listPhotos) {
//                AppConstant.photosList.clear()
//                AppConstant.photosList.addAll(listPhotos)
//                currentIndex?.let {index->
//                    val model = listPhotos[index]
//                    model.picturepath?.let { path ->
//                        binding.cardImage.loadImage(path, this)
//                        startFileService()
//                    }
//
//
//                }
//            }
            bottomSheet.bottomSheetRv.apply {
                adapter = GalleryAdapter(this@VideoCastCastActivity, listPhotos){
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
//                        if (index >= 1) {
                            currentIndex = index.plus(1)
                            loadImage()
//                        }
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

    }


    private fun loadImage(){
        currentIndex?.let { index ->
            if (listPhotos.isNotEmpty()){
                if (index < listPhotos.size){
                    binding.cardImage.loadImage(listPhotos[index].picturepath , this)
                }
            }
        }
    }



}
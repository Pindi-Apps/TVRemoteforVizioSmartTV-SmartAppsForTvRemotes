package com.tv.remote.smarttv.control.smartcast.vizio.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.GalleryAdapter
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseActivity
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityPhotosCastBinding
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.BottomSheetDesignBinding
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.loadImage

class PhotosCastActivity : BaseActivity<ActivityPhotosCastBinding>() {

    private var listPhotos = ArrayList<GalleryImagesModel>()
    private var currentIndex: Int? = null
    override fun getViewBinding(): ActivityPhotosCastBinding {
        return ActivityPhotosCastBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        currentIndex = intent.extras?.getInt("pos", 0)
        listPhotos.clear()
        listPhotos.addAll(AppConstant.photosList)
        loadImage()
    }

    override fun actions() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.playListBtn.setOnClickListener {
            val bottomSheet = BottomSheetDesignBinding.inflate(layoutInflater)
            val bottom = BottomSheetDialog(this,R.style.MyTransparentBottomSheetDialogTheme)
            bottom.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            bottom.requestWindowFeature(Window.FEATURE_NO_TITLE)
            bottom.setContentView(bottomSheet.root)
            bottomSheet.bottomSheetRv.apply {
                adapter = GalleryAdapter(this@PhotosCastActivity, listPhotos){
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
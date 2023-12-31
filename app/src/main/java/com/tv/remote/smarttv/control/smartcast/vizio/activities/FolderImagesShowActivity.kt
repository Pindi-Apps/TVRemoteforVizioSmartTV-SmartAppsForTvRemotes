package com.tv.remote.smarttv.control.smartcast.vizio.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.Formatter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.cast.Cast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.FolderDetailAdapter
import com.tv.remote.smarttv.control.smartcast.vizio.activities.background.AsyncTaskForFiles
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.ImageFolder
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseActivity
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.ActivityFolderImagesShowBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class FolderImagesShowActivity : BaseActivity<ActivityFolderImagesShowBinding>() {

    var listPhotos: ArrayList<GalleryImagesModel> = ArrayList()
    var folderAdapter: FolderDetailAdapter? = null
    private var taskFiles: TaskForFiles? = null
    private var indexPosition = 0


    override fun getViewBinding(): ActivityFolderImagesShowBinding {
        return ActivityFolderImagesShowBinding.inflate(layoutInflater)
    }

    inner class TaskForFiles(
        private var activity: FolderImagesShowActivity, var data: ImageFolder?, var dialog: Dialog
    ) : AsyncTaskForFiles<Int, Int, ArrayList<GalleryImagesModel>>("PhotoActivity") {


        override fun onPreExecute() {
            super.onPreExecute()
            if (!this@FolderImagesShowActivity.isFinishing) {
                dialog.show()
            }
        }

        override fun doInBackground(vararg params: Int?): ArrayList<GalleryImagesModel> {
            val list = ArrayList<GalleryImagesModel>()
            try {
                val cursor: Cursor? = activity.applicationContext.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.SIZE
                    ), MediaStore.Images.Media.DATA + " like ? ", arrayOf(
                        "%${data?.path}%"
                    ), null
                )
                cursor?.moveToFirst()
                do {
                    val pic = GalleryImagesModel()
                    pic.pictureName =
                        cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                            ?.let { cursor.getString(it) }
                    pic.picturepath = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        ?.let { cursor.getString(it) }
                    pic.pictureSize = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                        ?.let { cursor.getString(it) }
                    pic.file = pic.picturepath?.let { File(it) }
                    list.add(pic)
                } while (cursor?.moveToNext() == true)
                cursor?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onPostExecute(result: ArrayList<GalleryImagesModel>?) {
            if (!this@FolderImagesShowActivity.isFinishing) {
                activity.runOnUiThread {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    }
                    result?.let { res ->
                        listPhotos.clear()
                        listPhotos.addAll(res)
                        folderAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun initViews() {

        listPhotos = ArrayList()
        folderAdapter = FolderDetailAdapter(this, listPhotos) {
            if (listPhotos.isNotEmpty()) {
                if (AppConstant.connectableDevice != null) {
                    indexPosition = it
                    AppConstant.photosList.clear()
                    AppConstant.photosList.addAll(listPhotos)
                    goToNextWithData(PhotosCastActivity::class.java, indexPosition)
                } else {
                    if (isNetworkAvailable(this)){
                        goToNext(CastSearchActivity::class.java)
                    }else{
                        connectWifi()
                    }
                }
            }
        }

        binding.rvAdapter.apply {
            layoutManager = GridLayoutManager(this@FolderImagesShowActivity, 3)
            adapter = folderAdapter
        }
        binding.folderName.text = AppConstant.imageFolderPath?.FolderName
        startFileService()


    }

    @DelicateCoroutinesApi
    fun startFileService() {
        if (taskFiles?.status == AsyncTaskForFiles.StatusFiles.RUNNING) {
            taskFiles?.cancel(true)
        }

        showProgressDialog { dialog ->
            taskFiles = TaskForFiles((this), AppConstant.imageFolderPath, dialog)
            taskFiles?.execute()
        }
    }

    override fun onDestroy() {
        taskFiles?.let {
            stopFileService()
        }
        super.onDestroy()
    }

    fun details() {

    }


    @OptIn(DelicateCoroutinesApi::class)
    fun stopFileService() {
        if (taskFiles?.status == AsyncTaskForFiles.StatusFiles.RUNNING) {
            taskFiles?.cancel(true)
        }
    }

    override fun actions() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}
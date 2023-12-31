package com.tv.remote.smarttv.control.smartcast.vizio.activities.fragments

import android.database.Cursor
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import com.tv.remote.smarttv.control.smartcast.vizio.activities.FolderImagesShowActivity
import com.tv.remote.smarttv.control.smartcast.vizio.activities.PhotosActivity
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.GallaryAdapter2
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.ImageFolder
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.FragmentPhotosFolderBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import tvremote.forroku.smart.tvremotecontrol.background.AsyncTaskForFolder
import tvremote.forroku.smart.tvremotecontrol.base.BaseFragment

class PhotosFolderFragment : BaseFragment<FragmentPhotosFolderBinding>() {

    var listFolder = ArrayList<ImageFolder>()
    var rvAdapter: GallaryAdapter2? = null
    private var taskFolder: TaskForFolder? = null

    override fun getViewBinding(): FragmentPhotosFolderBinding {
        return FragmentPhotosFolderBinding.inflate(layoutInflater)
    }

    inner class TaskForFolder(
        private var activity: PhotosActivity, var fragment: PhotosFolderFragment
    ) : AsyncTaskForFolder<Int, Int, ArrayList<ImageFolder>>("PhotosFolder") {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Int?): ArrayList<ImageFolder> {
            val picFolder = ArrayList<ImageFolder>()
            val picPath = ArrayList<String>()

            try {
                val cursor: Cursor? = activity.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.BUCKET_ID,
                    ), null, null, null
                )
                cursor?.moveToFirst()
                do {
                    val folds = ImageFolder()
                    val folder =
                        cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                            ?.let { cursor?.getString(it) }
                    val dataPath = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        ?.let { cursor?.getString(it) }
                    var folderPath = dataPath?.lastIndexOf("$folder/")?.let {
                        if (it != -1) {
                            dataPath.substring(0, it)
                        } else {
                            ""
                        }
                    }
                    folderPath = "$folderPath$folder/"
                    if (!picPath.contains(folderPath)) {
                        picPath.add(folderPath)
                        folds.path = folderPath
                        folds.FolderName = folder
                        folds.firstPic = dataPath
                        folds.addpics()
                        picFolder.add(folds)
                    } else {
                        for (i in picFolder.indices) {
                            if (picFolder[i].path.equals(folderPath)) {
                                picFolder[i].firstPic = dataPath
                                picFolder[i].addpics()
                            }
                        }
                    }
                } while (cursor?.moveToNext() == true)
                cursor?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return picFolder
        }

        override fun onPostExecute(result: ArrayList<ImageFolder>?) {
            activity.runOnUiThread {
                result?.let { res ->
                    fragment.listFolder.clear()
                    fragment.listFolder.addAll(res)
                    fragment.rvAdapter?.notifyDataSetChanged()
                }
            }
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startFolderService() {
        if (taskFolder?.status == AsyncTaskForFolder.Status.RUNNING) {
            taskFolder?.cancel(true)
        }
        taskFolder = TaskForFolder((activity as PhotosActivity), this)
        taskFolder?.execute()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun stoptFolderService() {
        if (taskFolder?.status == AsyncTaskForFolder.Status.RUNNING) {
            taskFolder?.cancel(true)
        }
    }

    override fun initViews() {

        rvAdapter = GallaryAdapter2(requireActivity(), listFolder) {
            if (it != -1) {
                if (listFolder.isNotEmpty()) {
                    AppConstant.imageFolderPath = listFolder[it]
                    goToNext(FolderImagesShowActivity::class.java)
                }
            }
        }
        binding.rvAdapter.apply {
            adapter = rvAdapter
            layoutManager = GridLayoutManager(requireActivity(), 3)
        }

        startFolderService()
    }

    override fun clicks() {
    }

    override fun onDestroy() {
        super.onDestroy()
        taskFolder?.let {
            stoptFolderService()
        }

    }


    override fun attachViewModel() {
    }
}
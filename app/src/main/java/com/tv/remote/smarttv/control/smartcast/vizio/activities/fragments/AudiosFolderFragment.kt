package com.tv.remote.smarttv.control.smartcast.vizio.activities.fragments

import android.database.Cursor
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import com.tv.remote.smarttv.control.smartcast.vizio.activities.AudiosActivity
import com.tv.remote.smarttv.control.smartcast.vizio.activities.FolderAudiosShowActivity
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.AudioAdapter2
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.ImageFolder
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.FragmentAudiosFolderBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import com.tv.remote.smarttv.control.smartcast.vizio.activities.background.AsyncTaskForFolder
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseFragment

class AudiosFolderFragment : BaseFragment<FragmentAudiosFolderBinding>() {

    var listFolder = ArrayList<ImageFolder>()
    var rvAdapter: AudioAdapter2? = null
    private var taskFolder: TaskForFolder? = null
    private var indexPosition = 0

    override fun getViewBinding(): FragmentAudiosFolderBinding {
        return FragmentAudiosFolderBinding.inflate(layoutInflater)
    }

    inner class TaskForFolder(
        private var activity: AudiosActivity, var fragment: AudiosFolderFragment
    ) : AsyncTaskForFolder<Int, Int, ArrayList<ImageFolder>>("PhotosFolder") {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Int?): ArrayList<ImageFolder> {
            val picFolder = ArrayList<ImageFolder>()
            val picPath = ArrayList<String>()

            try {
                val cursor: Cursor? = activity.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Audio.Media.BUCKET_ID,
                    ), null, null, null
                )
                cursor?.moveToFirst()
                do {
                    val folds = ImageFolder()
                    val folder =
                        cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
                            ?.let { cursor?.getString(it) }
                    val dataPath = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
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
        taskFolder = TaskForFolder((activity as AudiosActivity), this)
        taskFolder?.execute()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun stoptFolderService() {
        if (taskFolder?.status == AsyncTaskForFolder.Status.RUNNING) {
            taskFolder?.cancel(true)
        }
    }

    override fun initViews() {

        rvAdapter = AudioAdapter2(requireActivity(), listFolder) {
            if (it != -1) {
                if (listFolder.isNotEmpty()) {
                    AppConstant.imageFolderPath = listFolder[it]
                    goToNext(FolderAudiosShowActivity::class.java)
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
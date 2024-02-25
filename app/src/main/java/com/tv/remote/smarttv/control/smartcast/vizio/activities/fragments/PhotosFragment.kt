package com.tv.remote.smarttv.control.smartcast.vizio.activities.fragments

import android.app.Dialog
import android.database.Cursor
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import com.tv.remote.smarttv.control.smartcast.vizio.activities.CastSearchActivity
import com.tv.remote.smarttv.control.smartcast.vizio.activities.PhotosActivity
import com.tv.remote.smarttv.control.smartcast.vizio.activities.PhotosCastActivity
import com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter.GalleryAdapter
import com.tv.remote.smarttv.control.smartcast.vizio.activities.background.AsyncTaskForFiles
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.activities.wifi.AppConstant
import com.tv.remote.smarttv.control.smartcast.vizio.databinding.FragmentPhotosBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import com.tv.remote.smarttv.control.smartcast.vizio.base.BaseFragment
import java.io.File

class PhotosFragment : BaseFragment<FragmentPhotosBinding>() {

    var listPhotos = ArrayList<GalleryImagesModel>()
    var imagesAdapter: GalleryAdapter? = null
    private var taskFiles: TaskForFiles? = null
    private var currentIndex: Int = -1


    override fun getViewBinding(): FragmentPhotosBinding {
        return FragmentPhotosBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        imagesAdapter = GalleryAdapter(requireActivity(), listPhotos) {
            if (listPhotos.isNotEmpty()) {
                if (AppConstant.connectableDevice != null) {
                    currentIndex = it
                    AppConstant.photosList.clear()
                    AppConstant.photosList.addAll(listPhotos)
                    goToNextWithData(PhotosCastActivity::class.java, currentIndex)
                } else {
                    if (isNetworkAvailable(requireActivity())){
                        goToNext(CastSearchActivity::class.java)
                    }else{
                        connectWifi()
                    }
                }
            }
        }

        binding.photosRv.apply {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = imagesAdapter


        }
        startFileService()
    }

    inner class TaskForFiles(
        var activity: PhotosActivity,
        var fragment: PhotosFragment,
        var dialog: Dialog
    ) :
        AsyncTaskForFiles<Int, Int, ArrayList<GalleryImagesModel>>("Images Activity") {
        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: Int?): ArrayList<GalleryImagesModel> {
            val list = ArrayList<GalleryImagesModel>()
            try {
                val cursor: Cursor? = activity.applicationContext.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.SIZE
                    ), null, null, null
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

        override fun onPostExecute(result: ArrayList<GalleryImagesModel>?) {
            if (!activity.isFinishing) {
                activity.runOnUiThread {

                }
                result?.let { res ->
                    listPhotos.clear()
                    listPhotos.addAll(res)
                    fragment.imagesAdapter?.notifyDataSetChanged()
                }
            }
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startFileService() {
        if (taskFiles?.status == AsyncTaskForFiles.StatusFiles.RUNNING) {
            taskFiles?.cancel(true)
        }

        showProgressDialog { dialog ->
            taskFiles = TaskForFiles((activity as PhotosActivity), this, dialog)
            taskFiles?.execute()
        }


    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun stopFilesService() {
        if (taskFiles?.status == AsyncTaskForFiles.StatusFiles.RUNNING) {
            taskFiles?.cancel(true)
        }
    }

    override fun clicks() {
    }

    override fun onDestroy() {
        taskFiles?.let {
            stopFilesService()
        }
        super.onDestroy()
    }

    override fun attachViewModel() {
    }
//    fun details() {
//        if (currentIndex != -1) {
//            if (listPhotos.isNotEmpty()) {
//                val model = listPhotos[currentIndex]
//                val bottomSheet: BottomSheetBinding = BottomSheetBinding.inflate(layoutInflater)
//                val bottom = BottomSheetDialog(requireActivity())
//                bottom.setContentView(bottomSheet.root)
//                bottom.show()
//                val simpleDateFormat = SimpleDateFormat("dd:MM:yyyy", Locale.ENGLISH)
//                val date = simpleDateFormat.format(model.file?.lastModified()).toString()
//                bottomSheet.tvName.text = model.pictureName
//                bottomSheet.tvPath.text = model.picturepath
//                bottomSheet.tvDate.text = date
//                val size = model.file?.length()?.let {
//                    Formatter.formatFileSize(
//                        requireActivity(),
//                        it
//                    )
//                }
//                bottomSheet.tvSize.text = size?.toString()
//            }
//        }
//    }

//    fun sharePhoto(){
//        if (indexPosition != -1){
//            if (listPhotos.isNotEmpty()){
//                val model = listPhotos[indexPosition]
//                try {
//                    val share = Intent(Intent.ACTION_SEND)
//                    share.type = "image/jpeg"
//                    share.putExtra(
//                        Intent.EXTRA_STREAM,
//                        Uri.parse(model.picturepath)
//                    )
//                    share.putExtra(
//                        Intent.EXTRA_TEXT,
//                        "Install this application for Roku Media Casting to TV \nhttps://play.google.com/store/apps/details?id=${requireActivity().packageName}"
//                    )
//                    startActivity(Intent.createChooser(share, "Share Image"))
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    e.message?.let { showToast(it) }
//                }
//            }
//        }
//    }

//    fun castToPhone() {
//        if (currentIndex != -1) {
//            if (listPhotos.isNotEmpty()) {
//                val model = listPhotos[currentIndex]
//                var localPhoto: File? = null
//                model.file?.let { AppConstant.localPhoto = it }
//                goToNext(PhotosCastPhoneActivity::class.java)
//            }
//        }
//    }
//    fun castToTv(){
//        listPhotos.let {
//            if(AppConstant.connectableDevice != null){
//                    AppConstant.photosList.clear()
//                    AppConstant.photosList.addAll(it)
//                    goToNextWithData(CastPhotosActivity::class.java,currentIndex)
//            }else{
//                if (isNetworkAvailable(requireActivity())){
//                    goToNext(SearchingActivity::class.java)
//                }else{
//                    connectWifi()
//                }
//            }
//        }
//    }
}

package com.tv.remote.smarttv.control.smartcast.vizio.utils.general

import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.ImageFolder
import java.io.File


class AppConstant {

    companion object {
        const val currentDateFormat = "dd-MM-yyyy"
        var imageFolderPath: ImageFolder? = null
        var Vibration:Boolean=false
        var sound:Boolean=false


        var localPhoto: File? = null
        var localVideo: File? = null
        var localAudio: File? = null

        var URL_GET_INFORMATION = "http://192.168.100.9:8001"
        var isAvailableRemote: Boolean = false

        var photosList=ArrayList<GalleryImagesModel>()
        var videoList=ArrayList<GalleryImagesModel>()
        var audioList=ArrayList<GalleryImagesModel>()
        const val iconUrl = "https://play-lh.googleusercontent.com/dQnO7XQnAgpNCfuvDnAFwyGfwWzvX8UCQheArEi3-PJcZeNVxh8w2Q8v6Mx3c7swfZM=w480-h960-rw"

        var token : String? = null




    }


}
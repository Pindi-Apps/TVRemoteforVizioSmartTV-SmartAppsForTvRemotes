package com.tv.remote.smarttv.control.smartcast.vizio.activities.model

import java.io.Serializable

data class ImageFolder(
    var path: String? = null,
    var FolderName: String? = null,
    var numberOfPics: Int = 0,
    var firstPic: String? = null
) :  Serializable{
    fun addpics() {
        this.numberOfPics++
    }
}

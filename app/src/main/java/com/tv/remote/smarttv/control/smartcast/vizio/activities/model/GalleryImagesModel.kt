package com.tv.remote.smarttv.control.smartcast.vizio.activities.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.cast.MediaInfo
import java.io.File

class GalleryImagesModel(
    var pictureName: String?=null,
    var picturepath: String?=null,
    var pictureSize: String?=null,
    var imageUri: String?=null,
    var file: File? = null,
    var duration : String?=null,
    var mediaInfo: MediaInfo?= null,
    var url : String?= null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("file"),
        parcel.readString(),
        parcel.readParcelable(MediaInfo::class.java.classLoader),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pictureName)
        parcel.writeString(picturepath)
        parcel.writeString(pictureSize)
        parcel.writeString(imageUri)
        parcel.writeString(duration)
        parcel.writeParcelable(mediaInfo, flags)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GalleryImagesModel> {
        override fun createFromParcel(parcel: Parcel): GalleryImagesModel {
            return GalleryImagesModel(parcel)
        }

        override fun newArray(size: Int): Array<GalleryImagesModel?> {
            return arrayOfNulls(size)
        }
    }
}
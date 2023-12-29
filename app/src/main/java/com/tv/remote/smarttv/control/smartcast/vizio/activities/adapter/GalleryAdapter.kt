package com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.loadImage

class GalleryAdapter(
    var activity: Activity,
    var list: ArrayList<GalleryImagesModel>,
    private var click: (Int) -> Unit
) : RecyclerView.Adapter<PhotosViewHodler>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHodler {
        val view = LayoutInflater.from(activity).inflate(R.layout.photos_design, parent, false)
        return PhotosViewHodler(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PhotosViewHodler, position: Int) {
        val get = list[position]
        get.picturepath?.let {
            holder.image.loadImage(it, activity)
        }
        holder.itemView.setOnClickListener {
            click.invoke(position)
        }


    }
}

class PhotosViewHodler(view: View) : RecyclerView.ViewHolder(view) {
    var image: ImageView = view.findViewById(R.id.cvImage)

}

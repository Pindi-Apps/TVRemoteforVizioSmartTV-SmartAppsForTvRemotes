package com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.loadImage

class AudioAdapter(
    var activity: Activity,
    var list: ArrayList<GalleryImagesModel>,
    var click: (Int) -> Unit
) : RecyclerView.Adapter<AudiosViewHodler>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudiosViewHodler {
        var view = LayoutInflater.from(activity).inflate(R.layout.audios_design, parent, false)
        return AudiosViewHodler(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AudiosViewHodler, position: Int) {
        var get = list[position]
//        get.picturepath?.let {
//            holder.image.loadImage(it, activity)
//        }
            holder.name.text=get.pictureName
            holder.detail.text=get.picturepath
        holder.itemView.setOnClickListener {
            click.invoke(position)
        }


    }
}

class AudiosViewHodler(view: View) : RecyclerView.ViewHolder(view) {
//    var image = view.findViewById<ImageView>(R.id.cvImage)
    var name = view.findViewById<TextView>(R.id.VideoTextName)
    var detail = view.findViewById<TextView>(R.id.tvDetail)

}

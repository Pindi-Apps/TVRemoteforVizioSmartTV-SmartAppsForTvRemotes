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
    private var activity: Activity,
    private var list: ArrayList<GalleryImagesModel>,
    private var click: (Int) -> Unit
) : RecyclerView.Adapter<AudiosViewHodler>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudiosViewHodler {
        val view = LayoutInflater.from(activity).inflate(R.layout.audios_design, parent, false)
        return AudiosViewHodler(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AudiosViewHodler, position: Int) {
        val get = list[position]
        holder.name.text = get.pictureName
        holder.detail.text = get.picturepath
        holder.itemView.setOnClickListener {
            click.invoke(position)
        }


    }
}

class AudiosViewHodler(view: View) : RecyclerView.ViewHolder(view) {
    var name = view.findViewById<TextView>(R.id.VideoTextName)
    var detail = view.findViewById<TextView>(R.id.tvDetail)

}

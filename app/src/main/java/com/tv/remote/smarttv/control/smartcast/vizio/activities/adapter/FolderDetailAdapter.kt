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
import kotlin.collections.ArrayList

class FolderDetailAdapter(private var activiy:Activity, var list:ArrayList<GalleryImagesModel>, private var click:(Int)->Unit):
    RecyclerView.Adapter<FolderViewHOlder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHOlder {
        var view=LayoutInflater.from(activiy).inflate(R.layout.photos_design,parent,false)
        return FolderViewHOlder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FolderViewHOlder, position: Int) {
        var get=list[position]
        get.picturepath.let {
            holder.image.loadImage(it,activiy)
        }
        holder.itemView.setOnClickListener {
            click.invoke(position)
        }
    }
}

class FolderViewHOlder(view: View):RecyclerView.ViewHolder(view) {
    var image=view.findViewById<ImageView>(R.id.cvImage)
}

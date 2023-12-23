package com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.ImageFolder
import com.tv.remote.smarttv.control.smartcast.vizio.utils.extension.loadImage

class AudioAdapter2(var activity: Activity, var list:ArrayList<ImageFolder>, var click:(Int)->Unit): RecyclerView.Adapter<AudioAdapter2ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioAdapter2ViewHolder {
        var view =LayoutInflater.from(activity).inflate(R.layout.rv_folder_audio_design,parent,false)
        return AudioAdapter2ViewHolder(view)
    }

    override fun getItemCount(): Int {
    return list.size
    }

    override fun onBindViewHolder(holder: AudioAdapter2ViewHolder, position: Int) {
        var get=list[position]
        holder.textName.text=get.FolderName.toString()
//        get.firstPic.let {
//            holder.Image.loadImage(it,activity)
//        }
        holder.itemView.setOnClickListener {
            click.invoke(position)
        }
    }
}

class AudioAdapter2ViewHolder(view: View):RecyclerView.ViewHolder(view) {
    var textName=view.findViewById<TextView>(R.id.tvFolderName)
    var Image=view.findViewById<ImageView>(R.id.folderImage)

}

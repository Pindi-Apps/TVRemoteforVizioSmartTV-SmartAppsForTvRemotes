package com.tv.remote.smarttv.control.smartcast.vizio.activities.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tv.remote.smarttv.control.smartcast.vizio.R
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.RvConnectionModel

class RvConnection(
    private var arrayList: ArrayList<RvConnectionModel>,
    private var activity: Activity,
    private var click: (Int) -> Unit
) : RecyclerView.Adapter<RvConnectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvConnectionViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.list_item_vizio, parent, false)
        return RvConnectionViewHolder(view)
    }
    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RvConnectionViewHolder, position: Int) {
        val model = arrayList[position]
        holder.title.text = model.name
        holder.ipAddress.text = model.deviceIp
        holder.itemView.setOnClickListener {
            click.invoke(position)
        }
    }
}
class  RvConnectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val title: TextView = itemView.findViewById(R.id.DeviceName)
    val ipAddress: TextView = itemView.findViewById(R.id.DeviceIP)
}
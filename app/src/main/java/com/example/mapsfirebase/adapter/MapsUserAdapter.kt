package com.example.mapsfirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapsfirebase.R
import com.example.mapsfirebase.model.Maps
import kotlinx.android.synthetic.main.item_user.view.*

class MapsUserAdapter(
    private val data: List<Maps>?,
    private val itemClick: OnClickListener
) : RecyclerView.Adapter<MapsUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapsUserAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data?.get(position)

        holder.bind(item)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Maps?) {
            itemView.itemUser_nama.text = item?.nama
            itemView.itemUser_nama2.text = item?.nama2
            itemView.itemUser_lat.text = "Latitude : " + item?.lat
            itemView.itemUser_lot.text = "Longtitude : " + item?.lon

            itemView.setOnClickListener {
                itemClick.detail(item)
            }
        }
    }

    interface OnClickListener {
        fun detail(item: Maps?)
    }
}
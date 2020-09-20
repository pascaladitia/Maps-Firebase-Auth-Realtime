package com.example.mapsfirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapsfirebase.R
import com.example.mapsfirebase.model.Maps
import kotlinx.android.synthetic.main.item_admin.view.*

class MapsAdminAdapter(
    private val data: List<Maps>?,
    private val itemClick: OnClickListener
) : RecyclerView.Adapter<MapsAdminAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapsAdminAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data?.get(position)

        holder.bind(item)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Maps?) {
            itemView.item_nama.text = item?.nama
            itemView.item_nama2.text = item?.nama2
            itemView.item_lat.text = "Latitude : " + item?.lat
            itemView.item_lot.text = "Longtitude : " + item?.lon

            itemView.btn_itemUpdate.setOnClickListener {
                itemClick.update(item)
            }

            itemView.btn_itemDelete.setOnClickListener {
                itemClick.delete(item)
            }
        }

    }

    interface OnClickListener {
        fun update(item: Maps?)
        fun delete(item: Maps?)
    }
}
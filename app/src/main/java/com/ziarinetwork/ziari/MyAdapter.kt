package com.ziarinetwork.ziari

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val imagesList: ArrayList<PhotoModel>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private val imageList: ArrayList<PhotoModel> = imagesList

    fun getImageList(): ArrayList<PhotoModel> {
        return imageList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = imageList[position]

//        holder.image.setImageResource(currentItem.photo)
        holder.description.text = currentItem.desription

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        var image : ImageView = itemView.findViewById(R.id.imageView)
        var description: TextView = itemView.findViewById(R.id.textView)

    }

}
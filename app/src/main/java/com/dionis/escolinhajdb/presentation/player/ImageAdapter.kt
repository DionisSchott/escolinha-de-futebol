package com.dionis.escolinhajdb.presentation.player

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dionis.escolinhajdb.databinding.ImageLayoutBinding


class ImageAdapter : RecyclerView.Adapter<ImageAdapter.Holder>() {


    private var list: MutableList<Uri> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = ImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.bind(item, position)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<Uri>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }



    override fun getItemCount(): Int {
        return list.size
    }

    class Holder(
        val binding: ImageLayoutBinding,

    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Uri, position: Int) {
            binding.image.setImageURI(item)
            binding.cancel.setOnClickListener {

            }
        }

    }
}
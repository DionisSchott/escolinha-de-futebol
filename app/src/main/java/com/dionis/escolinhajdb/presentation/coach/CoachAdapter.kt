package com.dionis.escolinhajdb.presentation.coach

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.databinding.ItemPlayer2Binding
import com.dionis.escolinhajdb.databinding.ItemPlayerBinding
import com.squareup.picasso.Picasso


class CoachAdapter : RecyclerView.Adapter<CoachAdapter.Holder>() {

    lateinit var onItemClicked: (Coach) -> Unit
    private var coachList: MutableList<Coach> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemPlayer2Binding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), onItemClicked)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Coach>) {
        coachList.clear()
        coachList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(coachList[position])
    }

    override fun getItemCount(): Int {
        return coachList.size
    }


    class Holder(
        private val binding: ItemPlayer2Binding,
        private val onItemClicked: (Coach) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(info: Coach) {

            val fullName = info.name
            val firstName = fullName.split(" ")[0]
            binding.tvName.text = firstName


            if (info.photo.isNotEmpty()) {
                Picasso.get().load(info.photo).into(binding.image)
            } else {
                binding.image.setImageResource(R.drawable.person_)
            }

            binding.root.setOnClickListener {
                onItemClicked.invoke(info)
            }

        }
    }

}
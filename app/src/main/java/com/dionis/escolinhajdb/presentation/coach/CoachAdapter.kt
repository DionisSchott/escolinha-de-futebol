package com.dionis.escolinhajdb.presentation.coach

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.databinding.ItemPlayerBinding


class CoachAdapter : RecyclerView.Adapter<CoachAdapter.Holder>() {

    lateinit var onItemClicked: (Coach) -> Unit
    private var coachList: MutableList<Coach> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemPlayerBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), onItemClicked)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Coach>) {
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
        private val binding: ItemPlayerBinding,
        private val onItemClicked: (Coach) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(info: Coach) {
            binding.tvName.text = info.name
            binding.tvCategory.visibility = View.GONE

            binding.root.setOnClickListener {
                onItemClicked.invoke(info)
            }

            }
        }

}
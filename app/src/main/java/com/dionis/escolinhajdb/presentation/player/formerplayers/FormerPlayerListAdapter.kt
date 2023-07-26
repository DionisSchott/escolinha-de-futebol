package com.dionis.escolinhajdb.presentation.player.formerplayers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.ItemFormerPlayerBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class FormerPlayerListAdapter : RecyclerView.Adapter<FormerPlayerListAdapter.Holder>() {

    lateinit var onItemClicked: (Player) -> Unit
    private var playerList: MutableList<Player> = ArrayList()
    lateinit var onLongItemClicked: (Player) -> Unit


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemFormerPlayerBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), onItemClicked, onLongItemClicked)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Player>) {
        playerList.clear()
        playerList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(playerList[position])
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    class Holder(
        private val binding: ItemFormerPlayerBinding,
        private val onItemClicked: (Player) -> Unit,
        private val onLongItemClicked: (Player) -> Unit,

        ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var playerInfo: Player

        fun bind(information: Player) {
            this.playerInfo = information

            if (playerInfo.images.isNotEmpty()) {
                Picasso.get().load(playerInfo.images).into(binding.image)
            }

            binding.tvName.text = playerInfo.preferredName

            val startDateFormat = SimpleDateFormat("dd/MM/yyyy")
            binding.tvMemberSince.text = startDateFormat.format(playerInfo.startDate!!)

            val departureDateFormat = SimpleDateFormat("dd/MM/yyyy")
            binding.tvMemberUntil.text = departureDateFormat.format(playerInfo.departureDate!!)

            binding.root.setOnClickListener {
                onItemClicked.invoke(playerInfo)
            }
            binding.root.setOnLongClickListener {
                onLongItemClicked.invoke(playerInfo)
                true
            }

        }
    }
}

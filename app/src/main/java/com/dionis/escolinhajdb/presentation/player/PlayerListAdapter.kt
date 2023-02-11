package com.dionis.escolinhajdb.presentation.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.ItemPlayer2Binding
import com.squareup.picasso.Picasso

class PlayersListAdapter : RecyclerView.Adapter<PlayersListAdapter.Holder>() {

    lateinit var onItemClicked: (Player) -> Unit
    private var playerList: MutableList<Player> = ArrayList()
    lateinit var onLongItemClicked: (Player) -> Unit


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemPlayer2Binding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), onItemClicked, onLongItemClicked)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Player>) {
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
        private val binding: ItemPlayer2Binding,
        private val onItemClicked: (Player) -> Unit,
        private val onLongItemClicked: (Player) -> Unit,

        ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var playerInfo: Player

        fun bind(information: Player) {
            this.playerInfo = information

            if (playerInfo.images.isNotEmpty()) {
                Picasso.get().load(playerInfo.images[0]).into(binding.imgView)
            }

            binding.tvName.text = playerInfo.playerName
            binding.tvAge.text = playerInfo.playersBirth
            binding.tvWeight.text = playerInfo.weight.toString() + "Kg"
            (playerInfo.height.toString() + "M").also { binding.tvHeight.text = it }
            binding.tvPosition.text = playerInfo.position
            binding.tvCategory.text = playerInfo.category
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

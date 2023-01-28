package com.dionis.escolinhajdb.presentation.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.ItemPlayerBinding
import com.squareup.picasso.Picasso

class PlayersAdapter : RecyclerView.Adapter<PlayersAdapter.Holder>() {

    lateinit var onItemClicked: (Player) -> Unit
    private var playerList: MutableList<Player> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemPlayerBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), onItemClicked)
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
        private val binding: ItemPlayerBinding,
        private val onItemClicked: (Player) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var playerInfo: Player

        fun bind(information: Player) {
            this.playerInfo = information

            if (playerInfo.images.isNotEmpty()) {
                Picasso.get().load(playerInfo.images[0]).into(binding.imgPlayer)
            }
            binding.tvNamePlayer.text = playerInfo.playerName
            binding.root.setOnClickListener {
                onItemClicked.invoke(playerInfo)
            }
        }
    }
}

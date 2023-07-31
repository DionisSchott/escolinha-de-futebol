package com.dionis.escolinhajdb.presentation.events

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dionis.escolinhajdb.data.model.Events
import com.dionis.escolinhajdb.databinding.ItemEventBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.Holder>() {


    lateinit var onItemClicked: (Events) -> Unit
    private var eventsList: MutableList<Events> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemEventBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), onItemClicked)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Events>) {
        eventsList.clear()
        eventsList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(eventsList[position])
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    class Holder(
        private val binding: ItemEventBinding,
        private val onItemClicked: (Events) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var eventInfo: Events


        fun bind(information: Events) {
            this.eventInfo = information

            binding.eventType.text = eventInfo.eventType
            binding.eventName.text = eventInfo.title
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
            binding.eventDate.text = date.format(eventInfo.date!!)


            binding.root.setOnClickListener{
                onItemClicked.invoke(eventInfo)

            }

        }
    }

}

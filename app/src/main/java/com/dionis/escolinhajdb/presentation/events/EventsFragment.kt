package com.dionis.escolinhajdb.presentation.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Events
import com.dionis.escolinhajdb.databinding.FragmentEventsListBinding
import com.dionis.escolinhajdb.util.Extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventsFragment : Fragment() {

    private lateinit var binding: FragmentEventsListBinding
    private val viewModel: EventsViewModel by viewModels()
    private lateinit var eventsAdapter: EventsAdapter
    private var eventsList: List<Events> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEventsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getEvents()

        setup()
    }

    private fun setup() {
        setObservers()
        setAdapters()
    }

    private fun setObservers() {

        viewModel.events.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.Loading -> {
                }
                is UiState.Failure -> {
                }
                is UiState.Success -> {
                    eventsAdapter.updateList(it.data)
                    eventsList = it.data
                }
            }
        }
    }

    private fun setAdapters() {
        eventsAdapter = EventsAdapter()

        val layoutManager = LinearLayoutManager(requireContext(), GridLayoutManager.VERTICAL, false)

        val recyclerView = binding.recyclerViewEventsList
        recyclerView.adapter = eventsAdapter
        recyclerView.layoutManager = layoutManager


        eventsAdapter.onItemClicked = {
          //  navigateFromEventDetail(it)
            toast("click")
        }

    }

    private fun navigateFromEventDetail(event: Events) {

    }

}
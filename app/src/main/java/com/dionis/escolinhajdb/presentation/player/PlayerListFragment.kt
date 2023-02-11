package com.dionis.escolinhajdb.presentation.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.databinding.FragmentPlayerListBinding
import com.dionis.escolinhajdb.util.Extensions.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerListFragment : Fragment() {

    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var playersListAdapter: PlayersListAdapter
    private val viewModel: PlayerViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlayers()
        setUp()
    }

    private fun setUp() {

        setUpAdapter()
        setObservers()
    }

    private fun setObservers() {

        viewModel.player.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {

                }
                is UiState.Success -> {

                    playersListAdapter.updateList(it.data)
                }
            }
        }
    }


    private fun setUpAdapter() {
        playersListAdapter = PlayersListAdapter()
        playersListAdapter.onItemClicked = {

        }
        playersListAdapter.onLongItemClicked = {
            toast("long click")
        }
        binding.recyclerViewPlayerList.adapter = playersListAdapter


    }

}
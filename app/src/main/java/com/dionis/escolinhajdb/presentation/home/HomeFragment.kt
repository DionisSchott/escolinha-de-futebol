package com.dionis.escolinhajdb.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.databinding.FragmentHomeBinding
import com.dionis.escolinhajdb.presentation.player.PlayerViewModel
import com.dionis.escolinhajdb.presentation.player.PlayersAdapter
import com.dionis.escolinhajdb.util.Extensions.navTo
import com.dionis.escolinhajdb.util.UserManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dados: String
    private lateinit var playersAdapter: PlayersAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getPlayers()
        setUp()

    }

    private fun setUp() {

        binding.tvPlayers.setOnClickListener {
            navTo(R.id.action_homeFragment_to_registerPlayerFragment)
        }
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
                    playersAdapter.updateList(it.data)
                }
            }
        }
    }



    private fun setUpAdapter() {

        playersAdapter = PlayersAdapter()

        playersAdapter.onItemClicked = {
            val args = Bundle().apply { putParcelable("player", it) }
            findNavController().navigate(R.id.action_homeFragment_to_playerDetailFragment, args)
        }
        binding.recyclerView3.adapter = playersAdapter
    }


//    private fun readData() {
//
//
//        binding.ler.setOnClickListener {
//
//            dados = binding.nome.text.toString()
//            viewModel.read(dados)
//
//        }
//    }


}
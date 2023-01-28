package com.dionis.escolinhajdb.presentation.home

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.databinding.FragmentHomeBinding
import com.dionis.escolinhajdb.presentation.auth.CoachAdapter
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.presentation.player.PlayerDetailFragment.Companion.PLAYER
import com.dionis.escolinhajdb.presentation.player.PlayerViewModel
import com.dionis.escolinhajdb.presentation.player.PlayersAdapter
import com.dionis.escolinhajdb.util.Extensions.navTo
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.UserManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()
    private val coachViewModel: ViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var playersAdapter: PlayersAdapter
    private lateinit var coachAdapter: CoachAdapter


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
        coachViewModel.getCoach()
        setUp()

    }

    private fun setUp() {

        binding.floatButton.setOnClickListener {
            showAlertDialog()
//            navTo(R.id.action_homeFragment_to_registerPlayerFragment)
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
                    binding.progressPlayer.visibility = View.VISIBLE
                    binding.recyclerView3.visibility = View.INVISIBLE
                }
                is UiState.Success -> {
                    binding.progressPlayer.visibility = View.GONE
                    binding.recyclerView3.visibility = View.VISIBLE
                    playersAdapter.updateList(it.data)
                }
            }
        }

        coachViewModel.coach.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    coachAdapter.updateList(it.data)
                }
            }
        }
    }


    private fun setUpAdapter() {

        playersAdapter = PlayersAdapter()

        playersAdapter.onItemClicked = {
            val args = Bundle().apply { putParcelable(PLAYER, it) }
            findNavController().navigate(R.id.action_homeFragment_to_playerDetailFragment, args)
        }
        binding.recyclerView3.adapter = playersAdapter


        coachAdapter = CoachAdapter()

        coachAdapter.onItemClicked = {
            val args = Bundle().apply { putParcelable(PLAYER, it) }
            findNavController().navigate(R.id.action_homeFragment_to_playerDetailFragment, args)
        }
        binding.recyclerViewCoach.adapter = coachAdapter


    }

    fun showAlertDialog() {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Adicionar novo")
            .setIcon(R.drawable.person_)
            .setNeutralButton("cancelar") { dialog, which -> toast("oi") }
            .setNegativeButton("novo aluno") {
                    dialog, which -> findNavController().navigate(R.id.action_homeFragment_to_registerPlayerFragment)
            }
            .setPositiveButton("novo treinador") {dialog, wich -> toast("novo treinador")}
            .show()

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
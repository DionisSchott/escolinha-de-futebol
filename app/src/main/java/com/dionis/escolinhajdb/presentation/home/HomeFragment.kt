package com.dionis.escolinhajdb.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentHomeBinding
import com.dionis.escolinhajdb.presentation.auth.LoginActivity
import com.dionis.escolinhajdb.presentation.coach.CoachAdapter
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.presentation.coach.DialogCoachDetailFragment
import com.dionis.escolinhajdb.presentation.coach.DialogCoachDetailFragment.Companion.COACH
import com.dionis.escolinhajdb.presentation.player.PlayerDetailFragment.Companion.PLAYER
import com.dionis.escolinhajdb.presentation.player.PlayerViewModel
import com.dionis.escolinhajdb.presentation.player.PlayersAdapter
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.UserManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()
    private val coachViewModel: ViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var playersAdapter: PlayersAdapter
    private lateinit var coachAdapter: CoachAdapter
    private lateinit var userManager: UserManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userManager = UserManager(requireContext())
        viewModel.getPlayers()
        coachViewModel.getCoach()
        setUp()
    }

    private fun setUp() {
        recovery()
        logout()
        getData()
        setUpClicks()
        setUpAdapter()
        setObservers()
    }

    private fun getData() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        val id = intent.extras?.getString("coachId")

    }

    private fun setUpClicks() {
        binding.floatButton.setOnClickListener {
            showAlertDialog()
        }
        binding.btnSeeAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_playerListFragment)
        }
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

    private fun logout() {
        binding.btLogout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }


    private fun setUpAdapter() {

        playersAdapter = PlayersAdapter()

        playersAdapter.onItemClicked = {
            navigateFromPlayerDetail(it)
        }
        playersAdapter.onLongItemClicked = {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("apagar cadastro?")
                .setNegativeButton("Sim")
                { dialog, which ->
                    deletePlayer(it)
                }
                .setNeutralButton("cancelar") { dialog, wich ->
                }
                .show()
        }
        binding.recyclerView3.adapter = playersAdapter




        coachAdapter = CoachAdapter()
        coachAdapter.onItemClicked = {

            val args = Bundle().apply { putParcelable(COACH, it) }

            val dialog = DialogCoachDetailFragment()
            dialog.show(childFragmentManager, dialog.tag)

            dialog.arguments = args
        }

        binding.recyclerViewCoach.adapter = coachAdapter
    }



    private fun navigateFromPlayerDetail(player: Player) {

        val args = Bundle().apply { putParcelable(PLAYER, player) }
        findNavController().navigate(R.id.action_homeFragment_to_playerDetailFragment, args)
    }

    private fun deletePlayer(player: Player) {
        viewModel.deletePlayer(player)
    }

    private fun recovery() {
        val uid = FirebaseAuth.getInstance().uid!!
        coachViewModel.recovery(uid)


    }

    private fun showAlertDialog() {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("adicionar")
            .setIcon(R.drawable.person_)
            .setNeutralButton("cancelar") { dialog, which -> toast("oi") }
            .setNegativeButton("novo aluno")
            { dialog, which ->
                findNavController().navigate(R.id.action_homeFragment_to_registerPlayerFragment)
            }
            .setPositiveButton("novo treinador") { dialog, wich ->
                findNavController().navigate(R.id.action_homeFragment_to_hilt_RegisterFragment)
            }

            .show()

    }
}
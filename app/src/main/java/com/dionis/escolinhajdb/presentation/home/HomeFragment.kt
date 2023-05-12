package com.dionis.escolinhajdb.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentHomeBinding
import com.dionis.escolinhajdb.presentation.auth.LoginActivity
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.presentation.coach.CoachAdapter
import com.dionis.escolinhajdb.presentation.coach.DialogCoachDetailFragment.Companion.COACH
import com.dionis.escolinhajdb.presentation.player.PlayerDetailFragment.Companion.PLAYER
import com.dionis.escolinhajdb.presentation.player.PlayerViewModel
import com.dionis.escolinhajdb.presentation.player.PlayersAdapter
import com.dionis.escolinhajdb.util.Extensions.firstName
import com.dionis.escolinhajdb.util.UserManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()
    private val coachViewModel: ViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var playersAdapter: PlayersAdapter
    private lateinit var coachAdapter: CoachAdapter
    private lateinit var userManager: UserManager
    private lateinit var coach: Coach
    private var userUid: String = ""


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
        viewModel.getPlayers("")
        coachViewModel.getCoach()

        setUp()
    }


    private fun setUp() {
        setUpClicks()
        setUpAdapter()
        setObservers()
        setOnRefreshListener()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(50)
            recoveryCoach()
        }

    }


    private fun setUpClicks() {
        binding.userEdit.setOnClickListener {
            val args = Bundle().apply { putParcelable(COACH, coach) }
            findNavController().navigate(R.id.action_homeFragment_to_updateUserInfoFragment, args)
        }
        binding.btLogout.setOnClickListener { logout() }
    }

    private fun setObservers() {
        viewModel.player.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {
                    binding.progressBarPlayer.visibility = View.VISIBLE
//                    binding.recyclerView3.visibility = View.INVISIBLE
                }
                is UiState.Success -> {
                    binding.progressBarPlayer.visibility = View.GONE
//                    binding.recyclerView3.visibility = View.VISIBLE
                    playersAdapter.updateList(it.data)

                }
            }
        }

//        coachViewModel.coach.observe(viewLifecycleOwner) {
//            when (it) {
//                is UiState.Failure -> {
//                }
//                is UiState.Loading -> {
//                }
//                is UiState.Success -> {
//                    coachAdapter.updateList(it.data)
//                }
//            }
//        }

        coachViewModel.recoveryCoach.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {
                    binding.progressBarCoach.visibility = View.VISIBLE
                    binding.coachName.visibility = View.INVISIBLE
                }
                is UiState.Success -> {
                    binding.progressBarCoach.visibility = View.GONE
                    binding.coachName.visibility = View.VISIBLE

                    coach = it.data
                    binding.tvWelcomeName.text = firstName(coach.name)
                    binding.coachName.text = firstName(coach.name)
                    if (it.data.photo.isNotEmpty()) {
                        Picasso.get().load(it.data.photo).into(binding.coachImg)
//            }
                    }
                }
            }
//        coachViewModel.recoveryCoach.observe(viewLifecycleOwner) {
//            coach = it
//            val fullName = coach.name
//            val firstName = fullName.split(" ")[0]
//            binding.tvWelcomeName.text = firstName
//            binding.coachName.text = firstName
//            if (it.photo.isNotEmpty()) {
//                Picasso.get().load(it.photo).into(binding.coachImg)
//            }

        }
    }

    private fun setUpAdapter() {

        playersAdapter = PlayersAdapter()

        val layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.HORIZONTAL, false)

        val recyclerView = binding.recyclerViewPlayer
        recyclerView.adapter = playersAdapter
        recyclerView.layoutManager = layoutManager

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


//        val snapHelper = LinearSnapHelper()
//        snapHelper.attachToRecyclerView(recyclerView)
//
//        val timer = Timer()
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                if (layoutManager.findLastCompletelyVisibleItemPosition() < (playersAdapter.itemCount - 1)) {
//                    layoutManager.smoothScrollToPosition(recyclerView, RecyclerView.State(), layoutManager.findLastVisibleItemPosition())
//                } else {
//                    layoutManager.smoothScrollToPosition(recyclerView, RecyclerView.State(), 0)
//                }
//            }
//        }, 0, 4500)


//        coachAdapter = CoachAdapter()
//        coachAdapter.onItemClicked = {
//
//            val args = Bundle().apply { putParcelable(COACH, it) }
//
//            val dialog = DialogCoachDetailFragment()
//            dialog.show(childFragmentManager, dialog.tag)
//
//            dialog.arguments = args
//        }
//
//        binding.recyclerViewCoach.adapter = coachAdapter
    }

    private fun setOnRefreshListener() {
        val refresh = binding.refreshListener

        refresh.setOnRefreshListener {
            viewModel.getPlayers("")
            coachViewModel.getCoach()
            setUpAdapter()
            refresh.isRefreshing = false
        }

    }

    private fun recoveryCoach() {
        lifecycleScope.launch {
            userUid = userManager.readUserUid()
        }
        if (userUid.isNotEmpty()) {
            coachViewModel.recoveryCoach(userUid)
        } else {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                coachViewModel.recoveryCoach(currentUser.uid)
            }
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
        lifecycleScope.launch {
            userManager.clearDataUser()
        }
    }

    private fun navigateFromPlayerDetail(player: Player) {

        val args = Bundle().apply { putParcelable(PLAYER, player) }
        findNavController().navigate(R.id.action_homeFragment_to_playerDetailFragment, args)
    }

    private fun deletePlayer(player: Player) {
        viewModel.deletePlayer(player)
        viewModel.getPlayers("")
        coachViewModel.getCoach()
        setUpAdapter()
    }


}
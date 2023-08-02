package com.dionis.escolinhajdb.presentation.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.auth.startCredentialAuthentication
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.dionis.escolinhajdb.util.Extensions.dialogConfirm
import com.dionis.escolinhajdb.util.Extensions.firstName
import com.dionis.escolinhajdb.util.Extensions.promptBiometricChecker
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: PlayerViewModel by activityViewModels()
    private val coachViewModel: ViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var playersAdapter: PlayersAdapter
    private lateinit var coachAdapter: CoachAdapter
    private lateinit var userManager: UserManager
    private lateinit var coach: Coach
    private var userUid: String = ""
    private var playerList: List<Player> = mutableListOf()
    private var category: String = ""


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
        setUpClicks()
        setObservers()
        recoveryCoach()
        setOnRefreshListener()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(50)
            setUpAdapter()
        }

    }


    private fun setUpClicks() {
        binding.userEdit.setOnClickListener {
            val args = Bundle().apply { putParcelable(COACH, coach) }
            findNavController().navigate(R.id.action_homeFragment_to_updateUserInfoFragment, args)
        }

        binding.btLogout.setOnClickListener { logout() }


    }

    private fun filterListByCategory(category: String) {
        val filteredList = playerList.filter {
            it.category.contains(category, ignoreCase = true)
        }
        playersAdapter.updateList(filteredList)
    }

    private fun setObservers() {
        coachViewModel.recoveryCoach.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {
                    binding.progressBarCoach.visibility = View.VISIBLE
                    binding.progressBarPlayer.visibility = View.VISIBLE
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
                    }
                    category = it.data.subFunction
                    lifecycleScope.launch {
                        delay(500)
                        viewModel.player.observe(viewLifecycleOwner) {
                            when (it) {
                                is UiState.Failure -> {
                                    toast("não foi possivel carregar a lista de jogadores")
                                }
                                is UiState.Loading -> {
                                }
                                is UiState.Success -> {
                                    binding.progressBarPlayer.visibility = View.GONE
                                    playerList = it.data
                                    filterListByCategory(category)
                                }
                            }
                        }
                    }

                }
            }
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
            dialogConfirm(
                "Desativar aluno"
            ) { biometricChecker(it) }

        }

    }


    private fun isBiometricSupported(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    private fun biometricChecker(player: Player) {

        promptBiometricChecker(
            title = "Por favor, confirme!",
            null,
            "cancelar",
            confirmationRequired = true,
            null,
            player,
            requireContext(),

            { error, errorMsg ->
                toast("$error: $errorMsg")
            }) {
            makePlayerInactive(player.copy(departureDate = Date()))
        }
    }

    private fun setOnRefreshListener() {
        val refresh = binding.refreshListener

        refresh.setOnRefreshListener {
            viewModel.getPlayers()
            coachViewModel.getCoach()
            setUp()
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
//        lifecycleScope.launch {
//            userManager.clearDataUser()
//        }
    }

    private fun navigateFromPlayerDetail(player: Player) {

        val args = Bundle().apply { putParcelable(PLAYER, player) }
        findNavController().navigate(R.id.action_homeFragment_to_playerDetailFragment, args)
    }

    private fun makePlayerInactive(player: Player) {

        viewModel.addFormerPlayer(player) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    toast("Enviado para lista de ex jogadores.")
                    viewModel.getPlayers()
                    coachViewModel.getCoach()
                    setUp()
                }
            }
        }
    }


}
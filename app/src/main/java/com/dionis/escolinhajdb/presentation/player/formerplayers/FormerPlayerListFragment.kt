package com.dionis.escolinhajdb.presentation.player.formerplayers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentFormerPlayerListBinding
import com.dionis.escolinhajdb.presentation.player.PlayerDetailFragment
import com.dionis.escolinhajdb.util.Extensions.dialogConfirm
import com.dionis.escolinhajdb.util.Extensions.promptBiometricChecker
import com.dionis.escolinhajdb.util.Extensions.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FormerPlayerListFragment : Fragment() {

    private lateinit var binding: FragmentFormerPlayerListBinding
    private lateinit var formerPlayersListAdapter: FormerPlayerListAdapter
    private val formerPlayerViewModel: FormerPlayerViewModel by viewModels()
    private var playerList: List<Player> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFormerPlayerListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formerPlayerViewModel.getFormerPlayers()

        setUp()
    }


    private fun setUp() {
        setFormerPlayerAdapter()
        setPlayerObservers()
        searchByName()
    }


    private fun setPlayerObservers() {
        formerPlayerViewModel.formerPlayers.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {

                }
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    formerPlayersListAdapter.updateList(it.data)
                    playerList = it.data
                }
            }
        }
    }


    private fun setFormerPlayerAdapter() {
        formerPlayersListAdapter = FormerPlayerListAdapter()

        val layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        val recyclerView = binding.recyclerViewPlayerList
        recyclerView.adapter = formerPlayersListAdapter
        recyclerView.layoutManager = layoutManager

        formerPlayersListAdapter.onItemClicked = {
            navigateFromPlayerDetail(it)
        }
        formerPlayersListAdapter.onLongItemClicked = {

            dialogConfirm(
                "Reativar jogador?"
            ) { biometricChecker(it) }

        }
        binding.recyclerViewPlayerList.adapter = formerPlayersListAdapter

    }

    private fun biometricChecker(player: Player) {

        promptBiometricChecker(
            "Por favor, confirme!",
            null,
            "cancelar",
            confirmationRequired = true,
            null,
            player,
            requireContext(),

            { error, errorMsg ->
                toast("$error: $errorMsg")
            }) {
            reactivatePlayer(
                player.copy(
                    departureDate = null,
                    isActive = true
                )
            )
        }
    }

    private fun reactivatePlayer(player: Player) {

        formerPlayerViewModel.reactivatePlayer(player) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    toast("Jogador reativado!")
                    formerPlayerViewModel.getFormerPlayers()
                    setUp()
                }
            }
        }
    }

    private fun navigateFromPlayerDetail(player: Player) {
        val args = Bundle().apply { putParcelable(PlayerDetailFragment.PLAYER, player) }
        findNavController().navigate(R.id.action_formerPlayerListFragment_to_playerDetailFragment, args)
    }

    private fun searchByName() {

        binding.playerFindEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                val query = binding.playerFindEdt.text.toString()
                search(query)
            }
        })
        binding.playerFindLayout.setEndIconOnClickListener {
            val query = binding.playerFindEdt.text.toString()
            search(query)
        }
    }


    fun search(query: String) {
        val filteredList = playerList.filter { document ->
            document.playerName.contains(query, ignoreCase = true) || document.responsibleName.contains(query, ignoreCase = true)
        }
        formerPlayersListAdapter.updateList(filteredList)
    }

}
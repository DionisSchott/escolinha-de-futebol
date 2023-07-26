package com.dionis.escolinhajdb.presentation.player

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentPlayerListBinding
import com.dionis.escolinhajdb.util.Extensions.dialogConfirm
import com.dionis.escolinhajdb.util.Extensions.promptBiometricChecker
import com.dionis.escolinhajdb.util.Extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PlayerListFragment : Fragment() {

    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var playersListAdapter: PlayersListAdapter
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private var playerList: List<Player> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerViewModel.getPlayers()

        setUp()
    }

    private fun setUp() {
        setPlayerAdapter()
        setPlayerObservers()
        searchByName()
        setupClicks()
    }

    private fun setupClicks() {
        binding.tvFormerPlayers.setOnClickListener {
            findNavController().navigate(R.id.action_playerListFragment_to_formerPlayerListFragment)
        }
    }


    private fun setPlayerObservers() {
        playerViewModel.player.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {

                }
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    playersListAdapter.updateList(it.data)
                    playerList = it.data
                }
            }
        }
    }


    private fun setPlayerAdapter() {
        playersListAdapter = PlayersListAdapter()

        val layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        val recyclerView = binding.recyclerViewPlayerList
        recyclerView.adapter = playersListAdapter
        recyclerView.layoutManager = layoutManager


        playersListAdapter.onItemClicked = {
            navigateFromPlayerDetail(it)
        }


        playersListAdapter.onLongItemClicked = {

            dialogConfirm(
                "Desativar aluno?"
            ) { biometricChecker(it) }
        }

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
            makePlayerInactive(player.copy(departureDate = Calendar.getInstance().time))
        }
    }


    private fun makePlayerInactive(player: Player) {

        playerViewModel.addFormerPlayer(player) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    toast("Enviado para lista de ex jogadores.")
                    playerViewModel.getPlayers()
                    setUp()
                }
            }
        }
    }

    private fun navigateFromPlayerDetail(player: Player) {

        val args = Bundle().apply { putParcelable(PlayerDetailFragment.PLAYER, player) }
        findNavController().navigate(R.id.action_playerListFragment_to_playerDetailFragment, args)
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
        playersListAdapter.updateList(filteredList)
    }

}
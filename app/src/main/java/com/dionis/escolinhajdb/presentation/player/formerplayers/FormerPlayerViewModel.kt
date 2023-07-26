package com.dionis.escolinhajdb.presentation.player.formerplayers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.data.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormerPlayerViewModel @Inject constructor(
    val playerRepository: PlayerRepository,
) : ViewModel() {

    private val _formerPlayers = MutableLiveData<UiState<List<Player>>>()
    val formerPlayers: LiveData<UiState<List<Player>>> get() = _formerPlayers

    private var _reactivatePlayer = MutableLiveData<UiState<Pair<Player, String>>>()
    val reactivatePlayer: LiveData<UiState<Pair<Player, String>>> get() = _reactivatePlayer


    fun reactivatePlayer(player: Player, onResult: (UiState<Player>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            playerRepository.reactivatePlayer(player, onResult)
        }
    }


    fun getFormerPlayers() {
        _formerPlayers.value = UiState.Loading
        playerRepository.getFormerPlayer() { _formerPlayers.value = it }
    }


}
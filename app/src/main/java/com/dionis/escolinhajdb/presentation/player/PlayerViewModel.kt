package com.dionis.escolinhajdb.presentation.player

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dionis.escolinhajdb.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.data.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val playerRepository: PlayerRepository,
) : ViewModel() {

    private var _registerPlayer = MutableLiveData<UiState<Pair<Player, String>>>()
    val playerRegister: LiveData<UiState<Pair<Player, String>>> get() = _registerPlayer

    private val _validateFields: MutableLiveData<States.ValidateRegisterPlayer> = MutableLiveData()
    val validateFields: LiveData<States.ValidateRegisterPlayer> get() = _validateFields

    private val _player = MutableLiveData<UiState<List<Player>>>()
    val player: LiveData<UiState<List<Player>>> get() = _player


    fun registerPlayer(player: Player) {
        _registerPlayer.value = UiState.Loading

        playerRepository.addPlayer(player) { _registerPlayer.value = it }
    }

    fun getPlayers() {
        _player.value = UiState.Loading
        playerRepository.getPlayer() {_player.value = it}
    }


    fun uploadImage(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            playerRepository.uploadImage(fileUri, onResult)
        }

    }

    fun validateFields(
        playerName: String,
        responsibleName: String,
        playersBirth: String,
    ) {
        if (validateAllFields(
                playerName,
                responsibleName,
                playersBirth
            )
        )
            _validateFields.value = States.ValidateRegisterPlayer.FieldsDone
    }

    private fun validateAllFields(
        playerName: String,
        responsibleName: String,
        playersBirth: String,
    ): Boolean {
        if (playerName.isEmpty()) {
            _validateFields.value = States.ValidateRegisterPlayer.PlayerNameEmpty
            return false
        }
        if (responsibleName.isEmpty()) {
            _validateFields.value = States.ValidateRegisterPlayer.ResponsibleNameEmpty
            return false
        }
        if (playersBirth.isEmpty()) {
            _validateFields.value = States.ValidateRegisterPlayer.PlayersBirthEmpty
            return false
        }
        return true
    }


}
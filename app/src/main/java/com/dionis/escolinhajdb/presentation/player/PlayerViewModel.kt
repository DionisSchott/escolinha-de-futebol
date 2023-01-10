package com.dionis.escolinhajdb.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dionis.escolinhajdb.UiState
import androidx.lifecycle.ViewModel
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.data.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val playerRepository: PlayerRepository,
) : ViewModel() {

    private var _registerPlayer = MutableLiveData<UiState<Pair<Player, String>>>()
    val playerRegister: LiveData<UiState<Pair<Player, String>>> get() = _registerPlayer

    private val _validateFields: MutableLiveData<States.ValidateRegisterPlayer> = MutableLiveData()
    val validateFields: LiveData<States.ValidateRegisterPlayer> get() = _validateFields


    fun registerPlayer(player: Player) {
        _registerPlayer.value = UiState.Loading

        playerRepository.addPlayer(player) { _registerPlayer.value = it }
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
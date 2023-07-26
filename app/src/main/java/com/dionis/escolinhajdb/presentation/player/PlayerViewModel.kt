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

    private val _deletePlayer = MutableLiveData<UiState<String>>()
    val deletePlayer: LiveData<UiState<String>> = _deletePlayer

    private var _updatePlayer = MutableLiveData<UiState<String>>()
    val updatePlayer: LiveData<UiState<String>> get() = _updatePlayer

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
        playerRepository.getPlayer() { _player.value = it }
    }

    fun updatePlayer(player: Player) {
        _updatePlayer.value = UiState.Loading
        playerRepository.updatePlayer(player) { _updatePlayer.value = it }
    }

    fun deletePlayer(player: Player) {
        _deletePlayer.value = UiState.Loading
        playerRepository.deletePlayer(player) { _deletePlayer.value = it }
    }

    fun addFormerPlayer(player: Player, onResult: (UiState<Player>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            playerRepository.addFormerPlayer(player, onResult)
        }
    }


    fun updateImage(imageUrl: String, fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            playerRepository.updateImage(imageUrl, fileUri, onResult)
        }
    }

    fun uploadSingleImage(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            playerRepository.uploadSingleFile(fileUri, onResult)
        }

    }


    fun validateFields(
        playerName: String,
        responsibleName: String,
        playersBirth: String,
        playerGenre: String,
        playerCategory: String,
    ) {
        if (validateAllFields(
                playerName,
                responsibleName,
                playersBirth,
                playerGenre,
                playerCategory
            ))
            _validateFields.value = States.ValidateRegisterPlayer.FieldsDone
    }

    private fun validateAllFields(
        playerName: String,
        responsibleName: String,
        playersBirth: String,
        playerGenre: String,
        playerCategory: String,
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
        if (playerGenre.isEmpty()) {
            _validateFields.value = States.ValidateRegisterPlayer.PlayerGenreEmpty
            return false
        }
        if (playerCategory.isEmpty()) {
            _validateFields.value = States.ValidateRegisterPlayer.PlayerCategoryEmpty
            return false
        }


        return true
    }


}
package com.dionis.escolinhajdb.data.repository

import android.net.Uri
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import javax.inject.Inject

interface PlayerRepository {

    fun addPlayer(player: Player, result: (UiState<Pair<Player, String>>) -> Unit)
    fun getPlayer(result: (UiState<List<Player>>) -> Unit)

    suspend fun uploadImage(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)


}
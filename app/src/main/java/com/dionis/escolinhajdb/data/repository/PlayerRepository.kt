package com.dionis.escolinhajdb.data.repository

import android.net.Uri
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import javax.inject.Inject

interface PlayerRepository {

    fun addPlayer(player: Player, result: (UiState<Pair<Player, String>>) -> Unit)
    fun deletePlayer(player: Player, result: (UiState<String>) -> Unit)
    fun updatePlayer(player: Player, result: (UiState<String>) -> Unit)
    fun getPlayer(result: (UiState<List<Player>>) -> Unit)

    suspend fun uploadImage(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
    suspend fun uploadSingleFile2(fileUri: Uri, onResult: (UiState<String>) -> Unit)


    suspend fun deleteImage(imageUrl: String)
    suspend fun updateImage(imageUrl: String, fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
}
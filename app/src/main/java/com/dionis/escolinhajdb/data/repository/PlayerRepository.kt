package com.dionis.escolinhajdb.data.repository

import android.net.Uri
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player

interface PlayerRepository {

    fun addPlayer(player: Player, result: (UiState<Pair<Player, String>>) -> Unit)
    fun deletePlayer(player: Player, result: (UiState<String>) -> Unit)
    fun updatePlayer(player: Player, result: (UiState<String>) -> Unit)
    fun getPlayer(result: (UiState<List<Player>>) -> Unit)

    suspend fun uploadImage(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
    suspend fun deleteImage(imageUrl: String)
    suspend fun updateImage(imageUrl: String, fileUri: Uri, onResult: (UiState<Uri>) -> Unit)

    fun addFormerPlayer(player: Player, result: (UiState<Player>) -> Unit)
    fun getFormerPlayer(result: (UiState<List<Player>>) -> Unit)
    fun reactivatePlayer(player: Player, result: (UiState<Player>) -> Unit)
    fun deleteFormerPlayer(player: Player, result: (UiState<String>) -> Unit)
}
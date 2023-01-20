package com.dionis.escolinhajdb.data.repository

import android.net.Uri
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.util.FireStoreCollection
import com.dionis.escolinhajdb.util.FireStoreCollection.PLAYER
import com.dionis.escolinhajdb.util.FirebaseStorageConstants.NOTE_IMAGES
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl(
    val dataBase: FirebaseFirestore,
    var storageReference: StorageReference,
) : PlayerRepository {

    override fun addPlayer(player: Player, result: (UiState<Pair<Player, String>>) -> Unit) {
        val document = dataBase.collection(FireStoreCollection.PLAYER).document()
        player.id = document.id
        document
            .set(player)
            .addOnCompleteListener {
                result.invoke(
                    UiState.Success(Pair(player, "jogador cadastrado com sucesso"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }


    }

    override fun getPlayer( result: (UiState<List<Player>>) -> Unit) {
        dataBase.collection(FireStoreCollection.PLAYER)
            .orderBy("playerName", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                val players = arrayListOf<Player>()
                for (document in it) {
                    val player = document.toObject(Player::class.java)
                    players.add(player)
                }
                result.invoke(
                    UiState.Success(players)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override suspend fun uploadImage(
        fileUri: List<Uri>,
        onResult: (UiState<List<Uri>>) -> Unit,
    ) {



        try {
             val uri: List<Uri> = withContext(Dispatchers.IO) {
                fileUri.map {
                    async {
                        storageReference.child(it.lastPathSegment ?: "${System.currentTimeMillis()}")
                            .putFile(it)
                            .await()
                            .storage
                            .downloadUrl
                            .await()
                    }
                }.awaitAll()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke((UiState.Failure(e.message)))
        }
    }





}
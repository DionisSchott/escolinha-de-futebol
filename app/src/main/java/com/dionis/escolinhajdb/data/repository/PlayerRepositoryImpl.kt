package com.dionis.escolinhajdb.data.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.util.FireStoreCollection
import com.dionis.escolinhajdb.util.FireStoreCollection.PLAYER
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class PlayerRepositoryImpl(
    val dataBase: FirebaseFirestore,
    var storageReference: StorageReference,
) : PlayerRepository {

    override fun addPlayer(player: Player, result: (UiState<Pair<Player, String>>) -> Unit) {
        val document = dataBase.collection(PLAYER).document()
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

    override fun deletePlayer(player: Player, result: (UiState<String>) -> Unit) {
        dataBase.collection(PLAYER).document(player.id)
            .delete()
            .addOnSuccessListener {
                result.invoke(UiState.Success("Cadastro deletado"))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
    }


    override fun updatePlayer(player: Player, result: (UiState<String>) -> Unit) {
        val document = dataBase.collection(PLAYER).document(player.id)
        document
            .set(player)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been update successfully")
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


    override fun getPlayer(filter: String, result: (UiState<List<Player>>) -> Unit) {
        val query = dataBase.collection(PLAYER)

//            .whereEqualTo("category", "sub10")
            .orderBy("playerName", Query.Direction.DESCENDING)
        //.startAt(filter)
        // .endAt(filter)

        query.get()
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

    override suspend fun uploadImage(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit) {
        try {
            val uri: List<Uri> = withContext(Dispatchers.IO) {
                fileUri.map {
                    async {
                        storageReference.child(PLAYER).child(it.lastPathSegment ?: "${System.currentTimeMillis()}")
                            .putFile(it)
                            .continueWithTask { storageReference.downloadUrl }
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


    override suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference.child(PLAYER).child(fileUri.lastPathSegment ?: "${System.currentTimeMillis()}")
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }

    override suspend fun updateImage(imageUrl: String, fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference.child(PLAYER).child(fileUri.lastPathSegment ?: "${System.currentTimeMillis()}")
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }

        deleteImage(imageUrl)

    }


    override suspend fun deleteImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            storageRef.delete()
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    Log.e(TAG, "Erro")
                }
        }
    }

    //testando
    override suspend fun uploadSingleFile2(fileUri: Uri, onResult: (UiState<String>) -> Unit) {
        try {
            storageReference.child(PLAYER).child(fileUri.lastPathSegment ?: "${System.currentTimeMillis()}")
                .putFile(fileUri)
                .await()
                .storage
                .downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    onResult.invoke(UiState.Success(url))
                }
                .await()

        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }


}
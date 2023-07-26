package com.dionis.escolinhajdb.data.repository

import android.net.Uri
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Events
import com.dionis.escolinhajdb.util.FireStoreCollection
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EventRepositoryImpl(
    val database: FirebaseFirestore,
    var storageReference: StorageReference,
) : EventRepository {


    override fun createEvent(event: Events, result: (UiState<Pair<Events, String>>) -> Unit) {
        val document = database.collection(FireStoreCollection.EVENTS).document()
        event.id = document.id
        document
            .set(event)
            .addOnCompleteListener {
                result.invoke(
                    UiState.Success(Pair(event, "jogador cadastrado com sucesso"))
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

    override fun updateEvent(event: Events, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.EVENTS).document(event.id)
        document
            .set(event)
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

    override fun getEvents(result: (UiState<List<Events>>) -> Unit) {
        database.collection(FireStoreCollection.EVENTS)
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                val events = arrayListOf<Events>()
                for (document in it) {
                    val event = document.toObject(Events::class.java)
                    events.add(event)
                }
                result.invoke(
                    UiState.Success(events)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference.child(FireStoreCollection.EVENTS).child(fileUri.lastPathSegment ?: "${System.currentTimeMillis()}")
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


}
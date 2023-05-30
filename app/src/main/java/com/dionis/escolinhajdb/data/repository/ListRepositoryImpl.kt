package com.dionis.escolinhajdb.data.repository

import android.content.ContentValues
import android.util.Log
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Lists
import com.dionis.escolinhajdb.util.FireStoreCollection
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class ListRepositoryImpl(
    val database: FirebaseFirestore,

    ) : ListRepository {

    override fun getlists(result: (UiState<Lists>) -> Unit) {

        database.collection(FireStoreCollection.LISTS).document("1UT4NrbDvK1cZkwAb3Ex")
            .get()
            .addOnCompleteListener {
                val lists = it.result.toObject(Lists::class.java)

                result.invoke(
                    UiState.Success(lists!!)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    //função para adicionar item à lista, como por exemplo lsita de posição de jogador. falta terminar.
    override fun updateLists(newItem: String, result: (UiState<String>) -> Unit) {
        database.collection(FireStoreCollection.LISTS).document("1UT4NrbDvK1cZkwAb3Ex")
            .update("function", FieldValue.arrayUnion(newItem))
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("feito")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    //função para remover item da lista, como por exemplo lsita de posição de jogador. falta terminar.
    override fun removeListItem(itemId: String) {
        database.collection("lists").document("1UT4NrbDvK1cZkwAb3Ex")

            .update("position", FieldValue.arrayRemove(itemId))
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Item removed from list")
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error removing item from list", e)
            }
    }


}
package com.dionis.escolinhajdb.data.repository

import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.util.FireStoreCollection
import com.google.firebase.firestore.FirebaseFirestore

class PlayerRepositoryImpl(

    val dataBase: FirebaseFirestore,

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
}
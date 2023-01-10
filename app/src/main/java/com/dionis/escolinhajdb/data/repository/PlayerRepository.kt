package com.dionis.escolinhajdb.data.repository

import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player

interface PlayerRepository {

    fun addPlayer(player: Player, result: (UiState<Pair<Player, String>>) -> Unit)


}
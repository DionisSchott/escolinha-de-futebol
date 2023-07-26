package com.dionis.escolinhajdb.data.repository

import android.net.Uri
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Events

interface EventRepository {

    fun createEvent(event: Events, result: (UiState<Pair<Events, String>>) -> Unit)

    fun updateEvent(event: Events, result: (UiState<String>) -> Unit)

    fun getEvents(result: (UiState<List<Events>>) -> Unit)

    suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)

}
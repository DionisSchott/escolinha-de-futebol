package com.dionis.escolinhajdb.data.repository

import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Lists

interface ListRepository {


    fun getlists(result: (UiState<Lists>) -> Unit)
    fun updateLists(newItem: String, result: (UiState<String>) -> Unit)
    fun removeListItem(itemId: String)
}
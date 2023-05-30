package com.dionis.escolinhajdb.presentation.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Lists
import com.dionis.escolinhajdb.data.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    val listRepository: ListRepository
) : ViewModel(){


    private val _lists = MutableLiveData<UiState<Lists>>()
    val lists: LiveData<UiState<Lists>> get() = _lists

    private val _updateLists = MutableLiveData<UiState<String>>()
    val updateLists: LiveData<UiState<String>> get() = _updateLists



    fun getLists() {
        _lists.value = UiState.Loading
        listRepository.getlists { _lists.value = it }
    }

        fun updateLists(newItem: String) {
        _updateLists.value = UiState.Loading
        listRepository.updateLists(newItem) {_updateLists.value = it}
    }
}
package com.dionis.escolinhajdb.presentation.auth

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.data.model.Lists
import com.dionis.escolinhajdb.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    val authRepository: AuthRepository,
) : ViewModel() {

    private val _coach = MutableLiveData<UiState<List<Coach>>>()
    val coach: LiveData<UiState<List<Coach>>> get() = _coach

    private val _lists = MutableLiveData<UiState<Lists>>()
    val lists: LiveData<UiState<Lists>> get() = _lists

    private val _updateLists = MutableLiveData<UiState<String>>()
    val updateLists: LiveData<UiState<String>> get() = _updateLists

    private val _updateInfo = MutableLiveData<UiState<String>>()
    val updateInfo: LiveData<UiState<String>> get() = _updateInfo

    private var _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>> get() = _register

    private var _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>> get() = _login

    private val _recoveryCoach = MutableLiveData<UiState<Coach>>()
    val recoveryCoach: LiveData<UiState<Coach>> = _recoveryCoach

//    private val _recoveryCoach = MutableLiveData<Coach>()
//    val recoveryCoach: LiveData<Coach> = _recoveryCoach


    fun getCoach() {
        _coach.value = UiState.Loading
        authRepository.getCoach { _coach.value = it }
    }

    fun recoveryCoach(id: String) {
        _recoveryCoach.value = UiState.Loading
        authRepository.recoveryCoach(id) { _recoveryCoach.value = it }
    }

//    fun recoveryCoach(id: String) {
//        authRepository.storeSession(id) { _recoveryCoach.value = it  }
//    }

    fun updateUserInfo(coach: Coach) {
        _updateInfo.value = UiState.Loading
        authRepository.updateUserInfo(coach) { _updateInfo.value = it }
    }

    fun getLists() {
        _lists.value = UiState.Loading
        authRepository.getlists { _lists.value = it }
    }

    fun uploadImage(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            authRepository.uploadImage(fileUri, onResult)
        }
    }

    fun changePassword(user: FirebaseUser?, newPassword: String, onResult: (UiState<String>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            authRepository.changePassword(user, newPassword, onResult)
        }
    }


//    fun updateLists(newItem: String) {
//        _updateLists.value = UiState.Loading
//        authRepository.updateLists(newFunction) {_updateLists.value = it}
//    }

    fun register(
        email: String,
        password: String,
        coach: Coach,
    ) {
        _register.value = UiState.Loading
        authRepository.registerUser(
            email = email,
            password = password,
            coach = coach
        ) { _register.value = it }
    }


    fun login(
        email: String,
        password: String,
    ) {
        _login.value = UiState.Loading
        authRepository.login(
            email,
            password
        ) { _login.value = it }
    }


}
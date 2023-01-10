package com.dionis.escolinhajdb.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.data.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    val authRepository: AuthRepository,
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private var _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>> get() = _register

    private var _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>> get() = _login


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
package com.dionis.escolinhajdb.data.repository

import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach

interface AuthRepository {

    fun registerUser(email: String, password: String, coach: Coach, result: (UiState<String>) -> Unit)
    fun updateUserInfo(coach: Coach, result: (UiState<String>) -> Unit)
    fun login(email: String, password: String, result: (UiState<String>) -> Unit)
    fun storeSession(id: String, result: (Coach?) -> Unit)
}
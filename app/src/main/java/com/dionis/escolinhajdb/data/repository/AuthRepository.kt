package com.dionis.escolinhajdb.data.repository

import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.data.model.Player

interface AuthRepository {

    fun getCoach(result: (UiState<List<Coach>>) -> Unit)
    fun registerUser(email: String, password: String, coach: Coach, result: (UiState<String>) -> Unit)
    fun updateUserInfo(coach: Coach, result: (UiState<String>) -> Unit)
    fun login(email: String, password: String, result: (UiState<String>) -> Unit)
    fun storeSession(id: String, result: (Coach?) -> Unit)
    fun recovery (id: String, result: (UiState<Coach>) -> Unit)
}
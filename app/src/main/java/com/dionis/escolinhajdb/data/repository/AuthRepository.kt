package com.dionis.escolinhajdb.data.repository

import android.net.Uri
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.data.model.Lists
import com.dionis.escolinhajdb.data.model.Player
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    fun getCoach(result: (UiState<List<Coach>>) -> Unit)
    fun registerUser(email: String, password: String, coach: Coach, result: (UiState<String>) -> Unit)

    fun updateUserInfo(coach: Coach, result: (UiState<String>) -> Unit)
    fun login(email: String, password: String, result: (UiState<String>) -> Unit)
    fun storeSession(id: String, result: (Coach?) -> Unit)
    fun recoveryCoach (id: String, result: (UiState<Coach>) -> Unit)

    suspend fun uploadImage(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)

    suspend fun authenticateUser(user: FirebaseUser?, currentPassword: String, onResult: (UiState<String>) -> Unit)
    suspend fun changePassword(user: FirebaseUser?, newPassword: String, onResult: (UiState<String>) -> Unit)

}
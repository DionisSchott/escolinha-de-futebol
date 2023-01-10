package com.dionis.escolinhajdb.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dionis.escolinhajdb.data.model.User
import kotlinx.coroutines.flow.first

private val Context.dataUser: DataStore<Preferences> by preferencesDataStore(name = "user")

class UserManager(val context: Context) {

    companion object {
        private val USER_EMAIL_KEY = stringPreferencesKey("USER_EMAIL")
        private val USER_PASSWORD_KEY = stringPreferencesKey("USER_PASSWORD")
        private val USER_AUTHENTICATED_KEY = booleanPreferencesKey("USER_AUTHENTICATED")
    }

    suspend fun saveDataUser(email: String, password: String, authenticated: Boolean) {

        context.dataUser.edit {
            it[USER_EMAIL_KEY] = email
            it[USER_PASSWORD_KEY] = password
            it[USER_AUTHENTICATED_KEY] = authenticated
        }
    }

    suspend fun readDataUser(): User {

        val prefs = context.dataUser.data.first()

        return User(
            email = prefs[USER_EMAIL_KEY] ?: "",
            password = prefs[USER_PASSWORD_KEY] ?: "",
            authenticated = prefs[USER_AUTHENTICATED_KEY] ?: false
        )
    }

    suspend fun clearDataUser() {
        context.dataUser.edit { it.clear() }
    }

}
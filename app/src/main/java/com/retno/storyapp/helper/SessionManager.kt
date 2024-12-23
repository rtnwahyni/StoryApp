package com.retno.storyapp.helper

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object SessionManager {

    private const val DATASTORE_NAME = "user_session"
    private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)
    private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")

    suspend fun saveAuthToken(context: Context, token: String?, userId: String?) {
        if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
            Log.e("SessionManager", "Invalid token or userId: token=$token, userId=$userId")
            return
        }

        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
        }
        Log.d("SessionManager", "Token saved: $token")
    }

    fun getAuthToken(context: Context): Flow<String?> {
        return context.dataStore.data
            .map { preferences -> preferences[AUTH_TOKEN_KEY] }
    }

    suspend fun clearSession(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
        Log.d("SessionManager", "Session cleared.")
    }

    fun getAuthTokenSync(context: Context): String? {
        var token: String?
        runBlocking {
            token = getAuthToken(context).first()
        }
        return token
    }
}

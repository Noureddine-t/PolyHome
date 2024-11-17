package com.noureddinetaleb.polyhome.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

private val Context.tokenStore by preferencesDataStore(name = "token");
class TokenStorage(private var context: Context) {

    private val tokenKey = stringPreferencesKey("token")

    suspend fun write(token: String) {
        this.context.tokenStore.edit { preferences ->
            preferences[tokenKey] = token
        }

    }
    suspend fun read(): String {
        val data = this.context.tokenStore.data.firstOrNull()?.get(tokenKey)
        return data ?: ""
    }
}
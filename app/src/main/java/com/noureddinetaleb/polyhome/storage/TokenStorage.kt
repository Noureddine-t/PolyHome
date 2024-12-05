package com.noureddinetaleb.polyhome.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

private val Context.tokenStore by preferencesDataStore(name = "token");

/**
 * Token storage class
 * @property tokenKey the token key
 * @constructor Creates an instance of TokenStorage with the provided application context.
 */
class TokenStorage(private var context: Context) {

    private val tokenKey = stringPreferencesKey("token")

    /**
     * Write the token
     * @param token the token
     */
    suspend fun write(token: String) {
        this.context.tokenStore.edit { preferences ->
            preferences[tokenKey] = token
        }

    }

    /**
     * Read the token
     * @return the token
     */
    suspend fun read(): String {
        val data = this.context.tokenStore.data.firstOrNull()?.get(tokenKey)
        return data ?: ""
    }
}
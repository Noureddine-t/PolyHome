package com.noureddinetaleb.polyhome.storage;

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

private val Context.usernameStore by preferencesDataStore(name = "username");

/**
 * Username storage class.
 *
 * @property usernameKey the username key.
 * @constructor Creates an instance of UsernameStorage with the provided application context.
 */
class UsernameStorage(private var context: Context) {

    private val usernameKey = stringPreferencesKey("username")

    /**
     * Write the username.
     *
     * @param username The username.
     */
    suspend fun write(username: String) {
        this.context.usernameStore.edit { preferences ->
            preferences[usernameKey] = username
        }

    }

    /**
     * Read the username.
     *
     * @return the username.
     */
    suspend fun read(): String {
        val data = this.context.usernameStore.data.firstOrNull()?.get(usernameKey)
        return data ?: ""
    }

}
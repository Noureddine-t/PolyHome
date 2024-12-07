package com.noureddinetaleb.polyhome.storage;

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

private val Context.usernameStore by preferencesDataStore(name = "username");

class UsernameStorage(private var context: Context) {

    private val usernameKey = stringPreferencesKey("username")


    suspend fun write(username: String) {
        this.context.usernameStore.edit { preferences ->
            preferences[usernameKey] = username
        }

    }

    suspend fun read(): String {
        val data = this.context.usernameStore.data.firstOrNull()?.get(usernameKey)
        return data ?: ""
    }

}
package com.noureddinetaleb.polyhome.storage;

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private val Context.homeStore by preferencesDataStore(name = "homes");

class HomeStorage(private var context: Context) {

    private val homesKey = stringPreferencesKey("homes")
    suspend fun write(){

    }
    suspend fun read(){

    }

}
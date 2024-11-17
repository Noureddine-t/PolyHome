package com.noureddinetaleb.polyhome.storage

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


private val Context.deviceStore by preferencesDataStore(name = "devices");

class DeviceStorage(private var context: Context) {

    private val devicesKey = stringPreferencesKey("devices")

    suspend fun write(){

    }
    suspend fun read(){

    }

}
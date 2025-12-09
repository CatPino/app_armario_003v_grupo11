package com.example.app_armario

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Declaración ÚNICA y PÚBLICA del DataStore para toda la app.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_armario_local")

package com.example.app_armario

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// Esta es la extensión global para usar DataStore en toda la app
val Context.dataStore by preferencesDataStore(name = "app_armario_local")
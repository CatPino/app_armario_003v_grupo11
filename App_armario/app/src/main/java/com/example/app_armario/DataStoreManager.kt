package com.example.app_armario

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// Instancia única de DataStore para toda la app
val Context.dataStore by preferencesDataStore(name = "registro_usuario")

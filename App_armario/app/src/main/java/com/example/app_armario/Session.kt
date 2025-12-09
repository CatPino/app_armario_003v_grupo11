package com.example.app_armario

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.app_armario.KEY_USER
import kotlinx.coroutines.flow.first

private val KEY_USER = stringPreferencesKey("usuario_logueado")
private val KEY_ROLE = stringPreferencesKey("rol_logueado")

suspend fun guardarSesion(context: Context, email: String, rol: String) {
    context.dataStore.edit { prefs ->
        prefs[KEY_USER] = email
        prefs[KEY_ROLE] = rol
    }
}

suspend fun obtenerRolUsuario(context: Context): String? {
    return context.dataStore.data.first()[KEY_ROLE]
}

suspend fun cerrarSesion(context: Context) {
    context.dataStore.edit { prefs ->
        prefs.remove(KEY_USER)
        prefs.remove(KEY_ROLE)
    }
}

suspend fun obtenerEmailSesion(context: Context): String? {
    return context.dataStore.data.first()[KEY_USER]
}
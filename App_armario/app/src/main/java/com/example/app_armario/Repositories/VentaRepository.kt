package com.example.app_armario.Repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.app_armario.Models.Venta
import com.example.app_armario.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class VentaRepository(private val context: Context) {
    private val ventasKey = stringPreferencesKey("ventas_registradas")

    fun registrarVenta(id: String, fecha: String, total: Long, productos: List<com.example.app_armario.Models.CarritoItem>) {
        val lista = obtenerVentas().toMutableList()
        lista.add(Venta(id, fecha, total, productos))
        guardar(lista)
    }

    fun obtenerVentas(): List<Venta> = runBlocking {
        context.dataStore.data.map { prefs ->
            prefs[ventasKey]?.let { Json.decodeFromString<List<Venta>>(it) } ?: emptyList()
        }.first()
    }

    private fun guardar(lista: List<Venta>) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs[ventasKey] = Json.encodeToString(lista)
        }
    }
}

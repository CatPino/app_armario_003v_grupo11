package com.example.app_armario.Repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.app_armario.Models.CarritoItem
import com.example.app_armario.dataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CarritoRepository(private val context: Context) {

    private val carritoKey = stringPreferencesKey("carrito_items")
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val _contador = MutableStateFlow(0)
    val contador: StateFlow<Int> = _contador.asStateFlow()

    init {
        actualizarContador()
    }

    fun obtenerCarrito(): List<CarritoItem> = runBlocking {
        context.dataStore.data.map { prefs ->
            prefs[carritoKey]?.let { json.decodeFromString<List<CarritoItem>>(it) } ?: emptyList()
        }.first()
    }

    private fun guardar(lista: List<CarritoItem>) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs[carritoKey] = json.encodeToString(lista)
        }
        actualizarContador()
    }

    fun agregar(idProducto: String, nombre: String, precio: Long, imagenUrl: String?) {
        val actual = obtenerCarrito().toMutableList()
        val idx = actual.indexOfFirst { it.idProducto == idProducto }
        if (idx >= 0) {
            val it = actual[idx]
            actual[idx] = it.copy(cantidad = it.cantidad + 1)
        } else {
            actual.add(CarritoItem(idProducto, nombre, precio, imagenUrl, cantidad = 1))
        }
        guardar(actual)
    }

    fun incrementar(idProducto: String) {
        val actual = obtenerCarrito().toMutableList()
        val idx = actual.indexOfFirst { it.idProducto == idProducto }
        if (idx >= 0) {
            val it = actual[idx]
            actual[idx] = it.copy(cantidad = it.cantidad + 1)
            guardar(actual)
        }
    }

    fun decrementar(idProducto: String) {
        val actual = obtenerCarrito().toMutableList()
        val idx = actual.indexOfFirst { it.idProducto == idProducto }
        if (idx >= 0) {
            val it = actual[idx]
            val nueva = it.cantidad - 1
            if (nueva <= 0) actual.removeAt(idx) else actual[idx] = it.copy(cantidad = nueva)
            guardar(actual)
        }
    }

    fun eliminar(idProducto: String) {
        val nueva = obtenerCarrito().filter { it.idProducto != idProducto }
        guardar(nueva)
    }

    fun limpiar() {
        guardar(emptyList())
    }

    private fun actualizarContador() {
        val total = obtenerCarrito().sumOf { it.cantidad }
        _contador.value = total
    }
}

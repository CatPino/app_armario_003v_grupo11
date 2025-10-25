package com.example.app_armario.Repositories

import android.content.Context
import com.example.app_armario.Models.Producto
import com.example.app_armario.Models.Categoria
import com.example.app_armario.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

class ProductoRepository(private val context: Context) {

    private val productoKey = stringPreferencesKey("productos")

    // Obtener todos los productos
    fun getProductos(): List<Producto> = runBlocking {
        context.dataStore.data.map { prefs ->
            prefs[productoKey]?.let { Json.decodeFromString<List<Producto>>(it) } ?: emptyList()
        }.first()
    }

    // Agregar un producto nuevo
    fun agregarProducto(producto: Producto) = runBlocking {
        val lista = getProductos().toMutableList()
        val nuevoId = (lista.maxOfOrNull { it.id } ?: 0) + 1
        lista.add(producto.copy(id = nuevoId))

        context.dataStore.edit { prefs ->
            prefs[productoKey] = Json.encodeToString(lista)
        }
    }

    // Editar un producto existente
    fun editarProducto(actualizado: Producto) = runBlocking {
        val lista = getProductos().toMutableList()
        val index = lista.indexOfFirst { it.id == actualizado.id }
        if (index != -1) {
            lista[index] = actualizado
            context.dataStore.edit { prefs ->
                prefs[productoKey] = Json.encodeToString(lista)
            }
        }
    }

    // Eliminar un producto por id
    fun eliminarProducto(id: Long) = runBlocking {
        val nuevaLista = getProductos().filter { it.id != id }
        context.dataStore.edit { prefs ->
            prefs[productoKey] = Json.encodeToString(nuevaLista)
        }
    }
}
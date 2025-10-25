package com.example.app_armario.Repositories

import android.content.Context
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

class CategoriaRepository(private val context: Context) {

    private val categoriaKey = stringPreferencesKey("categorias")

    // Obtener todas las categorías
    fun getCategorias(): List<Categoria> = runBlocking {
        context.dataStore.data.map { prefs ->
            prefs[categoriaKey]?.let { Json.decodeFromString<List<Categoria>>(it) } ?: emptyList()
        }.first()
    }

    // Agregar una categoría nueva
    fun agregarCategoria(categoria: Categoria) = runBlocking {
        val lista = getCategorias().toMutableList()
        val nuevoId = (lista.maxOfOrNull { it.id } ?: 0) + 1
        lista.add(categoria.copy(id = nuevoId))

        context.dataStore.edit { prefs ->
            prefs[categoriaKey] = Json.encodeToString(lista)
        }
    }

    // Editar una categoría existente
    fun editarCategoria(actualizada: Categoria) = runBlocking {
        val lista = getCategorias().toMutableList()
        val index = lista.indexOfFirst { it.id == actualizada.id }
        if (index != -1) {
            lista[index] = actualizada
            context.dataStore.edit { prefs ->
                prefs[categoriaKey] = Json.encodeToString(lista)
            }
        }
    }

    // Eliminar una categoría por id
    fun eliminarCategoria(id: Long) = runBlocking {
        val nuevaLista = getCategorias().filter { it.id != id }
        context.dataStore.edit { prefs ->
            prefs[categoriaKey] = Json.encodeToString(nuevaLista)
        }
    }
}
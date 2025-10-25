package com.example.app_armario.Repositories

import android.content.Context
import com.example.app_armario.Models.Usuario
import com.example.app_armario.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

class UsuarioRepository(private val context: Context) {

    private val usuarioKey = stringPreferencesKey("usuarios")

    // 🔹 Obtener todos los usuarios
    fun getUsuarios(): List<Usuario> = runBlocking {
        context.dataStore.data.map { prefs ->
            prefs[usuarioKey]?.let {
                Json.decodeFromString<List<Usuario>>(it)
            } ?: emptyList()
        }.first()
    }

    // 🔹 Agregar usuario nuevo (sin duplicar email)
    fun agregarUsuario(usuario: Usuario) = runBlocking {
        val lista = getUsuarios().toMutableList()

        if (lista.none { it.email.equals(usuario.email, ignoreCase = true) }) {
            val nuevoId = (lista.maxOfOrNull { it.id } ?: 0) + 1
            lista.add(usuario.copy(id = nuevoId))
            guardarLista(lista)
        }
    }

    // 🔹 Editar usuario
    fun editarUsuario(actualizado: Usuario) = runBlocking {
        val lista = getUsuarios().toMutableList()
        val index = lista.indexOfFirst { it.id == actualizado.id }
        if (index != -1) {
            lista[index] = actualizado
            guardarLista(lista)
        }
    }

    // 🔹 Eliminar usuario
    fun eliminarUsuario(id: Long) = runBlocking {
        val nuevaLista = getUsuarios().filterNot { it.id == id }
        guardarLista(nuevaLista)
    }

    // 🔹 Buscar usuario por email
    fun buscarPorEmail(email: String): Usuario? = runBlocking {
        getUsuarios().find { it.email.equals(email, ignoreCase = true) }
    }

    // 🔹 Validar login (retorna el usuario si es válido)
    fun validarLogin(email: String, password: String): Usuario? = runBlocking {
        getUsuarios().find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    // 🔹 Guardar lista interna (evita repetir código)
    private fun guardarLista(lista: List<Usuario>) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs[usuarioKey] = Json.encodeToString(lista)
        }
    }
}
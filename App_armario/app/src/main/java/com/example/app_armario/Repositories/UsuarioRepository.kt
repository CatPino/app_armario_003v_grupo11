package com.example.app_armario.Repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.app_armario.Models.Rol
import com.example.app_armario.Models.RolesPredefinidos
import com.example.app_armario.Models.Usuario
import com.example.app_armario.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UsuarioRepository(private val context: Context) {

    private val usuarioKey = stringPreferencesKey("usuarios")

    // Obtener todos los usuarios
    fun getUsuarios(): List<Usuario> = runBlocking {
        context.dataStore.data.map { prefs ->
            prefs[usuarioKey]?.let { Json.decodeFromString<List<Usuario>>(it) } ?: emptyList()
        }.first()
    }

    // Agregar usuario nuevo (sin duplicar email)
    fun agregarUsuario(usuario: Usuario) = runBlocking {
        val lista = getUsuarios().toMutableList()

        val limpio = limpiarCampos(usuario)
        validarSegunRol(limpio)

        if (lista.none { it.email.equals(limpio.email, ignoreCase = true) }) {
            val nuevoId = (lista.maxOfOrNull { it.id } ?: 0) + 1
            lista.add(limpio.copy(id = nuevoId))
            guardarLista(lista)
        } else {
            // Si quieres, lanza una excepción aquí
            // throw IllegalArgumentException("El email ya está registrado")
        }
    }

    // Editar usuario
    fun editarUsuario(actualizado: Usuario) = runBlocking {
        val lista = getUsuarios().toMutableList()
        val index = lista.indexOfFirst { it.id == actualizado.id }
        if (index != -1) {
            val limpio = limpiarCampos(actualizado)

            // Evitar email duplicado en otro usuario
            val emailEnUsoPorOtro =
                lista.any { it.id != limpio.id && it.email.equals(limpio.email, ignoreCase = true) }
            if (emailEnUsoPorOtro) {
                // throw IllegalArgumentException("Ese email ya está en uso por otro usuario")
                return@runBlocking
            }

            validarSegunRol(limpio)
            lista[index] = limpio
            guardarLista(lista)
        }
    }

    // Eliminar usuario
    fun eliminarUsuario(id: Long) = runBlocking {
        val nuevaLista = getUsuarios().filterNot { it.id == id }
        guardarLista(nuevaLista)
    }

    // Buscar usuario por email
    fun buscarPorEmail(email: String): Usuario? = runBlocking {
        val e = email.trim().lowercase()
        getUsuarios().find { it.email.equals(e, ignoreCase = true) }
    }

    // Validar login
    fun validarLogin(email: String, password: String): Usuario? = runBlocking {
        val e = email.trim().lowercase()
        getUsuarios().find { it.email.equals(e, ignoreCase = true) && it.password == password }
    }

    // ================== Helpers internos ==================

    // Normaliza strings (trim) y pone el email en minúsculas
    private fun limpiarCampos(u: Usuario): Usuario {
        return u.copy(
            nombre = u.nombre.trim(),
            email = u.email.trim().lowercase(),
            password = u.password, // si quieres: u.password.trim()
            telefono = u.telefono?.trim()?.ifBlank { null },
            region = u.region?.trim()?.ifBlank { null },
            comuna = u.comuna?.trim()?.ifBlank { null },
            direccion = u.direccion?.trim()?.ifBlank { null }
        )
    }

    // Si es CLIENTE, exige región/comuna/dirección
    private fun validarSegunRol(u: Usuario) {
        val esCliente = (u.rol.nombre.equals(RolesPredefinidos.CLIENTE.nombre, ignoreCase = true))
        if (esCliente) {
            require(!u.region.isNullOrBlank()) { "La región es obligatoria para clientes." }
            require(!u.comuna.isNullOrBlank()) { "La comuna es obligatoria para clientes." }
            require(!u.direccion.isNullOrBlank()) { "La dirección es obligatoria para clientes." }
        }
    }

    // Persistencia
    private fun guardarLista(lista: List<Usuario>) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs[usuarioKey] = Json.encodeToString(lista)
        }
    }
}


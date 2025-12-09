package com.example.app_armario.Repositories

import com.example.app_armario.Models.RolesPredefinidos
import com.example.app_armario.Models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = db.collection("usuarios")

    suspend fun getUsuarios(): List<Usuario> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject<Usuario>() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun agregarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            // 1. Crear usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(usuario.email, usuario.password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("No se pudo crear el usuario en Firebase Auth."))

            // 2. Guardar datos adicionales en Firestore usando el UID de Auth como ID del documento
            val usuarioConId = usuario.copy(id = firebaseUser.uid)
            collection.document(firebaseUser.uid).set(usuarioConId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editarUsuario(actualizado: Usuario) {
        collection.document(actualizado.id).set(actualizado).await()
    }

    suspend fun eliminarUsuario(id: String) {
        collection.document(id).delete().await()
    }

    suspend fun buscarPorEmail(email: String): Usuario? {
        val e = email.trim().lowercase()
        val snapshot = collection.whereEqualTo("email", e).get().await()
        return snapshot.documents.firstOrNull()?.toObject<Usuario>()
    }

    suspend fun validarLogin(email: String, password: String): Usuario? {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            buscarPorEmail(email)
        } catch (e: Exception) {
            null
        }
    }
}
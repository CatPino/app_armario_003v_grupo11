package com.example.app_armario.Repositories

import android.content.Context
import com.example.app_armario.Models.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class CategoriaRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("categorias")

    // Obtener todas las categorías
    suspend fun getCategorias(): List<Categoria> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject<Categoria>() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Agregar una categoría nueva
    suspend fun agregarCategoria(categoria: Categoria) {
        val id = categoria.id.toString() // Usamos el ID numérico como String para el documento
        collection.document(id).set(categoria).await()
    }

    // Editar una categoría existente
    suspend fun editarCategoria(actualizada: Categoria) {
        val id = actualizada.id.toString()
        collection.document(id).set(actualizada).await()
    }

    // Eliminar una categoría por id
    suspend fun eliminarCategoria(id: Long) {
        collection.document(id.toString()).delete().await()
    }
}

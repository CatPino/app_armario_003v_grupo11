package com.example.app_armario.Repositories

import android.content.Context
import android.util.Log
import com.example.app_armario.Models.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class ProductoRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("productos")

    // Obtener todos los productos
    suspend fun getProductos(): List<Producto> {
        return try {
            val snapshot = collection.get().await()
            val lista = snapshot.documents.mapNotNull { it.toObject<Producto>() }
            Log.d("ProductoRepository", "Productos obtenidos: ${lista.size}")
            lista
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Error obteniendo productos", e)
            emptyList()
        }
    }

    // Agregar un producto nuevo
    suspend fun agregarProducto(producto: Producto) {
        try {
            collection.document(producto.id).set(producto).await()
            Log.d("ProductoRepository", "Producto agregado: ${producto.nombre}")
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Error agregando producto ${producto.nombre}", e)
        }
    }

    // Editar un producto existente
    suspend fun editarProducto(actualizado: Producto) {
        try {
            collection.document(actualizado.id).set(actualizado).await()
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Error editando producto", e)
        }
    }

    // Eliminar un producto por id
    suspend fun eliminarProducto(id: String) {
        try {
            collection.document(id).delete().await()
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Error eliminando producto", e)
        }
    }

    suspend fun descontarStock(idProducto: String, cantidadVendida: Int) {
        try {
            val docRef = collection.document(idProducto)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val nuevoStock = (snapshot.getLong("stock") ?: 0) - cantidadVendida
                if (nuevoStock >= 0) {
                    transaction.update(docRef, "stock", nuevoStock)
                }
            }.await()
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Error descontando stock", e)
        }
    }
}

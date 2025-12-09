package com.example.app_armario.Repositories

import android.content.Context
import com.example.app_armario.Models.Venta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class VentaRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("ventas")

    suspend fun registrarVenta(venta: Venta) {
        collection.document(venta.id).set(venta).await()
    }

    suspend fun obtenerVentas(): List<Venta> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject<Venta>() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

package com.example.app_armario

import android.content.Context
import android.util.Log
import com.example.app_armario.Repositories.ProductoRepository
import com.example.app_armario.data.productosMock

suspend fun seedProductosSiVacio(context: Context) {
    val repo = ProductoRepository(context)
    try {
        val actuales = repo.getProductos()
        if (actuales.isEmpty()) {
            Log.d("SEED", "Base de datos vacía. Insertando ${productosMock.size} productos...")
            productosMock.forEach { p ->
                repo.agregarProducto(p)
            }
            Log.d("SEED", "Carga inicial de productos completada.")
        } else {
            Log.d("SEED", "Ya existen ${actuales.size} productos en la base de datos.")
        }
    } catch (e: Exception) {
        Log.e("SEED", "Error durante el seed de productos", e)
    }
}

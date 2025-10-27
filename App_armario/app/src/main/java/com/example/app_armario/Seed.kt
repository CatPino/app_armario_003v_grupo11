package com.example.app_armario

import android.content.Context
import com.example.app_armario.Repositories.ProductoRepository
import com.example.app_armario.data.productosMock

fun seedProductosSiVacio(context: Context) {
    val repo = ProductoRepository(context)
    val actuales = repo.getProductos()
    if (actuales.isEmpty()) {
        productosMock.forEach { p -> repo.agregarProducto(p) }
    }
}

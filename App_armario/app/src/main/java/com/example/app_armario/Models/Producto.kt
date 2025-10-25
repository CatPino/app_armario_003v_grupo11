package com.example.app_armario.Models

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Long,
    val stock: Int,
    val imagenUrl: String? = null,
    val activo: Boolean = true,
    val categoria: Categoria,
    val tallas: List<String> = emptyList(),
    val colores: List<String> = emptyList(),
    val material: String? = null
)
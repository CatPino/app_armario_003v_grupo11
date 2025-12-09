package com.example.app_armario.Models

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Long = 0,
    val stock: Int = 0,
    val imagenUrl: String? = null,
    val activo: Boolean = true,
    val categoria: Categoria = Categoria(), // Usar constructor vacío
    val tallas: List<String> = emptyList(),
    val colores: List<String> = emptyList(),
    val material: String? = null
)

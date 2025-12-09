package com.example.app_armario.Models

// No es necesario @Serializable si se usa el método .toObject() de Firebase
data class CarritoItem(
    val idProducto: String = "",
    val nombre: String = "",
    val precio: Long = 0,
    val imagenUrl: String? = null,
    val cantidad: Int = 1
)

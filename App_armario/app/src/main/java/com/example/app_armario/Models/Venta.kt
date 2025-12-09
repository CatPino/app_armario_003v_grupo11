package com.example.app_armario.Models

// No es necesario @Serializable si se usa el método .toObject() de Firebase
data class Venta(
    val id: String = "",
    val fecha: String = "",
    val total: Long = 0,
    val productos: List<CarritoItem> = emptyList()
)

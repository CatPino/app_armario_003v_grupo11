package com.example.app_armario.Models

import kotlinx.serialization.Serializable

@Serializable
data class CarritoItem(
    val idProducto: Long,
    val nombre: String,
    val precio: Long,
    val imagenUrl: String? = null,
    val cantidad: Int = 1
)

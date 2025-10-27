package com.example.app_armario.Models
import kotlinx.serialization.Serializable

@Serializable
data class Venta(
    val id: String,
    val fecha: String,
    val total: Long,
    val productos: List<CarritoItem>
)

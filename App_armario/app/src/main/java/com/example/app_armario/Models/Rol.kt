package com.example.app_armario.Models
import kotlinx.serialization.Serializable

@Serializable
data class Rol(
    val id: Long = 0,
    val nombre: String // Ejemplo: "cliente", "admin"
)
object RolesPredefinidos {
    val ADMIN = Rol(1, "admin")
    val CLIENTE = Rol(2, "cliente")
}
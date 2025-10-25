package com.example.app_armario.Models
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String? = null,
    val rol: Rol = RolesPredefinidos.CLIENTE //  por defecto cliente
)
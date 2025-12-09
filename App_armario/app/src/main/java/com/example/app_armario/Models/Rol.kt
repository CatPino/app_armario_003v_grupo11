package com.example.app_armario.Models

data class Rol(
    val id: Long = 0,
    val nombre: String = "" // Necesario para Firestore
)

object RolesPredefinidos {
    val ADMIN = Rol(1, "admin")
    val CLIENTE = Rol(2, "cliente")
}

package com.example.app_armario.Models

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val password: String = "", // Nota: En producción real, no guardar password en Firestore, solo en Auth.
    val telefono: String? = null,
    val rol: Rol = RolesPredefinidos.CLIENTE,
    val region: String? = null,
    val comuna: String? = null,
    val direccion: String? = null
)

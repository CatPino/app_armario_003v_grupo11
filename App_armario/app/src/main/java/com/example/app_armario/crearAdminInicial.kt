package com.example.app_armario

import android.content.Context
import com.example.app_armario.Models.RolesPredefinidos
import com.example.app_armario.Models.Usuario
import com.example.app_armario.Repositories.UsuarioRepository

fun crearAdminInicial(context: Context) {
    val repo = UsuarioRepository(context)
    val adminExistente = repo.buscarPorEmail("admin@gmail.com")

    if (adminExistente == null) {
        val admin = Usuario(
            nombre = "Administrador",
            email = "admin@gmail.com",
            password = "Admin123#",
            rol = RolesPredefinidos.ADMIN
        )
        repo.agregarUsuario(admin)
    }
}
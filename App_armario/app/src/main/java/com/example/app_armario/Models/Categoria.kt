package com.example.app_armario.Models

import kotlinx.serialization.Serializable

@Serializable
data class Categoria(
    val id: Long = 0,
    val nombre: String,
    val descripcion: String? = null
) {
    companion object {
        val POLERAS = Categoria(1, "Poleras")
        val FALDAS = Categoria(2, "Faldas")
        val CALZAS = Categoria(3, "Calzas")
        val ACCESORIOS = Categoria(4, "Accesorios")

        // Si alguna vez quieres obtener todas juntas:
        val TODAS = listOf(POLERAS, FALDAS, CALZAS, ACCESORIOS)
    }
}

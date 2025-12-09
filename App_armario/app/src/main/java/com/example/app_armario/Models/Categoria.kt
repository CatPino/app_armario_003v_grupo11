package com.example.app_armario.Models

data class Categoria(
    val id: Long = 0,
    val nombre: String = "" // Necesario para Firestore
) {
    companion object {
        val POLERAS = Categoria(1, "Poleras")
        val FALDAS = Categoria(2, "Faldas")
        val CALZAS = Categoria(3, "Calzas")
        val ACCESORIOS = Categoria(4, "Accesorios")

        val TODAS = listOf(POLERAS, FALDAS, CALZAS, ACCESORIOS)
    }
}

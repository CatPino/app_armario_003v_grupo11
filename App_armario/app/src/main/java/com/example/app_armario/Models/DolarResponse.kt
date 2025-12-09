package com.example.app_armario.Models

import com.google.gson.annotations.SerializedName

data class DolarResponse(
    @SerializedName("serie") val serie: List<DolarSerie>
)

data class DolarSerie(
    @SerializedName("fecha") val fecha: String,
    @SerializedName("valor") val valor: Double
)

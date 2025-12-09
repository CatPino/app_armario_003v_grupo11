package com.example.app_armario.Repositories

import com.example.app_armario.Models.DolarResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MindicadorApi {
    @GET("dolar")
    suspend fun getDolar(): DolarResponse
}

class DolarRepository {
    private val api: MindicadorApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mindicador.cl/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(MindicadorApi::class.java)
    }

    suspend fun obtenerValorDolar(): Double? {
        return try {
            val response = api.getDolar()
            // Retorna el primer valor de la serie (el más actual)
            response.serie.firstOrNull()?.valor
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

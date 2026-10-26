package com.example.ep2.network

import com.example.ep2.model.PokeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {

    @GET("pokemon/{name}")
    fun obtenerPokemon(
        @Path("name") nombre: String
    ): Call<PokeResponse>
}
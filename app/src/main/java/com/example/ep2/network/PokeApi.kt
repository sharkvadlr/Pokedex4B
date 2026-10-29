package com.example.ep2.network

import com.example.ep2.model.Pokemon
import com.example.ep2.model.PokeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {

    // Devuelve los detalles individuales de un Pokémon
    @GET("pokemon/{name}")
    fun obtenerPokemon(
        @Path("name") nombre: String
    ): Call<Pokemon>

    // Devuelve la lista de Pokémon que pertenecen a una generación
    @GET("generation/{id}")
    fun obtenerGeneracion(
        @Path("id") idGeneracion: Int
    ): Call<PokeResponse>
}
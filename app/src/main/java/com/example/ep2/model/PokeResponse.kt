package com.example.ep2.model

import com.google.gson.annotations.SerializedName

// ARCHIVO PARA RECIBIR LA RESPUESTA O DETALLES DEL POKÉMON

data class PokeResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<PokemonResult>?
)

data class PokemonResult(
    val name: String?,
    val url: String?
)
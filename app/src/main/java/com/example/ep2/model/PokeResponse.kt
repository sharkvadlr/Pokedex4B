package com.example.ep2.model

import com.google.gson.annotations.SerializedName

data class PokeResponse(
    val id: Int?,
    val name: String?,
    @SerializedName("pokemon_species")
    val pokemonSpecies: List<PokemonSpecies>?
)

data class PokemonSpecies(
    val name: String?,
    val url: String?
)
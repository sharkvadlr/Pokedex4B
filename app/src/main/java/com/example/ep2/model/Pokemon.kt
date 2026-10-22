package com.example.ep2.model

import com.google.gson.annotations.SerializedName

data class Pokemon(

    val id : Int?,
    val name : String?,
    val height : Int?,
    val weight : Int?,
    val sprites: PokemonSprites?,
)

data class PokemonSprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("back_default") val backDefault: String?
)

package com.example.ep2.model

import com.example.ep2.model.Pokemon.Sprites
data class PokeResponse(
    val id: Int?,
    val name: String?,
    val height: Int?,
    val weight: Int?,
    val sprites: Sprites?
)
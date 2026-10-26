package com.example.ep2.model

import com.google.gson.annotations.SerializedName

// ARCHIVO PARA OBTENER LA INFORMACIÓN DEL POKÉMON

data class Pokemon(
    val id: Int?,
    val name: String?,
    val height: Int?,
    val weight: Int?,
    val sprites: Sprites?
) {
    // MÉTODO PARA OBTENER LA IMAGEN
    fun obtenerImagen(): String {
        return sprites?.other?.officialArtwork?.frontDefault
            ?: sprites?.frontDefault
            ?: ""
    }
}

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String?,
    @SerializedName("other")
    val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String?
)
package com.example.ep2.model

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val id: Int?,
    val name: String?,
    val height: Int?,
    val weight: Int?,
    val sprites: Sprites?
) {
    fun obtenerImagen(): String {
        return sprites?.other?.officialArtwork?.frontDefault
            ?: sprites?.frontDefault
            ?: ""
    }

    fun obtenerGif(): String {
        return sprites?.other?.showdown?.frontDefault
            ?: sprites?.versions?.generationV?.blackWhite?.animated?.frontDefault
            ?: obtenerImagen()
    }

    data class Sprites(
        @SerializedName("front_default")
        val frontDefault: String?,
        @SerializedName("other")
        val other: OtherSprites?,
        @SerializedName("versions")
        val versions: Versions?
    )

    data class OtherSprites(
        @SerializedName("official-artwork")
        val officialArtwork: OfficialArtwork?,
        @SerializedName("showdown")
        val showdown: Showdown?
    )

    data class OfficialArtwork(
        @SerializedName("front_default")
        val frontDefault: String?
    )

    data class Showdown(
        @SerializedName("front_default")
        val frontDefault: String?
    )

    data class Versions(
        @SerializedName("generation-v")
        val generationV: GenerationV?
    )

    data class GenerationV(
        @SerializedName("black-white")
        val blackWhite: BlackWhite?
    )

    data class BlackWhite(
        @SerializedName("animated")
        val animated: Animated?
    )

    data class Animated(
        @SerializedName("front_default")
        val frontDefault: String?
    )
}
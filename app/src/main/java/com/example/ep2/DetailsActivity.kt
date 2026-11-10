package com.example.ep2

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {

    private lateinit var imgPokemonDetalle: ImageView
    private lateinit var btnFavorito: ImageButton
    private lateinit var tvNombrePokemonDetalle: TextView
    private lateinit var tvIdPokemonDetalle: TextView
    private lateinit var tvDetallesPokemon: TextView
    private lateinit var db: DatabaseHelper
    private var pokemonFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

        imgPokemonDetalle = findViewById(R.id.imgPokemonDetalle)
        btnFavorito = findViewById(R.id.btnFavorito)
        tvNombrePokemonDetalle = findViewById(R.id.tvNombrePokemonDetalle)
        tvIdPokemonDetalle = findViewById(R.id.tvIdPokemonDetalle)
        tvDetallesPokemon = findViewById(R.id.tvDetallesPokemon)
        db = DatabaseHelper(this)
        mostrarInformacionPokemon()
    }

    // MÉTODO PARA MOSTRAR LA INFORMACION DE UN ALBUM
    private fun mostrarInformacionPokemon() {

        val idPokemonString = intent.getStringExtra("ID_POKEMON") ?: "0"
        val idPokemon = idPokemonString.toLongOrNull() ?: 0L
        val nombrePokemon = intent.getStringExtra("NOMBRE_POKEMON") ?: "Pokémon sin nombre"
        val alturaPokemon = intent.getIntExtra("ALTURA_POKEMON", 0)
        val pesoPokemon = intent.getIntExtra("PESO_POKEMON", 0)
        val imagenPokemon = intent.getStringExtra("IMG_POKEMON") ?: ""

        val nombreFormatted = nombrePokemon.replaceFirstChar { it.uppercase() }
        val alturaMetros = alturaPokemon / 10.0
        val pesoKg = pesoPokemon / 10.0

        tvNombrePokemonDetalle.text = nombreFormatted
        tvIdPokemonDetalle.text = "#$idPokemonString"
        tvDetallesPokemon.text = "Altura: ${alturaMetros} m | Peso: ${pesoKg} kg"

        Glide.with(this)
            .asGif()
            .load(imagenPokemon)
            .into(imgPokemonDetalle)

        // Verificar que el album exista como favorito
        if (idPokemon != -1L) {
            pokemonFavorito = db.existePokemonFavorito(idPokemon)
            if (pokemonFavorito) {
                btnFavorito.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                btnFavorito.setImageResource(R.drawable.no_favorite)
            }
        }

        btnFavorito.setOnClickListener {


            if (pokemonFavorito) {
                //btnFavorito.setImageResource(R.drawable.baseline_favorite_24)
                val eliminado = db.eliminarAlbumFavorito(idPokemon)
                btnFavorito.setImageResource(R.drawable.no_favorite)
                if (eliminado) {
                    pokemonFavorito = !pokemonFavorito
                    Toast.makeText(this, "Pokemon $nombreFormatted eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }
            } else {
                val guardado = db.guardarPokemonFavorito(
                    idPokemon = idPokemon,
                    nombrePokemon = nombreFormatted,
                    alturaPokemon = "${alturaMetros} m",
                    pesoPokemon = "${pesoKg} kg",
                    imagenPokemon = imagenPokemon
                )
                btnFavorito.setImageResource(R.drawable.baseline_favorite_24)
                // Evaluar si la inseción fue correcta
                if (guardado) {
                    pokemonFavorito = !pokemonFavorito
                    Toast.makeText(this, "Pokemon $nombreFormatted agregado a favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Pokemon $nombreFormatted ya se encuentra en favoritos", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
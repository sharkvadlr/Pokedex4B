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
    private lateinit var tvNombrePokemonDetalle: TextView
    private lateinit var tvIdPokemonDetalle: TextView
    private lateinit var tvDetallesPokemon: TextView
    private lateinit var btnFavorito: ImageButton


    private lateinit var db : DatabaseHelper


    private var esFavorito =
        false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)


        imgPokemonDetalle = findViewById(R.id.imgPokemonDetalle)
        tvNombrePokemonDetalle = findViewById(R.id.tvNombrePokemonDetalle)
        tvIdPokemonDetalle = findViewById(R.id.tvIdPokemonDetalle)
        tvDetallesPokemon = findViewById(R.id.tvDetallesPokemon)
        btnFavorito = findViewById(R.id.btnFavorito)
        db = DatabaseHelper(this)


        mostrarInformacionPokemon()
    }


    private fun mostrarInformacionPokemon() {
        val idPokemon = intent.getStringExtra("ID_POKEMON") ?:
        "0"
        val nombrePokemon = intent.getStringExtra("NOMBRE_POKEMON") ?:
        "Pokémon"
        val alturaPokemon = intent.getIntExtra("ALTURA_POKEMON", 0)
        val pesoPokemon = intent.getIntExtra("PESO_POKEMON", 0)
        val imagenPokemon = intent.getStringExtra("IMG_POKEMON") ?:
        ""
        val nombreFormatted = nombrePokemon.replaceFirstChar { it.uppercase()
        }
        tvNombrePokemonDetalle.text = nombreFormatted
        tvIdPokemonDetalle.text = "#$idPokemon
        "
        val alturaMetros = alturaPokemon /
                10.0
        val pesoKg = pesoPokemon /
                10.0
        tvDetallesPokemon.text = "Altura: ${alturaMetros} m | Peso: ${pesoKg}
        kg"


        Glide.with(this)
            .asGif()
            .load(imagenPokemon)
            .into(imgPokemonDetalle)






        btnFavorito.setOnClickListener
        {
            btnFavorito.setImageResource(R.drawable.no_favorite)


            if (esFavorito) {


                val eliminar = db.eliminarFavorito(idPokemon)


                if (eliminar) {


                    esFavorito = !
                    esFavorito
                    Toast.makeText(this, "Pokemon $nombrePokemon eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }
            }else{
                val  guardado = db.guardarPokemonFavorito(


                )
                btnFavorito.setImageResource(R.drawable.baseline_favorite_24)


                if (guardado){
                    esFavorito = !
                    esFavorito
                    Toast.makeText(this, "Pokemon $nombrePokemon agregado a favoritos", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Pokemon $nombrePokemon ya se encuentra en favoritos",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
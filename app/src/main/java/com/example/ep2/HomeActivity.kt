package com.example.ep2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ep2.model.Pokemon
import com.example.ep2.model.PokeResponse
import com.example.ep2.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var contenedorPokemon: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        tvResultado = findViewById(R.id.tvEstado)
        contenedorPokemon = findViewById(R.id.contenedorPokemon)

        contenedorPokemon.removeAllViews()

        // Cargamos las 9 generaciones ordenadas
        cargarGeneracionSecuencial(1, 9)
    }

    private fun cargarGeneracionSecuencial(genIdActual: Int, maxGen: Int) {
        if (genIdActual > maxGen) {
            tvResultado.visibility = View.GONE
            return
        }

        RetrofitClient.api.obtenerGeneracion(genIdActual).enqueue(object : Callback<PokeResponse> {
            override fun onResponse(call: Call<PokeResponse>, response: Response<PokeResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val pokeResponse = response.body()!!
                    val especies = pokeResponse.pokemonSpecies ?: emptyList()

                    val vistaSeccion = layoutInflater.inflate(
                        R.layout.item_pokemon,
                        contenedorPokemon,
                        false
                    )

                    val tvNombreGeneracion = vistaSeccion.findViewById<TextView>(R.id.tvTitulo)
                    val contenedorHorizontal = vistaSeccion.findViewById<LinearLayout>(R.id.contenedorHorizontal)

                    tvNombreGeneracion.text = "Generación $genIdActual"
                    contenedorPokemon.addView(vistaSeccion)

                    val especiesOrdenadas = especies.sortedBy { especie ->
                        especie.url?.trimEnd('/')?.substringAfterLast('/')?.toIntOrNull() ?: Int.MAX_VALUE
                    }

                    val limite = if (especiesOrdenadas.size > 15) 15 else especiesOrdenadas.size

                    for (i in 0 until limite) {
                        val especie = especiesOrdenadas[i]
                        especie.name?.let { nombre ->
                            val vistaPokemon = layoutInflater.inflate(
                                R.layout.item_card_pokemon,
                                contenedorHorizontal,
                                false
                            )

                            contenedorHorizontal.addView(vistaPokemon)
                            obtenerYMostrarPokemonIndividual(nombre, vistaPokemon)
                        }
                    }

                    cargarGeneracionSecuencial(genIdActual + 1, maxGen)
                } else {
                    cargarGeneracionSecuencial(genIdActual + 1, maxGen)
                }
            }

            override fun onFailure(call: Call<PokeResponse>, t: Throwable) {
                cargarGeneracionSecuencial(genIdActual + 1, maxGen)
            }
        })
    }

    private fun obtenerYMostrarPokemonIndividual(nombre: String, vistaPokemon: View) {
        RetrofitClient.api.obtenerPokemon(nombre).enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                if (response.isSuccessful && response.body() != null) {
                    val pokemon = response.body()!!
                    poblarVistaPokemon(pokemon, vistaPokemon)
                }
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {}
        })
    }

    private fun poblarVistaPokemon(pokemon: Pokemon, vistaPokemon: View) {
        val imgPokemon = vistaPokemon.findViewById<ImageView>(R.id.imgPokemon)
        val tvNombrePokemon = vistaPokemon.findViewById<TextView>(R.id.tvNombrePokemon)
        val tvIdPokemon = vistaPokemon.findViewById<TextView>(R.id.tvIDPokemon)

        tvNombrePokemon.text = pokemon.name?.replaceFirstChar { it.uppercase() } ?: "Pokemon sin nombre"
        tvIdPokemon.text = "#${pokemon.id}"

        Glide.with(this@HomeActivity)
            .load(pokemon.obtenerImagen())
            .into(imgPokemon)

        vistaPokemon.setOnClickListener {
            val idPokemon = pokemon.id

            if (idPokemon != null) {
                val intent = Intent(this@HomeActivity, DetailsActivity::class.java).apply {
                    putExtra("ID_POKEMON", idPokemon.toString())
                    putExtra("NOMBRE_POKEMON", pokemon.name)
                    putExtra("ALTURA_POKEMON", pokemon.height ?: 0)
                    putExtra("PESO_POKEMON", pokemon.weight ?: 0)
                    putExtra("IMG_POKEMON", pokemon.obtenerGif()) // 👈 Envía el GIF animado
                }
                startActivity(intent)
            }
        }
    }
}
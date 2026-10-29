package com.example.ep2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

                    // Inflamos la sección de la generación
                    val vistaSeccion = layoutInflater.inflate(
                        R.layout.item_pokemon,
                        contenedorPokemon,
                        false
                    )

                    val tvNombreGeneracion = vistaSeccion.findViewById<TextView>(R.id.tvTitulo)
                    val contenedorHorizontal = vistaSeccion.findViewById<LinearLayout>(R.id.contenedorPokemon)

                    tvNombreGeneracion.text = "Generación $genIdActual"

                    contenedorPokemon.addView(vistaSeccion)

                    // ORDENAR POKÉMON POR ID EXTRAYÉNDOLO DE LA URL
                    val especiesOrdenadas = especies.sortedBy { especie ->
                        especie.url?.trimEnd('/')?.substringAfterLast('/')?.toIntOrNull() ?: Int.MAX_VALUE
                    }

                    // 1. Primero creamos e insertamos los placeholders en el orden correcto
                    for (especie in especiesOrdenadas) {
                        especie.name?.let { nombre ->
                            val vistaPokemon = layoutInflater.inflate(
                                R.layout.item_card_pokemon,
                                contenedorHorizontal,
                                false
                            )
                            // Insertamos la vista vacía inmediatamente en el contenedor horizontal
                            contenedorHorizontal.addView(vistaPokemon)

                            // 2. Cargamos los datos del Pokémon para esa vista en específico
                            obtenerYMostrarPokemonIndividual(nombre, vistaPokemon)
                        }
                    }

                    // Pasar a la siguiente generación
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

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                // Si falla, se puede ocultar o dejar la vista
            }
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
                    putExtra("ALTURA_POKEMON", pokemon.height)
                    putExtra("PESO_POKEMON", pokemon.weight)
                    putExtra("IMG_POKEMON", pokemon.obtenerImagen())
                }
                startActivity(intent)
            }
        }
    }
}
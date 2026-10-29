package com.example.ep2

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

        obtenerPokemon("Pikachu", contenedorPokemon)
    }

    private fun obtenerPokemon(nombre : String, contenedor : LinearLayout) {

        RetrofitClient.api.obtenerPokemon(
            nombre = "pikachu"

        ).enqueue(object : Callback<PokeResponse> {
            override fun onResponse(
                call: Call<PokeResponse>,
                response: Response<PokeResponse>
            ) {
                val pokeResponse = response.body()

                if (response.isSuccessful && pokeResponse != null) {

                    tvResultado.visibility = View.GONE
                    mostrarPokemon(pokemon, contenedor)


                } else {
                    Toast.makeText(this@HomeActivity, "Pokémon no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PokeResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en la API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarPokemon(pokemones: List<Pokemon>, contenedor: LinearLayout){

        contenedor.removeAllViews()

        for (pokemon in pokemones){

            val vistaPokemon = layoutInflater.inflate(
                R.layout.item_pokemon,
                contenedor,
                false
            )

            val imgPokemon = vistaPokemon.findViewById<ImageView>(R.id.imgPokemon)
            val tvNombrePokemon = vistaPokemon.findViewById<TextView>(R.id.tvNombrePokemon)
            val tvIdPokemon = vistaPokemon.findViewById<TextView>(R.id.tvIDPokemon)

            tvNombrePokemon.text = pokemon.name ?: "Pokemon sin nombre"
            tvIdPokemon.text = pokemon.id ?: "ID desconocido"

            Glide.with(this@HomeActivity)
                .load(pokemon.obtenerImagen())
                .into(imgPokemon)
        }

    }





    }


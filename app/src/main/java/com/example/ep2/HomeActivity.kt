package com.example.ep2

import android.os.Bundle
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

        tvResultado = findViewById(R.id.tvResultado)
        contenedorPokemon = findViewById(R.id.contenedorPokemon)

        obtenerPokemon()
    }

    private fun obtenerPokemon(generacion : String, contenedor : LinearLayout) {

        RetrofitClient.api.obtenerPokemon(
            nombre = "pikachu"

        ).enqueue(object : Callback<PokeResponse> {
            override fun onResponse(
                call: Call<PokeResponse>,
                response: Response<PokeResponse>
            ) {
                val pokeResponse = response.body()

                if (response.isSuccessful && pokeResponse != null) {
                    val pokemon = Pokemon(
                        id = pokeResponse.id,
                        name = pokeResponse.name,
                        height = pokeResponse.height,
                        weight = pokeResponse.weight,
                        sprites = pokeResponse.sprites
                    )

                    tvResultado.text =
                        "Nombre: ${pokemon.name?.replaceFirstChar { it.uppercase() }} \n\n" +
                                "ID: #${pokemon.id} \n\n" +
                                "Altura: ${pokemon.height} \n\n" +
                                "Peso: ${pokemon.weight} \n\n"

                    val imageUrl = pokemon.obtenerImagen()

                    Glide.with(this@HomeActivity)
                        .load(imageUrl)
                        .into(imgPoke)
                } else {
                    Toast.makeText(this@HomeActivity, "Pokémon no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PokeResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en la API", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
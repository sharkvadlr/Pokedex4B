package com.example.ep2

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ep2.model.Pokemon
import com.example.ep2.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var imgPoke: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        tvResultado = findViewById(R.id.tvResultado)
        imgPoke = findViewById(R.id.imgPoke)

        obtenerPokemon("tapu-koko")
    }

    private fun obtenerPokemon(nombrePokemon: String) {
        RetrofitClient.api.obtenerPokemon(nombrePokemon).enqueue(object : Callback<Pokemon> {
            override fun onResponse(
                call: Call<Pokemon>,
                response: Response<Pokemon>
            ) {
                if (response.isSuccessful) {
                    val pokemon = response.body()

                    if (pokemon != null) {
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
                } else {
                    Toast.makeText(this@HomeActivity, "Pokémon no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en la red o en el API", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
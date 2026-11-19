package com.example.ep2

import android.content.Intent
import androidx.credentials.CredentialManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ep2.model.Pokemon
import com.example.ep2.model.PokeResponse
import com.example.ep2.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var contenedorPokemon: LinearLayout
    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager : CredentialManager
    private lateinit var bottomNavigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        tvResultado = findViewById(R.id.tvEstado)
        contenedorPokemon = findViewById(R.id.contenedorPokemon)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        contenedorPokemon.removeAllViews()

        cargarGeneracionSecuencial(1, 9)

        menuNavegacion()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.nav_inicio
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

        if (!isDestroyed && !isFinishing) {
            Glide.with(this@HomeActivity)
                .load(pokemon.obtenerImagen())
                .into(imgPokemon)
        }

        vistaPokemon.setOnClickListener {
            val idPokemon = pokemon.id

            if (idPokemon != null) {
                val intent = Intent(this@HomeActivity, DetailsActivity::class.java).apply {
                    putExtra("ID_POKEMON", idPokemon.toString())
                    putExtra("NOMBRE_POKEMON", pokemon.name)
                    putExtra("ALTURA_POKEMON", pokemon.height ?: 0)
                    putExtra("PESO_POKEMON", pokemon.weight ?: 0)
                    putExtra("IMG_POKEMON", pokemon.obtenerGif())
                }
                startActivity(intent)
            }
        }
    }

    private fun menuNavegacion(){
        bottomNavigation.selectedItemId = R.id.nav_inicio

        bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_inicio -> {
                    true
                }

                R.id.nav_favoritos -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_salir ->{
                    cerrarSesionGoogle()
                    true
                }

                else -> false
            }
        }
    }

    private fun cerrarSesionGoogle(){
        auth.signOut()

        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
            } catch (e : Exception){
                Toast.makeText(this@HomeActivity, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
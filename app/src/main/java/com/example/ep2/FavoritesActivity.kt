package com.example.ep2

import android.content.Intent
import androidx.credentials.CredentialManager
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ep2.model.PokemonFavorito
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {
    private lateinit var bottomNavigation : BottomNavigationView

    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var gridFavoritos : GridLayout
    private lateinit var tvEstadoFavoritos : TextView
    private lateinit var dbHelper : DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        gridFavoritos = findViewById(R.id.gridFavoritos)
        tvEstadoFavoritos = findViewById(R.id.tvEstadoFavoritos)
        dbHelper = DatabaseHelper(this)

        menuNavegacion()
        consultarFavoritos()
    }

    override fun onResume(){
        super.onResume()
        bottomNavigation.selectedItemId = R.id.nav_favoritos
        consultarFavoritos()
    }

    private fun menuNavegacion(){
        bottomNavigation.selectedItemId = R.id.nav_favoritos

        bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_inicio -> {
                    finish()
                    true
                }

                R.id.nav_favoritos -> {
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
                Toast.makeText(this@FavoritesActivity, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@FavoritesActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun mostrarPokemonesFavoritos(favoritos : List<PokemonFavorito>){
        for (pokemon in favoritos){
            val vistaPokemon = layoutInflater.inflate(
                R.layout.item_favoritos,
                gridFavoritos,
                false
            )

            val imgAlbum = vistaPokemon.findViewById<ImageView>(R.id.imgFavorito)

            Glide.with(this)
                .load(pokemon.imagenPokemon)
                .centerCrop()
                .into(imgAlbum)

            vistaPokemon.setOnClickListener {
                val intent = Intent(this, DetailsActivity::class.java)

                intent.putExtra("ID_POKEMON", pokemon.idPokemon.toString())
                intent.putExtra("NOMBRE_POKEMON", pokemon.nombrePokemon)
                intent.putExtra("ALTURA_POKEMON", pokemon.alturaPokemon)
                intent.putExtra("PESO_POKEMON", pokemon.pesoPokemon)
                intent.putExtra("IMG_POKEMON", pokemon.imagenPokemon)

                startActivity(intent)
            }

            gridFavoritos.addView(vistaPokemon)
        }
    }

    private fun consultarFavoritos(){
        val favoritos = dbHelper.obtenerPokemonesFavoritos()

        gridFavoritos.removeAllViews()

        if (favoritos.isEmpty()){
            tvEstadoFavoritos.visibility = View.VISIBLE
            tvEstadoFavoritos.text = "No tienes ningun pokemon en Favoritos"
        }else{
            tvEstadoFavoritos.visibility = View.GONE
            mostrarPokemonesFavoritos(favoritos)
        }
    }
}
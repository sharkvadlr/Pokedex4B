package com.example.ep2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {


    companion object{
        private const val CANAL_ID = "canal_general"
        private const val NOTIFICACION_ID = 1
        private const val CODIGO_PERMISO = 100
    }
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
        crearCanalNoti()
    }

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
                    alturaPokemon = alturaPokemon.toString(),
                    pesoPokemon = pesoPokemon.toString(),
                    imagenPokemon = imagenPokemon
                )
                btnFavorito.setImageResource(R.drawable.baseline_favorite_24)
                if (guardado) {
                    pokemonFavorito = !pokemonFavorito
                    Toast.makeText(this, "Pokemon $nombreFormatted agregado a favoritos", Toast.LENGTH_SHORT).show()
                    mostrarNoti()
                } else {
                    Toast.makeText(this, "Pokemon $nombreFormatted ya se encuentra en favoritos", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun crearCanalNoti(){

        //Evaluar el permiso del usuario para manejar notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val canal = NotificationChannel(
                CANAL_ID,
                "Notificaciones generales",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            canal.description = "Canal de envío de notificaciones sobre la BD"

            //Indicar el uso de notificaciones
            val adminNotificaciones = getSystemService(NotificationManager::class.java)

            adminNotificaciones.createNotificationChannel(canal)
        }
    }

    private fun mostrarNoti(){

        //VERIFICAR LOS PERMISOS SELECCIONADOS POR EL USUARIO
        //Si el usuario no ha dado ermiso de enviar notificaciones
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ){

            //Volver a pedir permiso al usuario
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                com.example.ep2.DetailsActivity.Companion.CODIGO_PERMISO
            )
            Toast.makeText(this, "Autoriza el envio de notificaciones", Toast.LENGTH_SHORT).show()

            return
        }

        val notificacion = NotificationCompat.Builder(this,
            com.example.ep2.DetailsActivity.Companion.CANAL_ID
        )
            .setSmallIcon(R.drawable.pokefav)
            .setContentTitle("Pokedex")
            .setContentText("Un pokemon fue añadido a la base de datos")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(this)
            .notify(
                com.example.ep2.DetailsActivity.Companion.NOTIFICACION_ID,
                notificacion
            )

    }
}
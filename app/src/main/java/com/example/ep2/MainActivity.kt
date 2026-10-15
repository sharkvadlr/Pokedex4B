package com.example.ep2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var etCorreo : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnIngresar : Button

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPass)
        btnIngresar = findViewById(R.id.btnIngresar)

        dbHelper = DatabaseHelper(this)

        //Evento para iniciar sesion
        btnIngresar.setOnClickListener {
            //llamar metodo de login
            iniciarSesion()
        }

        tvCrearCuenta.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    //METODO LOGIN
    private fun iniciarSesion(){
        val correo = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if(correo.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Llamar al metodo login en la BD
        val resultado = dbHelper.iniciarSesion(correo, pass)

        //Validar si el login es correcto
        if(resultado){
            //El resultado es true
            Toast.makeText(this, "Bienvenido (˶ˆᗜˆ˵)", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            //El resultado es falso
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }
    }
}
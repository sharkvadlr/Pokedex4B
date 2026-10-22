package com.example.ep2.network

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Archivo que permite tener un objeto para la conexion a internet

object RetrofitClient {

    //Constante para tener la base URL

    private const val BASE_URL = "https://itunes.apple.com"

    //Variable que ereda de Lazy
    val api : ITunesApi by lazy {

        //Construir la URL completa
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }
}
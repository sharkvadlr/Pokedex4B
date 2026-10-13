package com.example.ep2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//BASE DE DATOS DEL INICIO DE SESION

//DatabaseHelper hereda de la cls SQLite
class DatabaseHelper (context: Context) : SQLiteOpenHelper (
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
){
    //DATOS DE LA BD
    companion object{

        //INFO DE LA BD
        private const val DATABASE_NAME = "pokedex.bd"
        private const val DATABASE_VERSION = 1

        //Info. de las tablas en la bd
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"
    }

    //Metodo onCreate --- Crea la BD
    override fun onCreate(db: SQLiteDatabase?) {
        //Crear la tabla
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS(
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_NOMBRE TEXT NOT NULL,
            $COL_CORREO TEXT NOT NULL,
            $COL_PASSWORD TEXT NOT NULL
            )
            """.trimIndent()

        //Crear la tabla de usuarios
        db?.execSQL(crearTablaUsuarios)
    }

    //Metodo onUpgrade --- Actualiza la BD
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    //Metodo pra crear un usuario
    fun registrarUsuario(nombre : String, correo : String, password : String) : Boolean {

        //Crear variable para escribir en la BD
        val db = writableDatabase

        //Insertar los datos
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }

        //Ejecutar la insercion de datos
        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        //Cerrar conexion de la BD
        db.close()

        //Regresar e valor obtenido de la insercion (true or false)
        return  resultado != -1L
    }
}
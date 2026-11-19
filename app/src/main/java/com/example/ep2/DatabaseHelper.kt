package com.example.ep2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ep2.model.PokemonFavorito

class DatabaseHelper (context: Context) : SQLiteOpenHelper (
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
){
    companion object{
        private const val DATABASE_NAME = "pokedex.bd"
        private const val DATABASE_VERSION = 2

        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

        private const val TABLE_FAVORITOS = "pokemonFavorito"
        private const val COL_ID_FAVORITO = "id"
        private const val COL_ID_POKEMON = "idPokemon"
        private const val COL_NOMBRE_POKEMON = "nombrePokemom"
        private const val COL_ALTURA_POKEMON = "alturaPokemon"
        private const val COL_PESO_P0KEMON = "pesoPokemon"
        private const val COL_IMAGEN = "imagenPokemon"

        private const val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS(
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_NOMBRE TEXT NOT NULL,
            $COL_CORREO TEXT NOT NULL,
            $COL_PASSWORD TEXT NOT NULL
            )
            """

        private const val crearTablaFavoritos = """
            CREATE TABLE $TABLE_FAVORITOS(
                $COL_ID_FAVORITO INTEGER PRIMARY KEY,
                $COL_ID_POKEMON INTEGER UNIQUE NOT NULL,
                $COL_NOMBRE_POKEMON TEXT NOT NULL,
                $COL_ALTURA_POKEMON TEXT,
                $COL_PESO_P0KEMON TEXT,
                $COL_IMAGEN TEXT
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(crearTablaUsuarios)
        db?.execSQL(crearTablaFavoritos)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2){
            db?.execSQL(crearTablaFavoritos)
        }
    }

    fun registrarUsuario(nombre : String, correo : String, password : String) : Boolean {
        val db = writableDatabase

        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }

        val resultado = db.insert(TABLE_USUARIOS, null, datos)
        db.close()

        return resultado != -1L
    }

    fun iniciarSesion(email : String, password: String) : Boolean {
        val db = readableDatabase

        val query_sql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
        """.trimIndent()

        val cursor = db.rawQuery(
            query_sql, arrayOf(email, password)
        )

        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return usuarioEncontrado
    }

    fun guardarPokemonFavorito(idPokemon: Long, nombrePokemon: String, alturaPokemon: String, pesoPokemon: String, imagenPokemon: String) : Boolean {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put(COL_ID_POKEMON, idPokemon)
            put(COL_NOMBRE_POKEMON, nombrePokemon)
            put(COL_ALTURA_POKEMON, alturaPokemon)
            put(COL_PESO_P0KEMON, pesoPokemon)
            put(COL_IMAGEN, imagenPokemon)
        }

        val resultado = db.insertWithOnConflict(
            TABLE_FAVORITOS,
            null,
            valores,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        db.close()
        return resultado != -1L
    }

    fun eliminarAlbumFavorito(idPokemon: Long) : Boolean{
        val db = writableDatabase

        val filas_afectadas = db.delete(
            TABLE_FAVORITOS,
            "$COL_ID_POKEMON = ?",
            arrayOf(idPokemon.toString())
        )
        db.close()
        return filas_afectadas > 0
    }

    fun existePokemonFavorito(idPokemon: Long) : Boolean{
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT $COL_ID_POKEMON FROM $TABLE_FAVORITOS WHERE $COL_ID_POKEMON = ?",
            arrayOf(idPokemon.toString())
        )

        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()
        return existe
    }

    fun obtenerPokemonesFavoritos(): List<PokemonFavorito>{
        val listaFavoritos = mutableListOf<PokemonFavorito>()
        val db = readableDatabase

        val query_sql = """
            SELECT
            $COL_ID_POKEMON,
            $COL_NOMBRE_POKEMON,
            $COL_ALTURA_POKEMON,
            $COL_PESO_P0KEMON,
            $COL_IMAGEN
            FROM $TABLE_FAVORITOS
            ORDER BY $COL_ID_FAVORITO DESC
        """.trimIndent()

        val cursor = db.rawQuery(query_sql, null)

        if(cursor.moveToFirst()){
            do {
                val pokemon = PokemonFavorito(
                    idPokemon = cursor.getLong(0),
                    nombrePokemon = cursor.getString(1) ?: "Pokemon Desconocido",
                    alturaPokemon = cursor.getString(2)?.toIntOrNull() ?: 0,
                    pesoPokemon = cursor.getString(3)?.toIntOrNull() ?: 0,
                    imagenPokemon = cursor.getString(4) ?: ""
                )
                listaFavoritos.add(pokemon)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return listaFavoritos
    }
}
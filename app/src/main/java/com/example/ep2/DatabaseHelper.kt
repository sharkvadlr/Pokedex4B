package com.example.ep2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ep2.model.Pokemon

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
        private const val DATABASE_VERSION = 2

        //Info. de las tablas en la bd
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

        //INFO DE LA TABLA FAVORITOS
        private const val TABLE_FAVORITOS = "pokemonFavorito"
        private const val COL_ID_FAVORITO = "id"
        private const val COL_ID_POKEMON = "idPokemon"
        private const val COL_NOMBRE_POKEMON = "nombrePokemom"
        private const val COL_ALTURA_POKEMON = "alturaPokemon"
        private const val COL_PESO_P0KEMON = "pesoPokemon"
        private const val COL_IMAGEN = "imagenPokemon"
        //CREAR AMBAS TBLAS EN LA BASE DE DATOS
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

    //Metodo onCreate --- Crea la BD
    override fun onCreate(db: SQLiteDatabase?) {
        //Crear la tabla para la aplicacion
        db?.execSQL(crearTablaUsuarios)
        db?.execSQL(crearTablaFavoritos)
    }

    //Metodo onUpgrade --- Actualiza la BD
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        //Verificar la version de la bd para evistar borrar los datos
        if (oldVersion < 2){
            db?.execSQL(crearTablaFavoritos)
        }
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


    // MÉTODO PARA INICIAR SESIÓN
    fun iniciarSesion(email : String, password: String) : Boolean {

        val db = readableDatabase

        //Crear consulta
        val query_sql = """
            
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
            
        """.trimIndent()

        //Crear un curso que permite recorrer todos los registros

        val cursor = db.rawQuery(
            query_sql, arrayOf(email, password)
        )

        //Verificar cuantos resultados  encuentra el cursor
        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return usuarioEncontrado
    }

    //METODO PARA GUARDAR LA INFORMACION DEL API
    fun guardarPokemonFavorito(idPokemon: Long, nombrePokemon: String, alturaPokemon: String, pesoPokemon: String, imagenPokemon: String) : Boolean {

        val db = writableDatabase

        //Crear la variable par pasar los valores de la API
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

        return resultado != -1L
    }

    // MÉTODO PARA ELIMINAR EL POKEMON DE FAVORITOS
    fun eliminarAlbumFavorito(idPokemon: Long) : Boolean{
        val db = writableDatabase

        val filas_afectadas = db.delete(
            TABLE_FAVORITOS,
            "$COL_ID_POKEMON = ?",
            arrayOf(idPokemon.toString())
        )
        return filas_afectadas > 0
    }

    // MÉTODO PARA VERIFICAR SI UN POKEMON ESTÁ REGISTRADO
    fun existePokemonFavorito(idPokemon: Long) : Boolean{
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT $COL_ID_POKEMON FROM $TABLE_FAVORITOS WHERE $COL_ID_POKEMON = ?",
            arrayOf(idPokemon.toString())
        )

        // VERIFICAR SI EXISTE EL REGISTRO
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

}
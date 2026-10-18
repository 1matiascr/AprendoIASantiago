package com.example.aprendoiasantiago.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.aprendoiasantiago.model.Player

class PlayerDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AprendoIAPlayers.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_PLAYERS = "players"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_ALIAS = "alias"
        private const val COLUMN_POINTS = "points"
        private const val COLUMN_QR_COUNT = "qr_count"
        private const val COLUMN_PASS = "pass"
        private const val COLUMN_IS_ADMIN = "is_admin"
        private const val COLUMN_IS_CREATOR = "is_creator"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_PLAYERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_ALIAS TEXT NOT NULL,
                $COLUMN_POINTS INTEGER DEFAULT 0,
                $COLUMN_QR_COUNT INTEGER DEFAULT 0,
                $COLUMN_PASS TEXT NOT NULL,
                $COLUMN_IS_ADMIN INTEGER DEFAULT 0,
                $COLUMN_IS_CREATOR INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Insertar perfiles iniciales obligatorios
        insertInitialPlayers(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYERS")
        onCreate(db)
    }

    private fun insertInitialPlayers(db: SQLiteDatabase) {
        val initialPlayers = listOf(
            Player(1, "Administrador", "Admin", 0, 0, "123", isAdmin = true),
            Player(2, "Creador de Contenido", "GenCont", 0, 0, "123", isCreator = true),
            Player(3, "Ana", "AnitaA", 1250, 12, "1234"),
            Player(4, "Tomás", "Tomi", 340, 5, "1234"),
            Player(5, "Sofía", "Sofi", 960, 9, "1234")
        )

        for (player in initialPlayers) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, player.name)
                put(COLUMN_ALIAS, player.alias)
                put(COLUMN_POINTS, player.points)
                put(COLUMN_QR_COUNT, player.qrCount)
                put(COLUMN_PASS, player.pass)
                put(COLUMN_IS_ADMIN, if (player.isAdmin) 1 else 0)
                put(COLUMN_IS_CREATOR, if (player.isCreator) 1 else 0)
            }
            db.insert(TABLE_PLAYERS, null, values)
        }
    }

    // OBTENER TODOS LOS JUGADORES
    fun getAllPlayers(): List<Player> {
        val playersList = mutableListOf<Player>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PLAYERS", null)

        if (cursor.moveToFirst()) {
            do {
                val player = Player(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    alias = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALIAS)),
                    points = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POINTS)),
                    qrCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QR_COUNT)),
                    pass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASS)),
                    isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1,
                    isCreator = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CREATOR)) == 1
                )
                playersList.add(player)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return playersList
    }

    // INSERTAR UN NUEVO JUGADOR (Registro)
    fun insertPlayer(name: String, alias: String, pass: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_ALIAS, alias)
            put(COLUMN_PASS, pass)
            put(COLUMN_POINTS, 0)
            put(COLUMN_QR_COUNT, 0)
            put(COLUMN_IS_ADMIN, 0)
            put(COLUMN_IS_CREATOR, 0)
        }
        val result = db.insert(TABLE_PLAYERS, null, values)
        return result != -1L
    }

    // ACTUALIZAR PERFIL (Funcionalidad Administrador)
    fun updatePlayer(id: Int, name: String, alias: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_ALIAS, alias)
        }
        val result = db.update(TABLE_PLAYERS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        return result > 0
    }

    // ELIMINAR PERFIL (Funcionalidad Administrador)
    fun deletePlayer(playerId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("players", "id = ?", arrayOf(playerId.toString()))
        return result > 0
    }
    fun getPlayerById(playerId: Int): Player? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM players WHERE id = ?", arrayOf(playerId.toString()))
        var player: Player? = null

        if (cursor.moveToFirst()) {
            player = Player(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                alias = cursor.getString(cursor.getColumnIndexOrThrow("alias")),
                pass = cursor.getString(cursor.getColumnIndexOrThrow("pass")),
                points = cursor.getInt(cursor.getColumnIndexOrThrow("points")),
                qrCount = cursor.getInt(cursor.getColumnIndexOrThrow("qr_count")),
                isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("is_admin")) == 1,
                isCreator = cursor.getInt(cursor.getColumnIndexOrThrow("is_creator")) == 1
            )
        }
        cursor.close()
        return player
    }
}
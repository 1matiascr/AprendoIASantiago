package com.example.aprendoiasantiago.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.aprendoiasantiago.model.Player

class PlayerDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AprendoIAPlayers.db"
        // Incrementamos la versión a 2 para ejecutar la migración del campo avatar
        private const val DATABASE_VERSION = 2
        private const val TABLE_PLAYERS = "players"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_ALIAS = "alias"
        private const val COLUMN_AVATAR = "avatar"
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
                $COLUMN_AVATAR TEXT DEFAULT 'avatar_1',
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
        if (oldVersion < 2) {
            // Agrega la columna avatar sin borrar la base de datos existente
            db.execSQL("ALTER TABLE $TABLE_PLAYERS ADD COLUMN $COLUMN_AVATAR TEXT DEFAULT 'avatar_1'")
        }
    }

    private fun insertInitialPlayers(db: SQLiteDatabase) {
        val initialPlayers = listOf(
            Player(1, "Administrador", "Admin", "avatar_1", 0, 0, "123", isAdmin = true),
            Player(2, "Creador de Contenido", "GenCont", "avatar_2", 0, 0, "123", isCreator = true),
            Player(3, "Ana", "AnitaA", "avatar_3", 1250, 12, "1234"),
            Player(4, "Tomás", "Tomi", "avatar_4", 340, 5, "1234"),
            Player(5, "Sofía", "Sofi", "avatar_1", 960, 9, "1234")
        )

        for (player in initialPlayers) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, player.name)
                put(COLUMN_ALIAS, player.alias)
                put(COLUMN_AVATAR, player.avatar)
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
                    avatar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)) ?: "avatar_1",
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

    // INSERTAR UN NUEVO JUGADOR (Registro con soporte para avatar/URI)
    fun insertPlayer(name: String, alias: String, pass: String, avatar: String = "avatar_1"): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_ALIAS, alias)
            put(COLUMN_PASS, pass)
            put(COLUMN_AVATAR, avatar)
            put(COLUMN_POINTS, 0)
            put(COLUMN_QR_COUNT, 0)
            put(COLUMN_IS_ADMIN, 0)
            put(COLUMN_IS_CREATOR, 0)
        }
        val result = db.insert(TABLE_PLAYERS, null, values)
        return result != -1L
    }

    // ACTUALIZAR PERFIL
    fun updatePlayer(id: Int, name: String, alias: String, avatar: String? = null): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_ALIAS, alias)
            avatar?.let { put(COLUMN_AVATAR, it) }
        }
        val result = db.update(TABLE_PLAYERS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        return result > 0
    }

    // ELIMINAR PERFIL
    fun deletePlayer(playerId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_PLAYERS, "$COLUMN_ID = ?", arrayOf(playerId.toString()))
        return result > 0
    }

    fun getPlayerById(playerId: Int): Player? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PLAYERS WHERE $COLUMN_ID = ?", arrayOf(playerId.toString()))
        var player: Player? = null

        if (cursor.moveToFirst()) {
            player = Player(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                alias = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALIAS)),
                avatar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)) ?: "avatar_1",
                pass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASS)),
                points = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POINTS)),
                qrCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QR_COUNT)),
                isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1,
                isCreator = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CREATOR)) == 1
            )
        }
        cursor.close()
        return player
    }

    // Sumar puntos e incrementar el contador de QR en SQLite
    fun addPointsToPlayer(playerId: Int, pointsToAdd: Int): Boolean {
        val player = getPlayerById(playerId) ?: return false
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_POINTS, player.points + pointsToAdd)
            put(COLUMN_QR_COUNT, player.qrCount + 1)
        }
        val result = db.update(TABLE_PLAYERS, values, "$COLUMN_ID = ?", arrayOf(playerId.toString()))
        return result > 0
    }
}
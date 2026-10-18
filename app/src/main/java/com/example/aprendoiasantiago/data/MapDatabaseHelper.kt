package com.example.aprendoiasantiago.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.aprendoiasantiago.model.QrPoint

class MapDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AprendoIAMap.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_POINTS = "qr_points"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_POINTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                latitude REAL DEFAULT 0.0,
                longitude REAL DEFAULT 0.0,
                is_completed INTEGER DEFAULT 0,
                distance_meters INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Insertar puntos iniciales de Santiago
        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POINTS")
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val initialPoints = listOf(
            QrPoint(1, "Plaza de Armas", "Hito histórico y cultural de Santiago.", -33.4372, -70.6506, false, 250),
            QrPoint(2, "Cerro Santa Lucía", "Mirador icónico de la ciudad.", -33.4408, -70.6431, true, 800),
            QrPoint(3, "Parque Forestal", "Área verde junto al Museo de Bellas Artes.", -33.4358, -70.6410, false, 1200)
        )

        for (point in initialPoints) {
            val values = ContentValues().apply {
                put("name", point.name)
                put("description", point.description)
                put("latitude", point.latitude)
                put("longitude", point.longitude)
                put("is_completed", if (point.isCompleted) 1 else 0)
                put("distance_meters", point.distanceMeters)
            }
            db.insert(TABLE_POINTS, null, values)
        }
    }

    // Nombre principal compatible con MapScreen
    fun getAllPoints(): List<QrPoint> {
        val pointsList = mutableListOf<QrPoint>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POINTS", null)

        if (cursor.moveToFirst()) {
            do {
                val point = QrPoint(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                    longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1,
                    distanceMeters = cursor.getInt(cursor.getColumnIndexOrThrow("distance_meters"))
                )
                pointsList.add(point)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return pointsList
    }

    // Alias por si lo llamas como getAllQrPoints en otros ViewModels
    fun getAllQrPoints(): List<QrPoint> = getAllPoints()

    // Marcar punto como completado al escanear QR
    fun markPointAsCompleted(pointId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("is_completed", 1)
        }
        val result = db.update(TABLE_POINTS, values, "id = ?", arrayOf(pointId.toString()))
        return result > 0
    }
}
package com.example.aprendoiasantiago.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.aprendoiasantiago.model.QrPoint

class MapDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "map_points.db"
        // Incrementamos la versión a 3 para forzar recreación e inserción de las imágenes locales
        private const val DATABASE_VERSION = 3

        const val TABLE_POINTS = "qr_points"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_IS_COMPLETED = "is_completed"
        const val COLUMN_DISTANCE = "distance_meters"
        const val COLUMN_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_POINTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_LATITUDE REAL NOT NULL,
                $COLUMN_LONGITUDE REAL NOT NULL,
                $COLUMN_IS_COMPLETED INTEGER DEFAULT 0,
                $COLUMN_DISTANCE INTEGER DEFAULT 10,
                $COLUMN_IMAGE_URL TEXT DEFAULT ''
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POINTS")
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // Imágenes iniciales asociadas a los recursos pegados en res/drawable/
        val initialPoints = listOf(
            QrPoint(
                id = 1,
                name = "Plaza de Armas",
                description = "Corazón histórico de Santiago",
                latitude = -33.4372,
                longitude = -70.6506,
                isCompleted = false,
                distanceMeters = 250,
                imageUrl = "plaza_armas" // Nombre del archivo sin extensión en res/drawable/
            ),
            QrPoint(
                id = 2,
                name = "Palacio de La Moneda",
                description = "Sede del gobierno nacional",
                latitude = -33.4429,
                longitude = -70.6539,
                isCompleted = false,
                distanceMeters = 500,
                imageUrl = "palacio_moneda" // Nombre del archivo sin extensión en res/drawable/
            ),
            QrPoint(
                id = 3,
                name = "Cerro Santa Lucía",
                description = "Mirador y parque emblemático",
                latitude = -33.4408,
                longitude = -70.6431,
                isCompleted = false,
                distanceMeters = 850,
                imageUrl = "cerro_santa_lucia" // Nombre del archivo sin extensión en res/drawable/
            )
        )

        for (point in initialPoints) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, point.name)
                put(COLUMN_DESCRIPTION, point.description)
                put(COLUMN_LATITUDE, point.latitude)
                put(COLUMN_LONGITUDE, point.longitude)
                put(COLUMN_IS_COMPLETED, if (point.isCompleted) 1 else 0)
                put(COLUMN_DISTANCE, point.distanceMeters)
                put(COLUMN_IMAGE_URL, point.imageUrl)
            }
            db.insert(TABLE_POINTS, null, values)
        }
    }

    fun getAllPoints(): List<QrPoint> {
        val pointsList = mutableListOf<QrPoint>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POINTS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))
                val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
                val distance = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISTANCE))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)) ?: ""

                pointsList.add(QrPoint(id, name, description, latitude, longitude, isCompleted, distance, imageUrl))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return pointsList
    }

    fun insertPoint(point: QrPoint): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, point.name)
            put(COLUMN_DESCRIPTION, point.description)
            put(COLUMN_LATITUDE, point.latitude)
            put(COLUMN_LONGITUDE, point.longitude)
            put(COLUMN_IS_COMPLETED, if (point.isCompleted) 1 else 0)
            put(COLUMN_DISTANCE, point.distanceMeters)
            put(COLUMN_IMAGE_URL, point.imageUrl)
        }
        val result = db.insert(TABLE_POINTS, null, values)
        return result != -1L
    }

    fun updatePoint(point: QrPoint): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, point.name)
            put(COLUMN_DESCRIPTION, point.description)
            put(COLUMN_LATITUDE, point.latitude)
            put(COLUMN_LONGITUDE, point.longitude)
            put(COLUMN_IS_COMPLETED, if (point.isCompleted) 1 else 0)
            put(COLUMN_DISTANCE, point.distanceMeters)
            put(COLUMN_IMAGE_URL, point.imageUrl)
        }
        val result = db.update(TABLE_POINTS, values, "$COLUMN_ID = ?", arrayOf(point.id.toString()))
        return result > 0
    }

    fun deletePoint(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_POINTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        return result > 0
    }

    fun markPointAsCompleted(id: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, 1)
        }
        val result = db.update(TABLE_POINTS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        return result > 0
    }
}
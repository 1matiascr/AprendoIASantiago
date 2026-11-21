package com.example.aprendoiasantiago.model

data class QrPoint(
    val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val isCompleted: Boolean = false,
    val distanceMeters: Int = 10,
    val imageUrl: String = "" // <--- CAMPO NUEVO PARA LA IMAGEN
)
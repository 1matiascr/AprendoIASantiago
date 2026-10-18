package com.example.aprendoiasantiago.model

data class QrPoint(
    val id: Int = 0,
    val name: String,
    val description: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isCompleted: Boolean = false,
    val distanceMeters: Int = 0
)
package com.example.aprendoiasantiago.model

/**
 * Modelo de datos que representa a un usuario/jugador dentro de la aplicación.
 */
data class Player(
    val id: Int,
    val name: String,
    val alias: String,
    val avatar: String,
    val points: Int = 0,
    val qrCount: Int = 0,
    val pass: String,
    val isAdmin: Boolean = false,
    val isCreator: Boolean = false
)
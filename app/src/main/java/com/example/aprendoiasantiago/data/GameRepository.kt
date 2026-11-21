package com.example.aprendoiasantiago.data

import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint

class GameRepository(
    private val playerDb: PlayerDatabaseHelper,
    private val mapDb: MapDatabaseHelper
) {

    // ==========================================
    // 1. GESTIÓN DE JUGADORES Y PUNTOS
    // ==========================================

    fun obtenerTodosLosJugadores(): List<Player> {
        return playerDb.getAllPlayers()
    }

    fun obtenerJugadorPorId(playerId: Int): Player? {
        return playerDb.getPlayerById(playerId)
    }

    fun sumarPuntosJugador(playerId: Int, puntos: Int): Player? {
        if (puntos <= 0) return playerDb.getPlayerById(playerId)
        playerDb.addPointsToPlayer(playerId, puntos)
        return playerDb.getPlayerById(playerId)
    }

    // ==========================================
    // 2. GESTIÓN DE LOCACIONES QR (CRUD ADMIN)
    // ==========================================

    fun obtenerTodasLasLocaciones(): List<QrPoint> {
        return mapDb.getAllPoints()
    }

    fun obtenerLocacionesCompletadas(): List<QrPoint> {
        return mapDb.getAllPoints().filter { it.isCompleted }
    }

    fun obtenerLocacionesPendientes(): List<QrPoint> {
        return mapDb.getAllPoints().filter { !it.isCompleted }
    }

    fun crearLocacion(point: QrPoint): Boolean {
        return mapDb.insertPoint(point)
    }

    fun actualizarLocacion(point: QrPoint): Boolean {
        return mapDb.updatePoint(point)
    }

    fun eliminarLocacion(pointId: Int): Boolean {
        return mapDb.deletePoint(pointId)
    }

    // ==========================================
    // 3. FLUJOS DE EXPLORACIÓN Y RECOMPENSAS
    // ==========================================

    /**
     * Marca una locación como completada y suma los puntos base (+10 pts) al jugador.
     */
    fun registrarEscaneoQr(playerId: Int, pointId: Int, puntosBase: Int = 10): Player? {
        mapDb.markPointAsCompleted(pointId)
        return sumarPuntosJugador(playerId, puntosBase)
    }

    /**
     * Otorga la bonificación extra por visualizar una cápsula educativa del Creador.
     */
    fun registrarVideoCompletado(playerId: Int, puntosBonus: Int): Player? {
        return sumarPuntosJugador(playerId, puntosBonus)
    }

    /**
     * Otorga la bonificación extra (+100 pts) por capturar una selfie turística en el hito.
     */
    fun registrarSelfieTuristica(playerId: Int, puntosSelfie: Int = 100): Player? {
        return sumarPuntosJugador(playerId, puntosSelfie)
    }
}
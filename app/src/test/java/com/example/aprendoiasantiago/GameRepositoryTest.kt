package com.example.aprendoiasantiago

import com.example.aprendoiasantiago.data.GameRepository
import com.example.aprendoiasantiago.data.MapDatabaseHelper
import com.example.aprendoiasantiago.data.PlayerDatabaseHelper
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameRepositoryTest {

    private lateinit var playerDb: PlayerDatabaseHelper
    private lateinit var mapDb: MapDatabaseHelper
    private lateinit var repository: GameRepository

    @Before
    fun setUp() {
        playerDb = mockk(relaxed = true)
        mapDb = mockk(relaxed = true)
        repository = GameRepository(playerDb, mapDb)
    }

    @Test
    fun `obtenerLocacionesCompletadas y Pendientes filtran correctamente`() {
        val listaMixta = listOf(
            QrPoint(1, "Plaza de Armas", "Desc", -33.43, -70.65, isCompleted = true, distanceMeters = 100, imageUrl = ""),
            QrPoint(2, "La Moneda", "Desc", -33.44, -70.65, isCompleted = false, distanceMeters = 200, imageUrl = ""),
            QrPoint(3, "Santa Lucía", "Desc", -33.44, -70.64, isCompleted = true, distanceMeters = 300, imageUrl = "")
        )
        every { mapDb.getAllPoints() } returns listaMixta

        val completadas = repository.obtenerLocacionesCompletadas()
        val pendientes = repository.obtenerLocacionesPendientes()

        assertEquals(2, completadas.size)
        assertEquals(1, pendientes.size)
        assertEquals("La Moneda", pendientes.first().name)
    }

    @Test
    fun `registrarSelfieTuristica suma 100 puntos al jugador`() {
        val jugadorEsperado = Player(
            id = 1,
            name = "Ana",
            alias = "AnitaA",
            avatar = "avatar_1",
            pass = "1234",
            points = 200
        )
        every { playerDb.getPlayerById(1) } returns jugadorEsperado

        val resultado = repository.registrarSelfieTuristica(playerId = 1)

        assertEquals(200, resultado?.points)
        verify(exactly = 1) { playerDb.addPointsToPlayer(1, 100) }
    }

    @Test
    fun `sumarPuntosJugador ignora valores menores o iguales a cero`() {
        val jugadorActual = Player(
            id = 1,
            name = "Ana",
            alias = "AnitaA",
            avatar = "avatar_1",
            pass = "1234",
            points = 50
        )
        every { playerDb.getPlayerById(1) } returns jugadorActual

        val resultado = repository.sumarPuntosJugador(playerId = 1, puntos = 0)

        assertEquals(50, resultado?.points)
        verify(exactly = 0) { playerDb.addPointsToPlayer(any(), any()) }
    }

    @Test
    fun `operaciones CRUD de locaciones delegan correctamente en mapDb`() {
        val punto = QrPoint(4, "Parque Forestal", "Desc", -33.43, -70.64, false, 150, "")
        every { mapDb.insertPoint(punto) } returns true
        every { mapDb.updatePoint(punto) } returns true
        every { mapDb.deletePoint(4) } returns true

        assertTrue(repository.crearLocacion(punto))
        assertTrue(repository.actualizarLocacion(punto))
        assertTrue(repository.eliminarLocacion(4))
    }
}
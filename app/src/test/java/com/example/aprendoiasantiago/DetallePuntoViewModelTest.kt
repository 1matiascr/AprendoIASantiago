package com.example.aprendoiasantiago.ui.viewmodels

import com.example.aprendoiasantiago.data.GameRepository
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DetallePuntoViewModelTest {

    private lateinit var repository: GameRepository
    private lateinit var viewModel: DetallePuntoViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = DetallePuntoViewModel(repository)
    }

    @Test
    fun `inicializarPunto carga datos por defecto y estado inicial del punto`() {
        val punto = QrPoint(
            id = 1,
            name = "Plaza de Armas",
            description = "Corazón histórico",
            latitude = -33.4372,
            longitude = -70.6506,
            isCompleted = false,
            distanceMeters = 250,
            imageUrl = "plaza_armas"
        )

        viewModel.inicializarPunto(punto)

        val state = viewModel.uiState.value
        assertFalse(state.qrCompletado)
        assertFalse(state.videoVisto)
        assertNotNull(state.videoData)
        assertEquals("Introducción a la IA en Plaza de Armas", state.videoData?.titulo)
        assertEquals(50, state.videoData?.puntosBonus)
        assertNull(state.mensajeEstado)
    }

    @Test
    fun `procesarEscaneoQr con usuario valido y JSON actualiza puntos y capsula educativa`() {
        val jugadorActualizado = Player(
            id = 1,
            name = "Matías",
            alias = "MatiIA",
            avatar = "avatar_1",
            pass = "1234",
            points = 110
        )
        val jsonQr = """
            {
                "titulo": "IA en la Arquitectura",
                "descripcion": "Descubre cómo la IA analiza edificios históricos",
                "url": "https://youtu.be/ejemplo",
                "puntosBonus": 75
            }
        """.trimIndent()

        every {
            repository.registrarEscaneoQr(playerId = 1, pointId = 2, puntosBase = 10)
        } returns jugadorActualizado

        val resultado = viewModel.procesarEscaneoQr(
            playerId = 1,
            pointId = 2,
            contenidoQr = jsonQr
        )

        val state = viewModel.uiState.value
        assertEquals(110, resultado?.points)
        assertTrue(state.qrCompletado)
        assertEquals("IA en la Arquitectura", state.videoData?.titulo)
        assertEquals(75, state.videoData?.puntosBonus)
        assertEquals("¡Código QR escaneado con éxito! (+10 PTS)", state.mensajeEstado)

        verify(exactly = 1) {
            repository.registrarEscaneoQr(playerId = 1, pointId = 2, puntosBase = 10)
        }
    }

    @Test
    fun `procesarEscaneoQr con texto plano mantiene video por defecto y suma 10 puntos`() {
        val jugadorActualizado = Player(
            id = 1,
            name = "Matías",
            alias = "MatiIA",
            avatar = "avatar_1",
            pass = "1234",
            points = 60
        )
        viewModel.inicializarPunto(null)

        every {
            repository.sumarPuntosJugador(playerId = 1, puntos = 10)
        } returns jugadorActualizado

        val resultado = viewModel.procesarEscaneoQr(
            playerId = 1,
            pointId = null,
            contenidoQr = "QR_SIMPLE_SIN_JSON"
        )

        val state = viewModel.uiState.value
        assertEquals(60, resultado?.points)
        assertTrue(state.qrCompletado)
        assertEquals("Introducción a la IA en Santiago", state.videoData?.titulo)
        verify(exactly = 1) { repository.sumarPuntosJugador(1, 10) }
    }

    @Test
    fun `procesarEscaneoQr sin sesion activa retorna null y muestra mensaje de advertencia`() {
        val resultado = viewModel.procesarEscaneoQr(
            playerId = null,
            pointId = 1,
            contenidoQr = "{}"
        )

        val state = viewModel.uiState.value
        assertNull(resultado)
        assertFalse(state.qrCompletado)
        assertEquals("Debes iniciar sesión con un perfil para ganar puntos.", state.mensajeEstado)
    }

    @Test
    fun `registrarVisualizacionVideo suma puntos bonus solo la primera vez`() {
        viewModel.inicializarPunto(null)
        val jugadorConBonus = Player(
            id = 1,
            name = "Alejandro",
            alias = "AleDev",
            avatar = "avatar_2",
            pass = "1234",
            points = 150
        )

        every {
            repository.registrarVideoCompletado(playerId = 1, puntosBonus = 50)
        } returns jugadorConBonus

        // Primera reproducción: debe sumar los 50 pts
        val primerIntento = viewModel.registrarVisualizacionVideo(playerId = 1)
        assertEquals(150, primerIntento?.points)
        assertTrue(viewModel.uiState.value.videoVisto)
        assertEquals("¡Ganaste +50 PTS por ver el video educativo!", viewModel.uiState.value.mensajeEstado)

        // Segunda reproducción: debe bloquear puntos duplicados
        val segundoIntento = viewModel.registrarVisualizacionVideo(playerId = 1)
        assertNull(segundoIntento)

        verify(exactly = 1) {
            repository.registrarVideoCompletado(playerId = 1, puntosBonus = 50)
        }
    }

    @Test
    fun `registrarErrorEscaneo actualiza el mensaje de estado correctamente`() {
        viewModel.registrarErrorEscaneo("Escaneo cancelado.")

        assertEquals("Escaneo cancelado.", viewModel.uiState.value.mensajeEstado)
    }

    @Test
    fun `parsearJsonQr retorna null cuando el formato JSON es invalido`() {
        val resultado = viewModel.parsearJsonQr("{json_corrupto_sin_cierre")

        assertNull(resultado)
    }
}
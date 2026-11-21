package com.example.aprendoiasantiago.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.aprendoiasantiago.data.GameRepository
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import com.example.aprendoiasantiago.ui.screens.VideoQrData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONException
import org.json.JSONObject

// Estado completo y reactivo para la pantalla DetallePuntoScreen
data class DetallePuntoUiState(
    val qrCompletado: Boolean = false,
    val videoVisto: Boolean = false,
    val videoData: VideoQrData? = null,
    val mensajeEstado: String? = null,
    val jugadorActualizado: Player? = null
)

class DetallePuntoViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetallePuntoUiState())
    val uiState: StateFlow<DetallePuntoUiState> = _uiState.asStateFlow()

    /**
     * Inicializa el estado de la pantalla según el punto seleccionado.
     */
    fun inicializarPunto(point: QrPoint?) {
        val videoPorDefecto = VideoQrData(
            titulo = "Introducción a la IA en ${point?.name ?: "Santiago"}",
            descripcion = "Cápsula oficial creada por Triskeledu: Aprende conceptos fundamentales de IA.",
            url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            puntosBonus = 50
        )

        _uiState.update {
            it.copy(
                qrCompletado = point?.isCompleted ?: false,
                videoVisto = false,
                videoData = videoPorDefecto,
                mensajeEstado = null,
                jugadorActualizado = null
            )
        }
    }

    /**
     * Procesa el contenido leído desde el escáner QR, actualiza SQLite y suma los +10 PTS base.
     */
    fun procesarEscaneoQr(
        playerId: Int?,
        pointId: Int?,
        contenidoQr: String
    ): Player? {
        if (playerId == null) {
            _uiState.update { it.copy(mensajeEstado = "Debes iniciar sesión con un perfil para ganar puntos.") }
            return null
        }

        val videoParseado = parsearJsonQr(contenidoQr)
        val jugadorActualizado = if (pointId != null) {
            repository.registrarEscaneoQr(playerId = playerId, pointId = pointId, puntosBase = 10)
        } else {
            repository.sumarPuntosJugador(playerId = playerId, puntos = 10)
        }

        _uiState.update { currentState ->
            currentState.copy(
                qrCompletado = true,
                videoData = videoParseado ?: currentState.videoData,
                mensajeEstado = "¡Código QR escaneado con éxito! (+10 PTS)",
                jugadorActualizado = jugadorActualizado
            )
        }

        return jugadorActualizado
    }

    /**
     * Otorga la bonificación extra al visualizar la cápsula educativa por primera vez.
     */
    fun registrarVisualizacionVideo(playerId: Int?): Player? {
        val currentState = _uiState.value
        val video = currentState.videoData ?: return null

        // Evita otorgar puntos duplicados si el usuario vuelve a reproducir el mismo video
        if (currentState.videoVisto || playerId == null) {
            return null
        }

        val jugadorActualizado = repository.registrarVideoCompletado(
            playerId = playerId,
            puntosBonus = video.puntosBonus
        )

        _uiState.update { state ->
            state.copy(
                videoVisto = true,
                mensajeEstado = "¡Ganaste +${video.puntosBonus} PTS por ver el video educativo!",
                jugadorActualizado = jugadorActualizado
            )
        }

        return jugadorActualizado
    }

    /**
     * Registra un mensaje de error o cancelación proveniente del escáner.
     */
    fun registrarErrorEscaneo(error: String) {
        _uiState.update { it.copy(mensajeEstado = error) }
    }

    /**
     * Deserializador seguro de JSON incluido en el código QR.
     */
    fun parsearJsonQr(texto: String): VideoQrData? {
        return try {
            val json = JSONObject(texto)
            VideoQrData(
                titulo = json.optString("titulo", "Cápsula Educativa"),
                descripcion = json.optString("descripcion", "Aprende sobre este punto con IA"),
                url = json.optString("url", ""),
                puntosBonus = json.optInt("puntosBonus", 50)
            )
        } catch (e: JSONException) {
            null
        }
    }
}
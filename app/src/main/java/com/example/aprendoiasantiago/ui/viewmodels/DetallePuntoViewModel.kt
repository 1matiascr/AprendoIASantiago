package com.example.aprendoiasantiago.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Estado para el Detalle del Punto
data class DetallePuntoUiState(
    val qrCompletado: Boolean = false
)

class DetallePuntoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetallePuntoUiState())
    val uiState: StateFlow<DetallePuntoUiState> = _uiState.asStateFlow()

    fun alternarEstadoEscaneo() {
        _uiState.update { currentState ->
            currentState.copy(qrCompletado = !currentState.qrCompletado)
        }
    }
}
package com.example.aprendoiasantiago.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Estado de la interfaz del Dashboard
data class DashboardUiState(
    val nombreUsuario: String = "Ana",
    val puntosTotales: Int = 1250,
    val qrEncontrados: Int = 12,
    val videosVistos: Int = 8,
    val lugaresVisitados: Int = 3,
    val totalPuntosMapa: Int = 30,
    val progresoPorcentaje: Float = 0.40f,
    val desafioActualTitulo: String = "Visita Plaza de Armas",
    val desafioActualDescripcion: String = "Escanea el QR y gana 100 puntos extra con una selfie"
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
}
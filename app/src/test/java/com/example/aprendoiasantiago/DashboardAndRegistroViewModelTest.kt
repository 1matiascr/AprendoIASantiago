package com.example.aprendoiasantiago.ui.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardAndRegistroViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // =========================================================
    // 1. PRUEBAS DE DashboardViewModel
    // =========================================================

    @Test
    fun `DashboardViewModel inicia con el estado por defecto esperado`() {
        val dashboardViewModel = DashboardViewModel()
        val state = dashboardViewModel.uiState.value

        assertEquals("Ana", state.nombreUsuario)
        assertEquals(1250, state.puntosTotales)
        assertEquals(12, state.qrEncontrados)
        assertEquals(8, state.videosVistos)
        assertEquals(3, state.lugaresVisitados)
        assertEquals(30, state.totalPuntosMapa)
        assertEquals(0.40f, state.progresoPorcentaje, 0.01f)
        assertEquals("Visita Plaza de Armas", state.desafioActualTitulo)
        assertTrue(state.desafioActualDescripcion.contains("100 puntos extra"))
    }

    // =========================================================
    // 2. PRUEBAS DE RegistroViewModel (VALIDACIONES Y FLUJO)
    // =========================================================

    @Test
    fun `RegistroViewModel actualiza campos y valida longitud minima de password`() {
        val registroViewModel = RegistroViewModel()

        // Formulario vacío -> inválido
        assertFalse(registroViewModel.uiState.value.isFormularioValido)

        registroViewModel.onNombreChanged("Matías Currin")
        registroViewModel.onAliasChanged("MatiIA")
        registroViewModel.onPasswordChanged("123") // Menos de 4 caracteres -> inválido
        assertFalse(registroViewModel.uiState.value.isFormularioValido)

        // 4 o más caracteres -> válido
        registroViewModel.onPasswordChanged("1234")
        assertTrue(registroViewModel.uiState.value.isFormularioValido)

        // Visibilidad de contraseña y selección de avatar
        assertFalse(registroViewModel.uiState.value.isPasswordVisible)
        registroViewModel.togglePasswordVisibility()
        assertTrue(registroViewModel.uiState.value.isPasswordVisible)

        registroViewModel.onAvatarSelected(3)
        assertEquals(3, registroViewModel.uiState.value.selectedAvatarId)
    }

    @Test
    fun `crearJugador muestra error cuando el formulario es invalido`() {
        val registroViewModel = RegistroViewModel()

        registroViewModel.crearJugador()

        val state = registroViewModel.uiState.value
        assertNotNull(state.errorMensaje)
        assertFalse(state.registroExitoso)
        assertFalse(state.isCargando)
    }

    @Test
    fun `crearJugador con datos validos completa el registro exitoso y permite resetear estado`() = runTest {
        val registroViewModel = RegistroViewModel()

        registroViewModel.onNombreChanged("Alejandro Rodríguez")
        registroViewModel.onAliasChanged("AleDev")
        registroViewModel.onPasswordChanged("clave2026")
        registroViewModel.onAvatarSelected(2)

        registroViewModel.crearJugador()
        advanceUntilIdle() // Ejecuta el delay(1200) de viewModelScope al instante

        val statePostRegistro = registroViewModel.uiState.value
        assertFalse(statePostRegistro.isCargando)
        assertTrue(statePostRegistro.registroExitoso)
        assertNull(statePostRegistro.errorMensaje)

        registroViewModel.resetRegistroExitoso()
        assertFalse(registroViewModel.uiState.value.registroExitoso)
    }
}
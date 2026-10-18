package com.example.aprendoiasantiago.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la interfaz para la pantalla de registro
data class RegistroUiState(
    val nombre: String = "",
    val alias: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val selectedAvatarId: Int = 1,
    val isCargando: Boolean = false,
    val registroExitoso: Boolean = false,
    val errorMensaje: String? = null
) {
    val isFormularioValido: Boolean
        get() = nombre.isNotBlank() && alias.isNotBlank() && password.length >= 4
}

/**
 * ViewModel encargado de gestionar la lógica de negocio, validaciones y estado de la pantalla de Registro.
 */
class RegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombreChanged(nuevoNombre: String) {
        _uiState.update { currentState ->
            currentState.copy(
                nombre = nuevoNombre,
                errorMensaje = null
            )
        }
    }

    fun onAliasChanged(nuevoAlias: String) {
        _uiState.update { currentState ->
            currentState.copy(
                alias = nuevoAlias,
                errorMensaje = null
            )
        }
    }

    fun onPasswordChanged(nuevaPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = nuevaPassword,
                errorMensaje = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { currentState ->
            currentState.copy(
                isPasswordVisible = !currentState.isPasswordVisible
            )
        }
    }

    fun onAvatarSelected(avatarId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedAvatarId = avatarId
            )
        }
    }

    fun crearJugador() {
        val currentState = _uiState.value
        if (!currentState.isFormularioValido) {
            _uiState.update {
                it.copy(errorMensaje = "Por favor, completa todos los campos requeridos correctamente (mínimo 4 caracteres en la contraseña).")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCargando = true, errorMensaje = null) }
            try {
                // Simulación de guardado local / persistencia en SQLite
                delay(1200)
                _uiState.update {
                    it.copy(
                        isCargando = false,
                        registroExitoso = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCargando = false,
                        errorMensaje = e.localizedMessage ?: "Ocurrió un error inesperado al registrar."
                    )
                }
            }
        }
    }

    fun resetRegistroExitoso() {
        _uiState.update { it.copy(registroExitoso = false) }
    }
}
package com.example.aprendoiasantiago

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import com.example.aprendoiasantiago.ui.screens.*
import com.example.aprendoiasantiago.ui.theme.AprendoIASantiagoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AprendoIASantiagoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf("welcome") }
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var selectedPoint by remember { mutableStateOf<QrPoint?>(null) }

    // Variables para capturar coordenadas al presionar sostenido en el mapa (Admin)
    var initialLat by remember { mutableStateOf<Double?>(null) }
    var initialLng by remember { mutableStateOf<Double?>(null) }

    // Control del botón físico o gesto de volver atrás en Android
    BackHandler(enabled = currentScreen != "welcome") {
        when (currentScreen) {
            "registro" -> currentScreen = "player_selection"
            "player_selection" -> currentScreen = "welcome"
            "dashboard" -> currentScreen = "player_selection"
            "editar_perfil" -> currentScreen = "dashboard"
            "gestionar_locaciones" -> currentScreen = "dashboard"
            "subir_video" -> currentScreen = "dashboard"
            "coleccion" -> currentScreen = "dashboard"
            "mapa" -> currentScreen = "dashboard"
            "detalle_punto" -> currentScreen = "mapa"
            else -> currentScreen = "welcome"
        }
    }

    when (currentScreen) {
        "welcome" -> {
            WelcomeScreen(
                onComenzarClick = { currentScreen = "player_selection" }
            )
        }

        "registro" -> {
            RegistroJugadorScreen(
                onNavigateBack = { currentScreen = "player_selection" },
                onRegistroExitoso = { currentScreen = "player_selection" }
            )
        }

        "player_selection" -> {
            PlayerSelectionScreen(
                onPlayerSelected = { player: Player ->
                    selectedPlayer = player
                    currentScreen = "dashboard"
                },
                onNavigateToRegister = { currentScreen = "registro" }
            )
        }

        "dashboard" -> {
            DashboardScreen(
                player = selectedPlayer,
                onNavigateToMapa = { currentScreen = "mapa" },
                onNavigateToEscaner = { currentScreen = "detalle_punto" },
                onNavigateToColeccion = { currentScreen = "coleccion" },
                onNavigateToPerfil = { currentScreen = "player_selection" },
                onNavigateToEditarPerfil = { currentScreen = "editar_perfil" },
                onNavigateToGestionLocaciones = {
                    initialLat = null
                    initialLng = null
                    currentScreen = "gestionar_locaciones"
                },
                onNavigateToSubirVideo = { currentScreen = "subir_video" },
                onNavigateBack = { currentScreen = "player_selection" }
            )
        }

        "gestionar_locaciones" -> {
            GestionLocacionesScreen(
                initialLat = initialLat,
                initialLng = initialLng,
                onNavigateBack = { currentScreen = "dashboard" }
            )
        }

        "subir_video" -> {
            SubirVideoScreen(
                onNavigateBack = { currentScreen = "dashboard" }
            )
        }

        "editar_perfil" -> {
            EditarPerfilScreen(
                player = selectedPlayer,
                onNavigateBack = { currentScreen = "dashboard" },
                onPerfilActualizado = { currentScreen = "dashboard" }
            )
        }

        "coleccion" -> {
            ColeccionScreen(
                activePlayer = selectedPlayer,
                onPlayerUpdated = { updatedPlayer ->
                    selectedPlayer = updatedPlayer
                },
                onNavigateBack = { currentScreen = "dashboard" }
            )
        }

        "mapa" -> {
            MapScreen(
                activePlayer = selectedPlayer,
                onNavigateToPointDetail = { point ->
                    selectedPoint = point
                    currentScreen = "detalle_punto"
                },
                onNavigateToGestionLocaciones = {
                    initialLat = null
                    initialLng = null
                    currentScreen = "gestionar_locaciones"
                },
                onNavigateToCrearPuntoEnCoords = { lat, lng ->
                    initialLat = lat
                    initialLng = lng
                    currentScreen = "gestionar_locaciones"
                },
                onNavigateBack = { currentScreen = "dashboard" }
            )
        }

        "detalle_punto" -> {
            DetallePuntoScreen(
                point = selectedPoint,
                activePlayer = selectedPlayer,
                onNavigateBack = { currentScreen = "mapa" },
                onPointCompleted = { updatedPlayer ->
                    selectedPlayer = updatedPlayer
                    currentScreen = "dashboard"
                }
            )
        }
    }
}
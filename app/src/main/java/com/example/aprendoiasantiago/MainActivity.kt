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

    // Control seguro del botón físico o gesto de volver atrás en Android
    BackHandler(enabled = currentScreen != "welcome") {
        when (currentScreen) {
            "registro" -> currentScreen = "player_selection"
            "player_selection" -> currentScreen = "welcome"
            "dashboard" -> currentScreen = "player_selection"
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
                onNavigateToColeccion = {
                    Toast.makeText(context, "Sección Colección en desarrollo", Toast.LENGTH_SHORT).show()
                },
                onNavigateToPerfil = { currentScreen = "player_selection" },
                onNavigateBack = { currentScreen = "player_selection" }
            )
        }

        "mapa" -> {
            MapScreen(
                onNavigateToPointDetail = { point ->
                    selectedPoint = point
                    currentScreen = "detalle_punto"
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
                    selectedPlayer = updatedPlayer // Sincroniza el usuario con sus nuevos puntos
                    currentScreen = "dashboard"    // Vuelve al panel con los puntos actualizados
                }
            )
        }
    }
}
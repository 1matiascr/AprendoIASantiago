package com.example.aprendoiasantiago.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendoiasantiago.R
import com.example.aprendoiasantiago.data.MapDatabaseHelper
import com.example.aprendoiasantiago.data.PlayerDatabaseHelper
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONException
import org.json.JSONObject

// Modelo de datos para las cápsulas aprendidas vía QR
data class VideoQrData(
    val titulo: String,
    val descripcion: String,
    val url: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePuntoScreen(
    point: QrPoint?,
    activePlayer: Player?,
    onNavigateBack: () -> Unit,
    onPointCompleted: (Player) -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val nombre = point?.name ?: "Punto Turístico"
    val descripcionPunto = point?.description ?: "Escanea el código QR en el lugar para desbloquear el contenido educativo y ganar experiencia."
    val distancia = point?.distanceMeters ?: 0
    var estaCompletado by remember { mutableStateOf(point?.isCompleted ?: false) }

    // Estado del QR leído
    var videoLeido by remember { mutableStateOf<VideoQrData?>(null) }
    var mensajeEscaneo by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de Santiago
        Image(
            painter = painterResource(id = R.drawable.fondo_santiago),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay oscuro para contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del Punto", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta de Información Principal
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = nombre,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                color = if (estaCompletado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (estaCompletado) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (estaCompletado) Color(0xFF2E7D32) else Color(0xFFE65100),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (estaCompletado) "Completado" else "Pendiente",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (estaCompletado) Color(0xFF2E7D32) else Color(0xFFE65100)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "A $distancia metros de tu ubicación",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = descripcionPunto,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Tarjeta de Recompensa
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1).copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Recompensa por Exploración",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                            Text(
                                text = "+100 puntos de experiencia y cápsula educativa",
                                fontSize = 12.sp,
                                color = Color(0xFF795548)
                            )
                        }
                    }
                }

                // Despliegue de Resultados del Escaneo (Si leyó JSON/Video)
                videoLeido?.let { video ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = video.titulo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = video.descripcion,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )

                            if (video.url.startsWith("http://") || video.url.startsWith("https://")) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "▶ Ver Cápsula Educativa en YouTube",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable { uriHandler.openUri(video.url) }
                                )
                            }
                        }
                    }
                }

                mensajeEscaneo?.let { msg ->
                    Text(
                        text = msg,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Botón Principal: Escanear con Google MLKit Barcode Scanner
                Button(
                    onClick = {
                        if (activePlayer != null) {
                            escanearCodigoQr(
                                context = context,
                                alLeer = { contenidoQr ->
                                    val videoParsed = parsearJsonQr(contenidoQr)
                                    val playerDb = PlayerDatabaseHelper(context)
                                    val mapDb = MapDatabaseHelper(context)

                                    // Persistir cambios en SQLite
                                    playerDb.addPointsToPlayer(activePlayer.id, 100)
                                    point?.let { mapDb.markPointAsCompleted(it.id) }
                                    estaCompletado = true

                                    if (videoParsed != null) {
                                        videoLeido = videoParsed
                                        mensajeEscaneo = "¡QR leído con éxito! +100 PTS ganados."
                                    } else {
                                        mensajeEscaneo = "QR procesado (+100 PTS). Contenido: $contenidoQr"
                                    }

                                    // Sincronizar jugador actualizado
                                    val playerActualizado = playerDb.getPlayerById(activePlayer.id)
                                    if (playerActualizado != null) {
                                        onPointCompleted(playerActualizado)
                                    }
                                },
                                alFallar = { error ->
                                    mensajeEscaneo = error
                                }
                            )
                        } else {
                            Toast.makeText(context, "Debes iniciar sesión con un perfil", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (estaCompletado) "Volver a Escanear QR" else "Escanear Código QR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Función auxiliar para invocar el escáner nativo de Google Play Services
private fun escanearCodigoQr(
    context: Context,
    alLeer: (String) -> Unit,
    alFallar: (String) -> Unit
) {
    val opciones = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    GmsBarcodeScanning.getClient(context, opciones)
        .startScan()
        .addOnSuccessListener { codigo ->
            alLeer(codigo.rawValue ?: "")
        }
        .addOnCanceledListener {
            alFallar("Escaneo cancelado.")
        }
        .addOnFailureListener { e ->
            alFallar("Error al escanear: ${e.localizedMessage}")
        }
}

// Deserializador seguro de JSON
private fun parsearJsonQr(texto: String): VideoQrData? {
    return try {
        val json = JSONObject(texto)
        VideoQrData(
            titulo = json.optString("titulo", "Cápsula Educativa"),
            descripcion = json.optString("descripcion", "Aprende sobre este punto con IA"),
            url = json.optString("url", "")
        )
    } catch (e: JSONException) {
        null
    }
}
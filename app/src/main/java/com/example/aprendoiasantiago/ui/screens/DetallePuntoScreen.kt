package com.example.aprendoiasantiago.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aprendoiasantiago.R
import com.example.aprendoiasantiago.data.GameRepository
import com.example.aprendoiasantiago.data.MapDatabaseHelper
import com.example.aprendoiasantiago.data.PlayerDatabaseHelper
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import com.example.aprendoiasantiago.ui.viewmodels.DetallePuntoViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

// Modelo de datos para las cápsulas aprendidas vía QR o asignadas por el Creador
data class VideoQrData(
    val titulo: String,
    val descripcion: String,
    val url: String,
    val puntosBonus: Int = 50
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

    // Instancia del ViewModel conectado al Repository
    val viewModel = remember {
        val repository = GameRepository(
            playerDb = PlayerDatabaseHelper(context),
            mapDb = MapDatabaseHelper(context)
        )
        DetallePuntoViewModel(repository)
    }

    val uiState by viewModel.uiState.collectAsState()

    // Inicializa el estado cuando cambia el punto seleccionado
    LaunchedEffect(point) {
        viewModel.inicializarPunto(point)
    }

    val nombre = point?.name ?: "Punto Turístico"
    val descripcionPunto = point?.description
        ?: "Escanea el código QR en el lugar para desbloquear el contenido educativo y ganar experiencia."
    val distancia = point?.distanceMeters ?: 0

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_santiago),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
                    title = { Text("6. Detalle del Punto", fontWeight = FontWeight.Bold, color = Color.White) },
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
                // 1. Tarjeta de Información Principal con soporte para Imágenes Locales y Web
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        if (!point?.imageUrl.isNullOrBlank()) {
                            LocationImageView(
                                imageUrl = point!!.imageUrl,
                                contentDescription = point.name
                            )
                        }

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
                                    color = if (uiState.qrCompletado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.qrCompletado) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = if (uiState.qrCompletado) Color(0xFF2E7D32) else Color(0xFFE65100),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (uiState.qrCompletado) "Completado" else "Pendiente",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (uiState.qrCompletado) Color(0xFF2E7D32) else Color(0xFFE65100)
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
                }

                // 2. Tarjeta de Recompensas
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1).copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFA000),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Recompensas Disponibles",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5D4037)
                                )
                                Text(
                                    text = "• Escaneo QR: +10 pts\n• Cápsula de Video: +${uiState.videoData?.puntosBonus ?: 50} pts extra\n• Selfie Turística: +100 pts extra",
                                    fontSize = 12.sp,
                                    color = Color(0xFF795548)
                                )
                            }
                        }
                    }
                }

                // 3. Tarjeta de Cápsula Educativa (Creador de Contenido)
                uiState.videoData?.let { video ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "+${video.puntosBonus} PTS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = video.descripcion,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Botón de reproducción delegado al ViewModel
                            Button(
                                onClick = {
                                    if (video.url.isNotEmpty()) {
                                        uriHandler.openUri(video.url)
                                    }
                                    val updatedPlayer = viewModel.registrarVisualizacionVideo(activePlayer?.id)
                                    if (updatedPlayer != null) {
                                        Toast.makeText(
                                            context,
                                            "¡Ganaste +${video.puntosBonus} PTS por ver el video educativo!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        onPointCompleted(updatedPlayer)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.videoVisto) "▶ Ver Cápsula de Nuevo (Puntos Recibidos)" else "▶ Reproducir Video (+${video.puntosBonus} Puntos Extra)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                uiState.mensajeEstado?.let { msg ->
                    Text(
                        text = msg,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 4. Botón de Escaneo QR delegado al ViewModel
                Button(
                    onClick = {
                        if (activePlayer != null) {
                            escanearCodigoQr(
                                context = context,
                                alLeer = { contenidoQr ->
                                    val playerActualizado = viewModel.procesarEscaneoQr(
                                        playerId = activePlayer.id,
                                        pointId = point?.id,
                                        contenidoQr = contenidoQr
                                    )
                                    if (playerActualizado != null) {
                                        onPointCompleted(playerActualizado)
                                    }
                                },
                                alFallar = { error ->
                                    viewModel.registrarErrorEscaneo(error)
                                }
                            )
                        } else {
                            Toast.makeText(context, "Debes iniciar sesión con un perfil", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.qrCompletado) "Volver a Escanear QR (+10 PTS)" else "Escanear Código QR (+10 PTS)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Componente para resolver si la imagen proviene de res/drawable/ o de una URL web
@Composable
fun LocationImageView(imageUrl: String, contentDescription: String?) {
    val context = LocalContext.current

    val drawableResId = remember(imageUrl) {
        if (imageUrl.isNotBlank() && !imageUrl.startsWith("http")) {
            context.resources.getIdentifier(imageUrl, "drawable", context.packageName)
        } else {
            0
        }
    }

    if (drawableResId != 0) {
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )
    } else if (imageUrl.startsWith("http")) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

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
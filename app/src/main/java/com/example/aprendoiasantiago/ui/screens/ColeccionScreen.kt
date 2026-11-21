package com.example.aprendoiasantiago.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aprendoiasantiago.R
import com.example.aprendoiasantiago.data.MapDatabaseHelper
import com.example.aprendoiasantiago.data.PlayerDatabaseHelper
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColeccionScreen(
    activePlayer: Player? = null,
    onPlayerUpdated: (Player) -> Unit = {},
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val mapDb = remember { MapDatabaseHelper(context) }
    val playerDb = remember { PlayerDatabaseHelper(context) }

    var puntosCompletados by remember { mutableStateOf(listOf<QrPoint>()) }
    // Almacena las selfies capturadas por ID de locación
    var selfiesCapturadas by remember { mutableStateOf(mapOf<Int, Bitmap>()) }
    var puntoSeleccionadoParaSelfie by remember { mutableStateOf<QrPoint?>(null) }

    LaunchedEffect(Unit) {
        puntosCompletados = mapDb.getAllPoints().filter { it.isCompleted }
    }

    // Lanzador de cámara nativa sin necesidad de configurar FileProvider complejo
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        val punto = puntoSeleccionadoParaSelfie
        if (bitmap != null && punto != null) {
            val yaTeniaSelfie = selfiesCapturadas.containsKey(punto.id)
            selfiesCapturadas = selfiesCapturadas + (punto.id to bitmap)

            if (!yaTeniaSelfie && activePlayer != null) {
                playerDb.addPointsToPlayer(activePlayer.id, 100)
                val actualizado = playerDb.getPlayerById(activePlayer.id)
                if (actualizado != null) {
                    onPlayerUpdated(actualizado)
                }
                Toast.makeText(
                    context,
                    "¡Selfie en ${punto.name} guardada! +100 PTS ganados",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, "¡Foto turística actualizada!", Toast.LENGTH_SHORT).show()
            }
        }
        puntoSeleccionadoParaSelfie = null
    }

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
                    title = { Text("Mi Colección y Selfies", fontWeight = FontWeight.Bold, color = Color.White) },
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
                    .padding(16.dp)
            ) {
                Text(
                    text = "Puntos descubiertos: ${puntosCompletados.size} • Selfies: ${selfiesCapturadas.size}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (puntosCompletados.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no has descubierto puntos QR.\n¡Explora el mapa y escanea lugares en Santiago para desbloquear tu galería y tomar selfies!",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(puntosCompletados) { point ->
                            val selfieBitmap = selfiesCapturadas[point.id]

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // 1. Muestra la selfie capturada si existe, o la imagen de la locación
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (selfieBitmap != null) {
                                            Image(
                                                bitmap = selfieBitmap.asImageBitmap(),
                                                contentDescription = "Selfie en ${point.name}",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else if (point.imageUrl.isNotBlank()) {
                                            ColeccionLocationImage(
                                                imageUrl = point.imageUrl,
                                                contentDescription = point.name
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                tint = Color(0xFFFFA000),
                                                modifier = Modifier.size(44.dp)
                                            )
                                        }
                                    }

                                    // 2. Información y botón de Selfie Turística
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = point.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Button(
                                            onClick = {
                                                puntoSeleccionadoParaSelfie = point
                                                cameraLauncher.launch(null)
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(34.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selfieBitmap != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(
                                                imageVector = if (selfieBitmap != null) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (selfieBitmap != null) "Selfie Lista" else "Selfie +100",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColeccionLocationImage(imageUrl: String, contentDescription: String?) {
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
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else if (imageUrl.startsWith("http")) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
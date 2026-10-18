package com.example.aprendoiasantiago.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendoiasantiago.data.MapDatabaseHelper
import com.example.aprendoiasantiago.model.QrPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToPointDetail: (QrPoint) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var qrPoints by remember { mutableStateOf<List<QrPoint>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar puntos desde SQLite sin bloquear el hilo principal
    LaunchedEffect(Unit) {
        try {
            val dbHelper = MapDatabaseHelper(context)
            val points = dbHelper.getAllPoints()
            qrPoints = if (points.isNotEmpty()) {
                points
            } else {
                listOf(
                    QrPoint(
                        id = 1,
                        name = "Plaza de Armas",
                        description = "Corazón histórico de Santiago",
                        latitude = -33.4372,
                        longitude = -70.6506,
                        isCompleted = false,
                        distanceMeters = 250
                    ),
                    QrPoint(
                        id = 2,
                        name = "Palacio de La Moneda",
                        description = "Sede del gobierno nacional",
                        latitude = -33.4429,
                        longitude = -70.6539,
                        isCompleted = false,
                        distanceMeters = 500
                    ),
                    QrPoint(
                        id = 3,
                        name = "Cerro Santa Lucía",
                        description = "Mirador y parque emblemático",
                        latitude = -33.4408,
                        longitude = -70.6431,
                        isCompleted = true,
                        distanceMeters = 850
                    )
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error cargando mapa: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("5. Mapa Interactivo de Santiago", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al Panel"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Visualización Simulada del Mapa
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vista Satelital Santiago Centro",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${qrPoints.size} Puntos QR detectados cerca de ti",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Puntos de Interés Cercanos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(qrPoints) { point ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToPointDetail(point) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (point.isCompleted) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (point.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = point.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "${point.distanceMeters} m • ${point.description}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
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
package com.example.aprendoiasantiago.ui.screens

import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.aprendoiasantiago.data.MapDatabaseHelper
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    activePlayer: Player?,
    onNavigateToPointDetail: (QrPoint) -> Unit,
    onNavigateToGestionLocaciones: () -> Unit,
    onNavigateToCrearPuntoEnCoords: (Double, Double) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var qrPoints by remember { mutableStateOf<List<QrPoint>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var esModoSatelital by remember { mutableStateOf(true) }
    var puntoSeleccionadoEnMapa by remember { mutableStateOf<QrPoint?>(null) }

    val isAdmin = activePlayer?.isAdmin == true

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

        try {
            val dbHelper = MapDatabaseHelper(context)
            val points = dbHelper.getAllPoints()
            qrPoints = if (points.isNotEmpty()) {
                points
            } else {
                listOf(
                    QrPoint(1, "Plaza de Armas", "Corazón histórico de Santiago", -33.4372, -70.6506, false, 250),
                    QrPoint(2, "Palacio de La Moneda", "Sede del gobierno nacional", -33.4429, -70.6539, false, 500),
                    QrPoint(3, "Cerro Santa Lucía", "Mirador y parque emblemático", -33.4408, -70.6431, true, 850)
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error cargando mapa: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        } finally {
            isLoading = false
        }
    }

    // Capa Satelital HD (Esri World Imagery)
    val esriSatTileSource = remember {
        object : OnlineTileSourceBase(
            "EsriWorldImagery",
            0, 19, 256, ".jpg",
            arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val zoom = MapTileIndex.getZoom(pMapTileIndex)
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                return "$baseUrl$zoom/$y/$x"
            }
        }
    }

    // Capa Estándar de Calles HD
    val esriStreetTileSource = remember {
        object : OnlineTileSourceBase(
            "EsriWorldStreetMap",
            0, 19, 256, ".jpg",
            arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val zoom = MapTileIndex.getZoom(pMapTileIndex)
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                return "$baseUrl$zoom/$y/$x"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("5. Mapa de Santiago", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al Panel",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = onNavigateToGestionLocaciones) {
                            Icon(
                                imageVector = Icons.Default.EditLocation,
                                contentDescription = "Gestionar Locaciones",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF111827))
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onNavigateToGestionLocaciones,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nueva Locación"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF111827))
                .padding(16.dp)
        ) {
            // Selector de Vista Satelital vs Estándar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = esModoSatelital,
                    onClick = { esModoSatelital = true },
                    label = { Text("Vista Satelital HD") },
                    leadingIcon = { Icon(Icons.Default.Satellite, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = !esModoSatelital,
                    onClick = { esModoSatelital = false },
                    label = { Text("Vista Estándar") },
                    leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visor de Mapa Nativo
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setMultiTouchControls(true)
                            controller.setZoom(15.5)
                            controller.setCenter(GeoPoint(-33.4372, -70.6506))
                        }
                    },
                    update = { mapView ->
                        if (esModoSatelital) {
                            mapView.setTileSource(esriSatTileSource)
                        } else {
                            mapView.setTileSource(esriStreetTileSource)
                        }

                        mapView.overlays.clear()

                        // Si es Administrador, agregamos el detector de clic largo sobre el mapa
                        if (isAdmin) {
                            val mapEventsReceiver = object : MapEventsReceiver {
                                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false

                                override fun longPressHelper(p: GeoPoint?): Boolean {
                                    p?.let {
                                        Toast.makeText(
                                            context,
                                            "Coordenadas capturadas: ${it.latitude}, ${it.longitude}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onNavigateToCrearPuntoEnCoords(it.latitude, it.longitude)
                                    }
                                    return true
                                }
                            }
                            mapView.overlays.add(MapEventsOverlay(mapEventsReceiver))
                        }

                        qrPoints.forEach { point ->
                            val marker = Marker(mapView).apply {
                                position = GeoPoint(point.latitude, point.longitude)
                                title = point.name
                                snippet = point.description
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                setOnMarkerClickListener { m, _ ->
                                    puntoSeleccionadoEnMapa = point
                                    m.showInfoWindow()
                                    true
                                }
                            }
                            mapView.overlays.add(marker)
                        }
                        mapView.invalidate()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Tarjeta flotante interactiva al tocar un marcador
            puntoSeleccionadoEnMapa?.let { selected ->
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPointDetail(selected) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Punto: ${selected.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = selected.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Ir al punto",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Puntos de Interés Cercanos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
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
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
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

                                if (isAdmin) {
                                    IconButton(onClick = onNavigateToGestionLocaciones) {
                                        Icon(
                                            imageVector = Icons.Default.EditLocation,
                                            contentDescription = "Editar Locación",
                                            tint = MaterialTheme.colorScheme.error
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
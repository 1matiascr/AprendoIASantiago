package com.example.aprendoiasantiago.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendoiasantiago.data.MapDatabaseHelper
import com.example.aprendoiasantiago.model.QrPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionLocacionesScreen(
    initialLat: Double? = null,
    initialLng: Double? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { MapDatabaseHelper(context) }

    var locacionesList by remember { mutableStateOf(listOf<QrPoint>()) }
    var selectedPointId by remember { mutableStateOf<Int?>(null) }

    // Campos del Formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf(initialLat?.toString() ?: "-33.4372") }
    var longitud by remember { mutableStateOf(initialLng?.toString() ?: "-70.6506") }
    var puntosBase by remember { mutableStateOf("10") }
    var imageUrl by remember { mutableStateOf("") } // <--- Campo para URL de la imagen
    var videoAsociado by remember { mutableStateOf("Introducción a la IA") }

    // Control de Menú desplegable para videos
    var dropdownExpanded by remember { mutableStateOf(false) }
    val videosDisponibles = listOf(
        "Introducción a la IA (Triskeledu)",
        "Fundamentos de IA en Santiago",
        "Robótica e IA en el Parque Forestal",
        "IA y Medio Ambiente en Bellas Artes",
        "Sin video asociado"
    )

    var errorMensaje by remember { mutableStateOf<String?>(null) }

    fun recargarLocaciones() {
        locacionesList = dbHelper.getAllPoints()
    }

    LaunchedEffect(Unit) {
        recargarLocaciones()
    }

    fun limpiarFormulario() {
        selectedPointId = null
        nombre = ""
        descripcion = ""
        latitud = "-33.4372"
        longitud = "-70.6506"
        puntosBase = "10"
        imageUrl = ""
        videoAsociado = videosDisponibles.first()
        errorMensaje = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Locaciones (Admin)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
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
            // Tarjeta Formulario de Administración
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddLocation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedPointId == null) "Agregar Nueva Locación QR" else "Editar Locación #${selectedPointId}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 1. Campo Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            errorMensaje = null
                        },
                        label = { Text("Nombre del lugar / Hito") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    // 2. Campo Descripción
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            descripcion = it
                            errorMensaje = null
                        },
                        label = { Text("Descripción o Reseña Turística") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    // 3. Coordenadas (Latitud y Longitud)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = latitud,
                            onValueChange = { latitud = it },
                            label = { Text("Latitud") },
                            leadingIcon = { Icon(Icons.Default.PinDrop, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )

                        OutlinedTextField(
                            value = longitud,
                            onValueChange = { longitud = it },
                            label = { Text("Longitud") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                        )
                    }

                    // 4. Puntos Base
                    OutlinedTextField(
                        value = puntosBase,
                        onValueChange = { puntosBase = it },
                        label = { Text("Puntos otorgados al escanear") },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                    )

                    // 5. URL o Ruta de la Imagen Representativa del Lugar
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("URL de la foto del lugar (http/https)") },
                        leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next)
                    )

                    // 6. Vincular Cápsula de Video Educativo
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = videoAsociado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Vincular Cápsula de Video (Creador)") },
                            leadingIcon = { Icon(Icons.Default.VideoLibrary, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            videosDisponibles.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        videoAsociado = item
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    errorMensaje?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (selectedPointId != null) {
                            OutlinedButton(
                                onClick = { limpiarFormulario() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                        }

                        Button(
                            onClick = {
                                if (nombre.isBlank() || descripcion.isBlank()) {
                                    errorMensaje = "Ingresa nombre y descripción para la locación."
                                    return@Button
                                }
                                val lat = latitud.toDoubleOrNull() ?: -33.4372
                                val lng = longitud.toDoubleOrNull() ?: -70.6506
                                val pts = puntosBase.toIntOrNull() ?: 10

                                if (selectedPointId == null) {
                                    // Insertar nueva locación en SQLite
                                    val newPoint = QrPoint(
                                        id = (locacionesList.maxOfOrNull { it.id } ?: 0) + 1,
                                        name = nombre.trim(),
                                        description = descripcion.trim(),
                                        latitude = lat,
                                        longitude = lng,
                                        isCompleted = false,
                                        distanceMeters = pts,
                                        imageUrl = imageUrl.trim()
                                    )
                                    dbHelper.insertPoint(newPoint)
                                    Toast.makeText(context, "¡Locación creada con éxito!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Actualizar locación existente
                                    val updatedPoint = QrPoint(
                                        id = selectedPointId!!,
                                        name = nombre.trim(),
                                        description = descripcion.trim(),
                                        latitude = lat,
                                        longitude = lng,
                                        isCompleted = false,
                                        distanceMeters = pts,
                                        imageUrl = imageUrl.trim()
                                    )
                                    dbHelper.updatePoint(updatedPoint)
                                    Toast.makeText(context, "Locación actualizada", Toast.LENGTH_SHORT).show()
                                }

                                recargarLocaciones()
                                limpiarFormulario()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (selectedPointId == null) "Guardar" else "Actualizar")
                        }
                    }
                }
            }

            // Lista de Locaciones Existentes
            Text(
                text = "Locaciones Registradas (${locacionesList.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                locacionesList.forEach { point ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = point.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = point.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Coords: ${point.latitude}, ${point.longitude}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row {
                                IconButton(onClick = {
                                    selectedPointId = point.id
                                    nombre = point.name
                                    descripcion = point.description
                                    latitud = point.latitude.toString()
                                    longitud = point.longitude.toString()
                                    puntosBase = point.distanceMeters.toString()
                                    imageUrl = point.imageUrl
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = {
                                    dbHelper.deletePoint(point.id)
                                    Toast.makeText(context, "Locación eliminada", Toast.LENGTH_SHORT).show()
                                    recargarLocaciones()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
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
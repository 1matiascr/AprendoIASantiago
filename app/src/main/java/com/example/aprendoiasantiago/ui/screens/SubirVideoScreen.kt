package com.example.aprendoiasantiago.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo simple para representar la cápsula de video cargada por el creador
data class VideoCapsula(
    val id: Int,
    val titulo: String,
    val tema: String,
    val urlOrPath: String,
    val puntosBonus: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubirVideoScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Estado local para simular/gestionar las cápsulas creadas por GenCont
    var videosList by remember {
        mutableStateOf(
            listOf(
                VideoCapsula(1, "Introducción a la IA", "Fundamentos de IA", "https://triskeledu.com/videos/intro_ia.mp4", 50),
                VideoCapsula(2, "Redes Neuronales en Santiago", "Redes Neuronales", "https://triskeledu.com/videos/redes_neuronales.mp4", 100)
            )
        )
    }

    // Campos del Formulario
    var titulo by remember { mutableStateOf("") }
    var temaSeleccionado by remember { mutableStateOf("Fundamentos de IA") }
    var urlOrPath by remember { mutableStateOf("") }
    var puntosBonus by remember { mutableStateOf("50") }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val temasIaDisponibles = listOf(
        "Fundamentos de IA",
        "Redes Neuronales",
        "Visión por Computador",
        "Procesamiento de Lenguaje Natural (NLP)",
        "Robótica e IA",
        "Ética e Inteligencia Artificial"
    )

    fun limpiarFormulario() {
        titulo = ""
        temaSeleccionado = temasIaDisponibles.first()
        urlOrPath = ""
        puntosBonus = "50"
        errorMensaje = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subir Cápsulas (Creador)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
            // Tarjeta Formulario de Carga de Videos
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
                            imageVector = Icons.Default.VideoCall,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nueva Cápsula Educativa de IA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Nota de estructura estandarizada Triskeledu
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📌 El video debe iniciar con la frase: \"Soy [Tu Nombre] y te enseñaré a [Tema]\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    // 1. Título del Video
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = {
                            titulo = it
                            errorMensaje = null
                        },
                        label = { Text("Título de la cápsula (ej. Shorts de IA)") },
                        leadingIcon = { Icon(Icons.Default.Movie, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    // 2. Tema de IA (Desplegable)
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = temaSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tema de Inteligencia Artificial") },
                            leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            temasIaDisponibles.forEach { tema ->
                                DropdownMenuItem(
                                    text = { Text(tema) },
                                    onClick = {
                                        temaSeleccionado = tema
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 3. URL o Ruta del Archivo de Video
                    OutlinedTextField(
                        value = urlOrPath,
                        onValueChange = {
                            urlOrPath = it
                            errorMensaje = null
                        },
                        label = { Text("URL de YouTube / Drive / Ruta del archivo") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    // 4. Puntos de Bonificación
                    OutlinedTextField(
                        value = puntosBonus,
                        onValueChange = { puntosBonus = it },
                        label = { Text("Puntos extra por ver el video completo") },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                    )

                    errorMensaje?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            if (titulo.isBlank() || urlOrPath.isBlank()) {
                                errorMensaje = "Ingresa un título y enlace válido para la cápsula."
                                return@Button
                            }
                            val pts = puntosBonus.toIntOrNull() ?: 50

                            val nuevaCapsula = VideoCapsula(
                                id = (videosList.maxOfOrNull { it.id } ?: 0) + 1,
                                titulo = titulo.trim(),
                                tema = temaSeleccionado,
                                urlOrPath = urlOrPath.trim(),
                                puntosBonus = pts
                            )

                            videosList = videosList + nuevaCapsula
                            Toast.makeText(context, "¡Cápsula registrada con éxito!", Toast.LENGTH_SHORT).show()
                            limpiarFormulario()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publicar Cápsula Educativa", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Lista de Cápsulas Creadas
            Text(
                text = "Cápsulas Disponibles (${videosList.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                videosList.forEach { video ->
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
                                    text = video.titulo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Tema: ${video.tema} • +${video.puntosBonus} pts",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = video.urlOrPath,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }

                            IconButton(onClick = {
                                videosList = videosList.filter { it.id != video.id }
                                Toast.makeText(context, "Cápsula eliminada", Toast.LENGTH_SHORT).show()
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
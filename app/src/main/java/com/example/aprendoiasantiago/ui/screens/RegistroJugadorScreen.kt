package com.example.aprendoiasantiago.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.aprendoiasantiago.data.PlayerDatabaseHelper
import com.example.aprendoiasantiago.ui.viewmodels.RegistroViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroJugadorScreen(
    onNavigateBack: () -> Unit,
    onRegistroExitoso: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dbHelper = remember { PlayerDatabaseHelper(context) }

    // Estado local para validar campos tras interactuar
    var intentoRegistro by remember { mutableStateOf(false) }

    // Evaluaciones de error por campo
    val nombreError = intentoRegistro && uiState.nombre.trim().isBlank()
    val aliasError = intentoRegistro && uiState.alias.trim().isBlank()
    val passwordError = intentoRegistro && uiState.password.length < 4

    // Estado para guardar la ruta o la foto capturada
    var customUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 1. Launcher para Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            customUri = uri
            capturedBitmap = null
            viewModel.onAvatarSelected(-1)
        }
    }

    // 2. Launcher para Cámara (Captura en tiempo real)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            customUri = null
            viewModel.onAvatarSelected(-1)
        }
    }

    // Función auxiliar para guardar el Bitmap de la cámara como archivo temporal
    fun saveBitmapToTempFile(bitmap: Bitmap): String? {
        return try {
            val file = File(context.cacheDir, "camera_avatar_${System.currentTimeMillis()}.jpg")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Manejo de éxito del registro
    LaunchedEffect(uiState.registroExitoso) {
        if (uiState.registroExitoso) {
            val avatarPath = when {
                capturedBitmap != null -> saveBitmapToTempFile(capturedBitmap!!) ?: "avatar_1"
                customUri != null -> customUri.toString()
                else -> "avatar_${uiState.selectedAvatarId}"
            }

            dbHelper.insertPlayer(
                name = uiState.nombre.trim(),
                alias = uiState.alias.trim(),
                pass = uiState.password,
                avatar = avatarPath
            )
            Toast.makeText(context, "¡Jugador creado con éxito!", Toast.LENGTH_SHORT).show()
            viewModel.resetRegistroExitoso()
            onRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("2. Registro de Jugador", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear nuevo perfil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Regístrate para acumular puntos explorando Santiago",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de Avatar (Predeterminados + Galería + Cámara)
            Text(
                text = "Selecciona tu Avatar o Foto",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatares predeterminados (1 al 3)
                listOf(1, 2, 3).forEach { avatarId ->
                    val isSelected = uiState.selectedAvatarId == avatarId && customUri == null && capturedBitmap == null
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                customUri = null
                                capturedBitmap = null
                                viewModel.onAvatarSelected(avatarId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (avatarId % 2 == 0) Icons.Default.Face else Icons.Default.Star,
                            contentDescription = "Avatar $avatarId",
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Opción Galería
                val isCustomSelected = customUri != null
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCustomSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (isCustomSelected) 3.dp else 0.dp,
                            color = if (isCustomSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (customUri != null) {
                        AsyncImage(
                            model = customUri,
                            contentDescription = "Foto elegida",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Galería",
                            tint = Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Opción Cámara
                val isCameraSelected = capturedBitmap != null
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCameraSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (isCameraSelected) 3.dp else 0.dp,
                            color = if (isCameraSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { cameraLauncher.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    if (capturedBitmap != null) {
                        AsyncImage(
                            model = capturedBitmap,
                            contentDescription = "Foto Cámara",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tomar Foto",
                            tint = Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nombre
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = { Text("Nombre Completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                isError = nombreError,
                supportingText = {
                    if (nombreError) {
                        Text(
                            text = "El nombre es obligatorio",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Alias
            OutlinedTextField(
                value = uiState.alias,
                onValueChange = { viewModel.onAliasChanged(it) },
                label = { Text("Alias / Nombre de usuario") },
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                singleLine = true,
                isError = aliasError,
                supportingText = {
                    if (aliasError) {
                        Text(
                            text = "El alias o usuario es obligatorio",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Cambiar visibilidad"
                        )
                    }
                },
                singleLine = true,
                isError = passwordError,
                supportingText = {
                    if (passwordError) {
                        Text(
                            text = if (uiState.password.isEmpty()) "La contraseña es obligatoria" else "La contraseña debe tener al menos 4 caracteres",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                },
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            // Mensaje de Error General de SQLite o backend
            uiState.errorMensaje?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            Button(
                onClick = {
                    intentoRegistro = true
                    if (!nombreError && !aliasError && !passwordError) {
                        viewModel.crearJugador()
                    }
                },
                enabled = !uiState.isCargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Registrar Jugador", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
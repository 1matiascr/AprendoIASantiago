package com.example.aprendoiasantiago.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
import coil.compose.AsyncImage
import com.example.aprendoiasantiago.data.PlayerDatabaseHelper
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    player: Player?,
    onNavigateBack: () -> Unit,
    onPerfilActualizado: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { PlayerDatabaseHelper(context) }

    var nombre by remember { mutableStateOf(player?.name ?: "") }
    var alias by remember { mutableStateOf(player?.alias ?: "") }
    var password by remember { mutableStateOf(player?.pass ?: "") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var avatarActual by remember { mutableStateOf(player?.avatar ?: "avatar_1") }

    var customUri by remember { mutableStateOf<Uri?>(null) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var selectedDefaultAvatarId by remember { mutableStateOf<Int?>(null) }

    // Launcher para seleccionar directamente de la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            customUri = uri
            selectedDefaultAvatarId = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
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
                text = "Actualiza tu información",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Modifica tu nombre, alias, avatar o contraseña",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Vista previa del Avatar
            Box(
                modifier = Modifier.size(90.dp),
                contentAlignment = Alignment.Center
            ) {
                if (customUri != null) {
                    AsyncImage(
                        model = customUri,
                        contentDescription = "Nuevo Avatar Galería",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    UserAvatar(
                        avatarPath = avatarActual,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Avatares
            Text(
                text = "Cambiar Avatar",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(1, 2, 3, 4).forEach { avatarId ->
                    val isSelected = selectedDefaultAvatarId == avatarId ||
                            (customUri == null && avatarActual == "avatar_$avatarId")

                    Box(
                        modifier = Modifier
                            .size(52.dp)
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
                                selectedDefaultAvatarId = avatarId
                                avatarActual = "avatar_$avatarId"
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

                // Opción Abrir Galería
                val isCustomSelected = customUri != null
                Box(
                    modifier = Modifier
                        .size(52.dp)
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
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Elegir de Galería",
                        tint = if (isCustomSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorMensaje = null
                },
                label = { Text("Nombre Completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Alias
            OutlinedTextField(
                value = alias,
                onValueChange = {
                    alias = it
                    errorMensaje = null
                },
                label = { Text("Alias / Nombre de usuario") },
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMensaje = null
                },
                label = { Text("Contraseña (mín. 4 caracteres)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Cambiar visibilidad"
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            errorMensaje?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Guardar
            Button(
                onClick = {
                    if (nombre.isBlank() || alias.isBlank()) {
                        errorMensaje = "El nombre y el alias no pueden estar vacíos."
                        return@Button
                    }
                    if (password.length < 4) {
                        errorMensaje = "La contraseña debe tener al menos 4 caracteres."
                        return@Button
                    }

                    player?.id?.let { playerId ->
                        // Guarda la URI directamente como String o el id por defecto
                        val finalAvatarPath = customUri?.toString() ?: avatarActual

                        val actualizado = dbHelper.updatePlayer(
                            id = playerId,
                            name = nombre.trim(),
                            alias = alias.trim(),
                            avatar = finalAvatarPath
                        )

                        if (actualizado) {
                            Toast.makeText(context, "¡Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show()
                            onPerfilActualizado()
                        } else {
                            errorMensaje = "Error al actualizar en la base de datos."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
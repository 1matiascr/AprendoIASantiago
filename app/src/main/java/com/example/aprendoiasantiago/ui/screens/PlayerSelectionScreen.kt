package com.example.aprendoiasantiago.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aprendoiasantiago.R
import com.example.aprendoiasantiago.data.PlayerDatabaseHelper
import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSelectionScreen(
    onPlayerSelected: (Player) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val dbHelper = remember { PlayerDatabaseHelper(context) }

    var playersList by remember { mutableStateOf(listOf<Player>()) }
    var playerToAuthenticate by remember { mutableStateOf<Player?>(null) }
    var playerToDelete by remember { mutableStateOf<Player?>(null) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    // Identificador si la sesión activa actual es Administrador (para permitir borrar otros perfiles)
    var currentActiveAdmin by remember { mutableStateOf(false) }

    // Cuentas predefinidas en duro requeridas por el caso de estudio
    val adminAccount = remember {
        Player(
            id = -1,
            name = "Administrador",
            alias = "Admin",
            pass = "123",
            avatar = "avatar_1",
            points = 0,
            qrCount = 0,
            isAdmin = true,
            isCreator = false
        )
    }

    val creatorAccount = remember {
        Player(
            id = -2,
            name = "Contenido",
            alias = "GenCont",
            pass = "123",
            avatar = "avatar_2",
            points = 0,
            qrCount = 0,
            isAdmin = false,
            isCreator = true
        )
    }

    // Recargar perfiles mezclando las cuentas fijas con las registradas en SQLite
    fun reloadPlayers() {
        val sqlitePlayers = dbHelper.getAllPlayers().filter { player ->
            player.alias != "Admin" && player.alias != "GenCont"
        }
        playersList = listOf(adminAccount, creatorAccount) + sqlitePlayers
    }

    LaunchedEffect(Unit) {
        reloadPlayers()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_santiago),
            contentDescription = "Fondo Santiago",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("3. Selección de Jugador", fontWeight = FontWeight.Bold, color = Color.White) },
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
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToRegister,
                    icon = { Icon(Icons.Default.Add, contentDescription = "Nuevo Perfil") },
                    text = { Text("Nuevo Jugador", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Selecciona un perfil o ingresa con credenciales especiales",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(playersList, key = { "${it.id}_${it.alias}" }) { player ->
                        PlayerCardItem(
                            player = player,
                            canDelete = currentActiveAdmin && !player.isAdmin && !player.isCreator,
                            onClick = {
                                passwordInput = ""
                                passwordError = false
                                playerToAuthenticate = player
                            },
                            onDeleteClick = {
                                playerToDelete = player
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo emergente para ingresar clave y validar Roles (Admin, GenCont o Cliente)
    playerToAuthenticate?.let { player ->
        AlertDialog(
            onDismissRequest = { playerToAuthenticate = null },
            icon = {
                Icon(
                    imageVector = when {
                        player.isAdmin -> Icons.Default.AdminPanelSettings
                        player.isCreator -> Icons.Default.VideoLibrary
                        else -> Icons.Default.Lock
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "Ingresar como ${player.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Contraseña para @${player.alias}:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            passwordError = false
                        },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        isError = passwordError,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (passwordInput == player.pass) {
                                    if (player.isAdmin) currentActiveAdmin = true
                                    val targetPlayer = player
                                    playerToAuthenticate = null
                                    onPlayerSelected(targetPlayer)
                                } else {
                                    passwordError = true
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Contraseña incorrecta",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (passwordInput == player.pass) {
                            if (player.isAdmin) currentActiveAdmin = true
                            val targetPlayer = player
                            playerToAuthenticate = null
                            onPlayerSelected(targetPlayer)
                        } else {
                            passwordError = true
                        }
                    }
                ) {
                    Text("Ingresar")
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToAuthenticate = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para confirmar la eliminación de un perfil cliente (Administrador)
    playerToDelete?.let { player ->
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar Perfil de Jugador", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Deseas eliminar permanentemente a \"${player.name}\" (@${player.alias})? Se borrarán sus puntos y progresos acumulados.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val deleted = dbHelper.deletePlayer(player.id)
                        if (deleted) {
                            Toast.makeText(context, "Perfil eliminado", Toast.LENGTH_SHORT).show()
                            reloadPlayers()
                        } else {
                            Toast.makeText(context, "Error al eliminar perfil", Toast.LENGTH_SHORT).show()
                        }
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PlayerCardItem(
    player: Player,
    canDelete: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val cardColor = when {
        player.isAdmin -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
        player.isCreator -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
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
                UserAvatar(
                    avatarPath = player.avatar,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "@${player.alias}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (player.isAdmin) {
                            BadgeChip(text = "Administrador", color = MaterialTheme.colorScheme.error)
                        } else if (player.isCreator) {
                            BadgeChip(text = "Creador de Contenido", color = MaterialTheme.colorScheme.tertiary)
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${player.points} pts",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${player.qrCount} QR",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (canDelete) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Perfil",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Protegido con clave",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BadgeChip(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
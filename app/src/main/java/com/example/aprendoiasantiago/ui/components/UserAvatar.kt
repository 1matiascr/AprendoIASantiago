package com.example.aprendoiasantiago.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * Componente reutilizable para mostrar la foto de perfil del usuario,
 * ya sea cargada desde la Galería (URI) o de los avatares predeterminados.
 */
@Composable
fun UserAvatar(
    avatarPath: String,
    modifier: Modifier = Modifier
) {
    if (avatarPath.startsWith("content://") || avatarPath.startsWith("file://")) {
        // Renderiza la imagen guardada desde la Galería
        AsyncImage(
            model = avatarPath,
            contentDescription = "Avatar de Usuario",
            modifier = modifier.clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        // Extrae el ID si viene en formato "avatar_1", "avatar_2", etc.
        val avatarId = avatarPath.removePrefix("avatar_").toIntOrNull() ?: 1

        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (avatarId % 2 == 0) Icons.Default.Face else Icons.Default.Star,
                contentDescription = "Avatar predeterminado",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize(0.6f)
            )
        }
    }
}
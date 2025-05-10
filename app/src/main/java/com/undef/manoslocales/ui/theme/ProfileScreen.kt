package com.undef.manoslocales.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter

@Composable
fun ProfileScreen(user: User, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff3E2C1C))
            .padding(24.dp), // Aumentado de 16.dp a 24.dp
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Imagen de perfil
        Image(
            painter = rememberImagePainter(user.profileImageUrl),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(140.dp) // Aumentado de 120.dp a 140.dp
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(24.dp)) // Aumentado

        // Datos del usuario
        Text(
            text = "Nombre: ${user.name}",
            style = MaterialTheme.typography.headlineSmall, // Más grande que titleLarge
            color = Color.White
        )
        Text(
            text = "Correo: ${user.email}",
            style = MaterialTheme.typography.titleMedium, // Más grande que bodyMedium
            color = Color.White
        )
        Text(
            text = "Ubicación: ${user.location}",
            style = MaterialTheme.typography.bodyLarge, // Más grande que bodySmall
            color = Color.White
        )

        Spacer(modifier = Modifier.height(40.dp)) // Más espacio

        // Opciones de configuración
        Button(
            onClick = { /* Acciones de configuración */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp) // Botón más alto
        ) {
            Text(text = "Editar perfil", color = Color.Black, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /* Cambiar contraseña */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
        ) {
            Text(text = "Cambiar contraseña", color = Color.Black, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /* Cerrar sesión */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
        ) {
            Text(text = "Cerrar sesión", color = Color.Black, style = MaterialTheme.typography.titleMedium)
        }
    }
}

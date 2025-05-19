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
import com.undef.manoslocales.ui.navigation.BottomNavigationBar

@Composable
fun ProfileScreen(user: User, navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                navController = navController
            )
        },
        containerColor = Color(0xff3E2C1C)
    ) { paddingValues ->

        //  Aquí se aplica el padding de Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff3E2C1C))
                .padding(paddingValues)  // ✅ Solución: se aplica el padding aquí
                .padding(24.dp), // Este padding extra se mantiene
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Imagen de perfil
            Image(
                painter = rememberImagePainter(user.profileImageUrl),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Datos del usuario
            Text(
                text = "Name: ${user.name}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Text(
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = "Phone Number: ${user.phonenumber}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Opciones de configuración
            Button(
                onClick = { /* Acciones de configuración */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text(
                    text = "Editar perfil",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Cambiar contraseña */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text(
                    text = "Cambiar contraseña",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Cerrar sesión */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text(
                    text = "Cerrar sesión",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

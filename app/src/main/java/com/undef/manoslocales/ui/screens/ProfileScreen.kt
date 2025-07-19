package com.undef.manoslocales.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.User // ✅ Asegurate de usar esta clase
import com.undef.manoslocales.ui.navigation.BottomNavigationBar

@Composable
fun ProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var selectedItem by remember { mutableStateOf(0) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.fetchUserInfo { fetchedUser ->
            user = fetchedUser
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff3E2C1C))
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            user?.let {
                Image(
                    painter = rememberImagePainter(it.profileImageUrl),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Nombre: ${it.nombre} ${it.apellido}", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Text("Email: ${it.email}", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Teléfono: ${it.phone}", style = MaterialTheme.typography.bodyLarge, color = Color.White)

                Spacer(modifier = Modifier.height(40.dp))
            } ?: Text("Cargando usuario...", color = Color.White)

            Button(
                onClick = { navController.navigate("editProfile") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text("Editar perfil", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* cambiar contraseña */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text("Cambiar contraseña", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    userViewModel.logoutUser()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text("Cerrar sesión", color = Color.Black)
            }
        }
    }
}

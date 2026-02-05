package com.undef.manoslocales.ui.screens

import android.app.Activity
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.login.LoginActivity
import com.undef.manoslocales.ui.navigation.BottomNavigationBar

@Composable
fun ProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var selectedItem by remember { mutableIntStateOf(1) } // 1 es el índice de Perfil en tu BottomBar
    var user by remember { mutableStateOf<User?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.fetchUserInfo { fetchedUser ->
            user = fetchedUser
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home") { popUpTo("home") { inclusive = true } }
                        1 -> {} // Ya estamos en Perfil
                        2 -> navController.navigate("settings") { popUpTo("settings") { inclusive = true } }
                    }
                },
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
                    painter = rememberAsyncImagePainter(it.profileImageUrl),
                    contentDescription = stringResource(id = R.string.profile_pic_desc),
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(id = R.string.profile_name, it.nombre, it.apellido),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.profile_email, it.email),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.profile_phone, it.phone),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(40.dp))
            } ?: Text(stringResource(id = R.string.loading_user), color = Color.White)

            Button(
                onClick = { navController.navigate("editProfile") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text(stringResource(id = R.string.btn_edit_profile), color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("changePassword") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text(stringResource(id = R.string.btn_change_password), color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // 1. Cerramos sesión en Firebase y SessionManager
                    userViewModel.logoutUser()
                    
                    // 2. Iniciamos LoginActivity explícitamente
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        // Limpiamos el stack de tareas para que no puedan volver atrás
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    
                    // 3. Cerramos la MainActivity actual
                    (context as? Activity)?.finish()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text(stringResource(id = R.string.btn_logout), color = Color.Black)
            }
        }
    }
}

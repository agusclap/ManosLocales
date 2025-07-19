package com.undef.manoslocales.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.database.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var user by remember { mutableStateOf<User?>(null) }

    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var telefono by remember { mutableStateOf(TextFieldValue("")) }
    var categoria by remember { mutableStateOf(TextFieldValue("")) }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        userViewModel.fetchUserInfo { u ->
            user = u
            u?.let {
                nombre = TextFieldValue(it.nombre)
                apellido = TextFieldValue(it.apellido)
                telefono = TextFieldValue(it.phone)
                if (it.role == "provider") {
                    categoria = TextFieldValue(it.categoria ?: "")
                }
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3E2C1C))
            )
        },
        containerColor = Color(0xFF3E2C1C)
    ) { padding ->
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFEFAE0))
            }
        } else {
            user?.let { u ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp)
                        .background(Color(0xFF3E2C1C)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    Spacer(Modifier.height(12.dp))

                    if (u.role == "provider") {
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = { categoria = it },
                            label = { Text("Rubro / Categoría") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors()
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            val updated = u.copy(
                                nombre = nombre.text,
                                apellido = apellido.text,
                                phone = telefono.text,
                                categoria = if (u.role == "provider") categoria.text else u.categoria
                            )
                            userViewModel.updateUserProfile(updated) {
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEFAE0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Guardar cambios", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFFEFAE0),
    unfocusedBorderColor = Color.LightGray,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color.White,
    focusedLabelColor = Color(0xFFFEFAE0),
    unfocusedLabelColor = Color.LightGray
)

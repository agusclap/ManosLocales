package com.undef.manoslocales.ui.screens

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.database.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val categoryOptions = listOf("Tecnologia", "Herramientas", "Alimentos")
    val context = LocalContext.current

    var user by remember { mutableStateOf<User?>(null) }
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var apellido by remember { mutableStateOf(TextFieldValue("")) }
    var telefono by remember { mutableStateOf(TextFieldValue("")) }
    var ciudad by remember { mutableStateOf(TextFieldValue("")) }
    var categoria by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }

    var showDropdown by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data?.data != null) {
            selectedImageUri = data.data
            userViewModel.uploadUserProfileImage(data.data!!) { uploadedUrl ->
                uploadedUrl?.let {
                    profileImageUrl = it
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                imagePickerLauncher.launch(intent)
            }
        }
    )

    LaunchedEffect(Unit) {
        userViewModel.fetchUserInfo { fetchedUser ->
            user = fetchedUser
            fetchedUser?.let {
                nombre = TextFieldValue(it.nombre)
                apellido = TextFieldValue(it.apellido)
                telefono = TextFieldValue(it.phone)
                ciudad = TextFieldValue(it.city ?: "")
                categoria = it.categoria ?: categoryOptions.first()
                profileImageUrl = it.profileImageUrl
            }
        }
    }

    Scaffold(
        containerColor = Color(0xff3E2C1C)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = selectedImageUri ?: profileImageUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable {
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            android.Manifest.permission.READ_MEDIA_IMAGES
                        else
                            android.Manifest.permission.READ_EXTERNAL_STORAGE

                        val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            imagePickerLauncher.launch(intent)
                        } else {
                            permissionLauncher.launch(permission)
                        }
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre", color = Color(0xFFFEFAE0)) },
                modifier = Modifier.fillMaxWidth(),
                colors = profileFieldColors()
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido", color = Color(0xFFFEFAE0)) },
                modifier = Modifier.fillMaxWidth(),
                colors = profileFieldColors()
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono", color = Color(0xFFFEFAE0)) },
                modifier = Modifier.fillMaxWidth(),
                colors = profileFieldColors()
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { Text("Ciudad", color = Color(0xFFFEFAE0)) },
                modifier = Modifier.fillMaxWidth(),
                colors = profileFieldColors()
            )
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = showDropdown,
                onExpandedChange = { showDropdown = !showDropdown }
            ) {
                TextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rubro", color = Color(0xFFFEFAE0)) },
                    trailingIcon = {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.White)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = profileFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false }
                ) {
                    categoryOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                categoria = option
                                showDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    user?.let {
                        val updated = it.copy(
                            nombre = nombre.text,
                            apellido = apellido.text,
                            phone = telefono.text,
                            city = ciudad.text.trim().lowercase(),
                            categoria = categoria,
                            profileImageUrl = profileImageUrl
                        )
                        userViewModel.updateUserProfile(updated) {
                            navController.popBackStack()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Guardar cambios", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffFEFAE0)),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Cancelar", color = Color.Black)
            }
        }
    }
}

@Composable
private fun profileFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color(0xFFFEFAE0),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    focusedLabelColor = Color(0xFFFEFAE0),
    unfocusedLabelColor = Color.LightGray,
    focusedContainerColor = Color(0xFF5C4033),
    unfocusedContainerColor = Color(0xFF5C4033)
)

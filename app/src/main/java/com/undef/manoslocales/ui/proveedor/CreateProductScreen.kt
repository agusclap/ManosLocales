package com.undef.manoslocales.ui.proveedor

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.undef.manoslocales.ui.database.UserViewModel

@Composable
fun CreateProductScreen(viewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3E2C1C))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopCenter)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Producto",
                fontSize = 28.sp,
                color = Color(0xFFFFF5C0),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFF5C0),
                    focusedContainerColor = Color(0xFFFFF5C0),
                    unfocusedIndicatorColor = Color(0xFFFEFAE0),
                    focusedIndicatorColor = Color(0xFFFEFAE0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFF5C0),
                    focusedContainerColor = Color(0xFFFFF5C0),
                    unfocusedIndicatorColor = Color(0xFFFEFAE0),
                    focusedIndicatorColor = Color(0xFFFEFAE0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFF5C0),
                    focusedContainerColor = Color(0xFFFFF5C0),
                    unfocusedIndicatorColor = Color(0xFFFEFAE0),
                    focusedIndicatorColor = Color(0xFFFEFAE0)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEFAE0),
                    contentColor = Color(0xFF3E2C1C)
                )
            ) {
                Text("Seleccionar imagen")
            }

            imageUri?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val priceDouble = price.toDoubleOrNull() ?: 0.0
                    if (imageUri != null) {
                        viewModel.uploadProductImage(imageUri!!) { imageUrl ->
                        if (imageUrl != null) {
                                viewModel.createProduct(name, description, priceDouble, imageUrl) { success, error ->
                                    if (success) {
                                        Toast.makeText(context, "Producto creado!", Toast.LENGTH_SHORT).show()
                                        name = ""
                                        description = ""
                                        price = ""
                                        imageUri = null
                                    } else {
                                        Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Seleccioná una imagen", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEFAE0),
                    contentColor = Color(0xFF3E2C1C)
                )
            ) {
                Text("Crear producto", fontSize = 18.sp)
            }
        }
    }
}

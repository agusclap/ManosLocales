package com.undef.manoslocales.ui.proveedor

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(viewModel: UserViewModel, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("Buenos Aires") }
    var cityExpanded by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf("Tecnología") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val provincias = listOf(
        "Buenos Aires", "CABA", "Catamarca", "Chaco", "Chubut", "Córdoba", "Corrientes",
        "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", "Mendoza", "Misiones",
        "Neuquén", "Río Negro", "Salta", "San Juan", "San Luis", "Santa Cruz", "Santa Fe",
        "Santiago del Estero", "Tierra del Fuego", "Tucumán"
    )
    val categorias = listOf("Tecnología", "Herramientas", "Alimentos")
    val context = LocalContext.current
    val placeholderImage = "https://via.placeholder.com/150"

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    fun handleCreateResult(success: Boolean, error: String?) {
        if (success) {
            Toast.makeText(context, "Producto creado exitosamente!", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        } else {
            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Producto", color = Cafe, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Cafe)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Crema)
            )
        },
        containerColor = CafeOscuro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del producto
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Crema.copy(alpha = 0.1f))
                    .clickable {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Añadir Foto", color = Crema, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                colors = outlinedTextFieldColors()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = outlinedTextFieldColors()
            )

            // Selección de Provincia (Exposed Dropdown Menu)
            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = { cityExpanded = !cityExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Provincia / Ciudad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = cityExpanded,
                    onDismissRequest = { cityExpanded = false },
                    modifier = Modifier.background(Crema)
                ) {
                    provincias.forEach { provincia ->
                        DropdownMenuItem(
                            text = { Text(provincia, color = Cafe) },
                            onClick = {
                                selectedCity = provincia
                                cityExpanded = false
                            }
                        )
                    }
                }
            }

            // Selección de Categoría
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(Crema)
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria, color = Cafe) },
                            onClick = {
                                selectedCategory = categoria
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val priceDouble = price.toDoubleOrNull()
                    if (name.isBlank() || priceDouble == null) {
                        Toast.makeText(context, "Complete los campos correctamente", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val normalizedCity = selectedCity.trim().lowercase()

                    if (imageUri != null) {
                        viewModel.uploadProductImage(imageUri!!) { imageUrl ->
                            if (imageUrl == null) {
                                Toast.makeText(context, "Error subiendo imagen", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.createProduct(
                                    name, description, priceDouble, imageUrl,
                                    selectedCategory, normalizedCity, ::handleCreateResult
                                )
                            }
                        }
                    } else {
                        viewModel.createProduct(
                            name, description, priceDouble, placeholderImage,
                            selectedCategory, normalizedCity, ::handleCreateResult
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Crema,
                    contentColor = Cafe
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("CREAR PRODUCTO", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Crema,
    unfocusedTextColor = Crema,
    focusedBorderColor = Crema,
    unfocusedBorderColor = Crema.copy(alpha = 0.5f),
    focusedLabelColor = Crema,
    unfocusedLabelColor = Crema.copy(alpha = 0.7f),
    cursorColor = Crema
)

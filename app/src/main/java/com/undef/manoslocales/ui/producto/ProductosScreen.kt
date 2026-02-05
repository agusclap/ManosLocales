package com.undef.manoslocales.ui.producto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.theme.*

@Composable
fun ProductosScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    favoritosViewModel: FavoritosViewModel
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("Todas") }
    var cityExpanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableIntStateOf(0) }

    val categories = listOf("Todas", "Tecnología", "Herramientas", "Alimentos")
    
    // Provincias dinámicas del ViewModel
    val provincias = viewModel.provincias.value
    val isProvinciasLoading = viewModel.isProvinciasLoading.value

    var productos by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val productosFavoritos by favoritosViewModel.productosFavoritos.collectAsState()
    
    val ciudadParaFiltro = if (selectedCity == "Todas") null else selectedCity.trim().lowercase()

    LaunchedEffect(Unit) {
        viewModel.loadProvincias()
    }

    LaunchedEffect(selectedCategory, selectedCity) {
        isLoading = true
        viewModel.getFilteredProducts(
            categoria = if (selectedCategory == "Todas") null else selectedCategory,
            ciudad = ciudadParaFiltro,
            proveedorId = null
        ) {
            productos = it
            isLoading = false
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true }
                        1 -> navController.navigate("favoritos") { popUpTo("favoritos") { inclusive = true }; launchSingleTop = true }
                        2 -> navController.navigate("settings") { popUpTo("settings") { inclusive = true }; launchSingleTop = true }
                    }
                },
                navController = navController
            )
        },
        containerColor = CafeOscuro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.manoslocales),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            // --- BUSCADOR CORREGIDO (Sin bug visual y con navegación) ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar productos...", color = Cafe.copy(alpha = 0.6f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Cafe) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Cafe)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (searchQuery.isNotBlank()) {
                        navController.navigate("searchResults/$searchQuery")
                    }
                }),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Crema,
                    unfocusedContainerColor = Crema,
                    focusedTextColor = Cafe,
                    unfocusedTextColor = Cafe,
                    focusedBorderColor = Crema,
                    unfocusedBorderColor = Crema
                )
            )

            // Filtros
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryDropdown(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it },
                            categories = categories
                        )
                    }

                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenuBox(
                        expanded = cityExpanded,
                        onExpandedChange = { cityExpanded = !cityExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(surface = Crema)) {
                            OutlinedTextField(
                                value = selectedCity,
                                onValueChange = {},
                                readOnly = true,
                                label = { 
                                    Text(
                                        "Provincia", 
                                        style = TextStyle(color = Cafe, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                                        color = Crema

                                    ) 
                                },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = TextStyle(color = Cafe, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Crema,
                                    unfocusedContainerColor = Crema,
                                    focusedTextColor = Cafe,
                                    unfocusedTextColor = Cafe,
                                    focusedBorderColor = Cafe,
                                    unfocusedBorderColor = Cafe,
                                    focusedLabelColor = Cafe,
                                    unfocusedLabelColor = Cafe,
                                    focusedTrailingIconColor = Cafe,
                                    unfocusedTrailingIconColor = Cafe
                                )
                            )
                        }
                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false },
                            modifier = Modifier.background(Crema)
                        ) {
                            if (isProvinciasLoading) {
                                DropdownMenuItem(
                                    text = { Text("Cargando...", color = Cafe) },
                                    onClick = {}
                                )
                            } else {
                                provincias.forEach { prov ->
                                    DropdownMenuItem(
                                        text = { Text(prov, color = Cafe, fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                                        onClick = { selectedCity = prov; cityExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Crema)
                }
            } else if (productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No se encontraron productos", color = Crema, fontWeight = FontWeight.Bold)
                        Text("Prueba con otros filtros", color = Crema.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(productos) { producto ->
                        val isFavorito = productosFavoritos.any { it.id == producto.id }
                        ItemProduct(
                            producto = producto,
                            isFavorito = isFavorito,
                            onFavoritoClicked = { favoritosViewModel.toggleProductoFavorito(it) },
                            onVerDetallesClick = {
                                navController.navigate("productoDetalle/${producto.id}/${producto.providerId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

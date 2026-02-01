package com.undef.manoslocales.ui.proveedor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedoresScreen(
    navController: NavHostController,
    viewModel: UserViewModel,
    favoritosViewModel: FavoritosViewModel
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    var searchQuery by remember { mutableStateOf("") }
    var searchCity by remember { mutableStateOf("") }
    var selectedItem by remember { mutableIntStateOf(1) }
    var isSearchActive by remember { mutableStateOf(false) }

    val categories = listOf("Todas", "Tecnología", "Herramientas", "Alimentos")
    val proveedoresFavoritos by favoritosViewModel.proveedoresFavoritos.collectAsState()
    
    var proveedores by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(selectedCategory) {
        isLoading = true
        if (selectedCategory == "Todas") {
            viewModel.getProviders {
                proveedores = it
                isLoading = false
            }
        } else {
            viewModel.getProvidersByCategory(selectedCategory) {
                proveedores = it
                isLoading = false
            }
        }
    }

    val filteredList = proveedores.filter { proveedor ->
        val matchesName = proveedor.nombre.contains(searchQuery, ignoreCase = true) || 
                         proveedor.apellido.contains(searchQuery, ignoreCase = true)
        val matchesCity = searchCity.isEmpty() || (proveedor.city?.contains(searchCity, ignoreCase = true) ?: false)
        matchesName && matchesCity
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                navController = navController
            )
        },
        containerColor = CafeOscuro
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Image(
                    painter = painterResource(R.drawable.manoslocales),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(top = 16.dp, bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )

                // Buscador de Nombre
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { isSearchActive = false },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Buscar proveedor por nombre...", color = GrisSuave) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Cafe) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Cafe)
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(containerColor = Crema),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    // Sugerencias...
                }

                // Filtros con MaterialTheme local para corregir el fondo de las etiquetas (labels)
                MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(surface = Crema)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryDropdown(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it },
                            categories = categories
                        )

                        OutlinedTextField(
                            value = searchCity,
                            onValueChange = { searchCity = it },
                            label = {
                                Text("Filtrar por Ciudad", color = Cafe)
                            },
                            placeholder = { Text("Ej: Córdoba", color = GrisSuave.copy(alpha = 0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Cafe,
                                unfocusedTextColor = Cafe,
                                focusedBorderColor = Cafe,
                                unfocusedBorderColor = CafeClaro.copy(alpha = 0.5f),
                                focusedLabelColor = Cafe,
                                unfocusedLabelColor = Cafe.copy(alpha = 0.7f),
                                focusedContainerColor = Crema,
                                unfocusedContainerColor = Crema
                            )
                        )

                        Button(
                            onClick = { navController.navigate("nearby") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Crema,
                                contentColor = Cafe
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                "📍 Ver proveedores cercanos (GPS)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Crema)
                    }
                } else if (filteredList.isEmpty()) {
                    EmptyStateProveedores(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredList) { proveedor ->
                            ProveedorItem(
                                proveedor = proveedor,
                                isFavorito = proveedoresFavoritos.any { it.id == proveedor.id },
                                onFavoritoClicked = { favoritosViewModel.toggleProveedorFavorito(proveedor) },
                                onVerDetallesClick = {
                                    navController.navigate("proveedorDetalle/${proveedor.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateProveedores(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("No se encontraron proveedores", color = Crema, style = MaterialTheme.typography.headlineSmall)
            Text("Intenta ajustar los filtros", color = GrisSuave)
        }
    }
}

@Composable
fun ProveedorItem(
    proveedor: User,
    isFavorito: Boolean,
    onFavoritoClicked: (User) -> Unit,
    onVerDetallesClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onVerDetallesClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Crema)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = proveedor.profileImageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(proveedor.nombre, style = MaterialTheme.typography.titleLarge, color = Cafe, fontWeight = FontWeight.Bold)
                Text(proveedor.categoria ?: "General", style = MaterialTheme.typography.bodyMedium, color = GrisSuave)
                Text(proveedor.city ?: "Ubicación no especificada", style = MaterialTheme.typography.bodySmall, color = Cafe)
            }
            IconButton(onClick = { onFavoritoClicked(proveedor) }) {
                Icon(
                    imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorito) Cafe else GrisSuave
                )
            }
        }
    }
}

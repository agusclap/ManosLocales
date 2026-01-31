package com.undef.manoslocales.ui.proveedor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.database.User
import com.undef.manoslocales.ui.database.UserViewModel
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.theme.*

@Composable
fun ProveedoresScreen(
    navController: NavHostController,
    favoritosViewModel: FavoritosViewModel,
    // CORRECCIÓN 1: Añadimos el UserViewModel como parámetro para poder usarlo.
    userViewModel: UserViewModel
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    val categories = listOf("Todas", "Tecnologia", "Herramientas", "Alimentos")
    var searchQuery by remember { mutableStateOf("") }
    var searchCity by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf(1) }

    val proveedoresFavoritos by favoritosViewModel.proveedoresFavoritos.collectAsState()
    var proveedores by remember { mutableStateOf<List<User>>(emptyList()) }

    // CORRECCIÓN 2: Llamamos a getProviders en la INSTANCIA 'userViewModel'.
    LaunchedEffect(Unit) {
        userViewModel.getProviders { listaDeProveedores ->
            proveedores = listaDeProveedores
        }
    }

    val filteredList = proveedores.filter {
        (selectedCategory == "Todas" || it.categoria == selectedCategory) &&
                it.nombre.contains(searchQuery, ignoreCase = true) &&
                (searchCity.isBlank() || it.city?.contains(searchCity, ignoreCase = true) == true)
    }

    var isSearchActive by remember { mutableStateOf(false) }

    ManosLocalesTheme {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(CafeOscuro)
            ) {
                // Header con logo
                Image(
                    painter = painterResource(id = R.drawable.manoslocales),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )

                // DockedSearchBar moderno
                @OptIn(ExperimentalMaterial3Api::class)
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = {
                        Text(
                            "Buscar proveedores...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisSuave
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Cafe
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = Cafe
                                )
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = Crema,
                        inputFieldColors = SearchBarDefaults.inputFieldColors(
                            focusedTextColor = Cafe,
                            unfocusedTextColor = Cafe,
                            focusedPlaceholderColor = GrisSuave,
                            unfocusedPlaceholderColor = GrisSuave
                        )
                    ),
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 4.dp
                ) {
                    // Contenido cuando el SearchBar está activo
                    LazyColumn {
                        items(filteredList.take(5)) { proveedor ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        proveedor.nombre,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Cafe
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        proveedor.categoria ?: "Sin categoría",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = GrisSuave
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("proveedorDetalle/${proveedor.id}")
                                        isSearchActive = false
                                    }
                            )
                        }
                    }
                }

                // Filtros mejorados
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            Text(
                                "Ciudad",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Cafe,
                            unfocusedTextColor = Cafe,
                            focusedBorderColor = Cafe,
                            unfocusedBorderColor = CafeClaro.copy(alpha = 0.5f),
                            focusedLabelColor = Cafe,
                            unfocusedLabelColor = GrisSuave
                        )
                    )

                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("nearby") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        containerColor = Crema,
                        contentColor = Cafe,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            "Ver proveedores cercanos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de proveedores
                when {
                    filteredList.isEmpty() -> {
                        EmptyStateProveedores(
                            modifier = Modifier.weight(1f)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
}

@Composable
private fun EmptyStateProveedores(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_proveedores),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = GrisSuave.copy(alpha = 0.5f)
            )
            Text(
                text = "No se encontraron proveedores",
                style = MaterialTheme.typography.headlineSmall,
                color = Crema,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Intenta ajustar tus filtros de búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisSuave,
                textAlign = TextAlign.Center
            )
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
    var isPressed by remember { mutableStateOf(false) }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale)
            .clickable(
                onClick = {
                    isPressed = true
                    onVerDetallesClick()
                },
                onClickLabel = "Ver detalles de ${proveedor.nombre}"
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Crema
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = CafeClaro.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del proveedor con aspectRatio(1f) y Clip(RoundedCornerShape(16.dp))
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                AsyncImage(
                    model = proveedor.profileImageUrl,
                    contentDescription = "Imagen de ${proveedor.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Título con headlineMedium Bold
                    Text(
                        text = proveedor.nombre,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = Cafe,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    // Categoría con bodyMedium grisáceo
                    Text(
                        text = proveedor.categoria ?: "Sin categoría",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisSuave,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    // Email
                    Text(
                        text = proveedor.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisSuave,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    // Ciudad si está disponible
                    proveedor.city?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Cafe,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onVerDetallesClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Cafe
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Ver detalles",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    IconButton(
                        onClick = { onFavoritoClicked(proveedor) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (isFavorito) Cafe else GrisSuave,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

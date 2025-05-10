package com.undef.manoslocales.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.CategoryDropdown

// Lista de ejemplo
val emprendedoresList = listOf(
    Emprendedor(1, "Juan Pérez", "Córdoba", "Artesanías", "file:///android_asset/sample_image.jpg"),
    Emprendedor(2, "Ana García", "Buenos Aires", "Textiles", "file:///android_asset/sample_image.jpg"),
    Emprendedor(3, "Pedro López", "Mendoza", "Alimentos", "file:///android_asset/sample_image.jpg")
)

@Composable
fun EmprendedoresScreen(navController: NavHostController) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    var favoritos by remember { mutableStateOf<List<Emprendedor>>(emptyList()) }
    val categories = listOf("Todas", "Artesanías", "Textiles", "Alimentos", "Favoritos")

    val filteredList = when (selectedCategory) {
        "Favoritos" -> favoritos
        "Todas" -> emprendedoresList
        else -> emprendedoresList.filter { it.categoria == selectedCategory }
    }

    var selectedItem by remember { mutableStateOf(0) }

    ManosLocalesTheme {
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
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.manoslocales),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .width(180.dp)
                        .offset(y = (-18).dp)
                )

                CategoryDropdown(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    categories = categories
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xff3E2C1C)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(filteredList) { emprendedor ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmprendedorItem(
                                emprendedor = emprendedor,
                                isFavorito = favoritos.any { it.id == emprendedor.id },
                                onFavoritoClicked = { selected ->
                                    favoritos = if (favoritos.any { it.id == selected.id }) {
                                        favoritos.filter { it.id != selected.id }
                                    } else {
                                        favoritos + selected
                                    }
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
fun EmprendedorItem(
    emprendedor: Emprendedor,
    isFavorito: Boolean,
    onFavoritoClicked: (Emprendedor) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(320.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xff3E2C1C))
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            // Imagen
            Card(
                modifier = Modifier.size(115.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AsyncImage(
                    model = emprendedor.imagenUrl,
                    contentDescription = "Imagen de ${emprendedor.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = emprendedor.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = emprendedor.categoria,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Text(
                    text = emprendedor.ubicacion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { /* Ver detalles */ }) {
                        Text(
                            text = "Ver detalles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = { onFavoritoClicked(emprendedor) }) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Agregar a favoritos",
                            tint = Color(0xffFEFAE0)
                        )
                    }
                }
            }
        }
    }
}



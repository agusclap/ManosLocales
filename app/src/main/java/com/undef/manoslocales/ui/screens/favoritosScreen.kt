package com.undef.manoslocales.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.undef.manoslocales.ui.navigation.BottomNavigationBar
import com.undef.manoslocales.ui.navigation.FavoritosViewModel
import com.undef.manoslocales.ui.producto.ProductoItem
import com.undef.manoslocales.ui.proveedor.ProveedorItem


@Composable
fun FavoritosScreen(
    navController: NavHostController,
    favoritosViewModel: FavoritosViewModel
) {
    val productosFavoritos by favoritosViewModel.productosFavoritos.collectAsState()
    val proveedoresFavoritos by favoritosViewModel.proveedoresFavoritos.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 1,
                onItemSelected = { /* manejar navegación */ },
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

            Text(
                text = "Mis Favoritos",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn {
                if (productosFavoritos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Productos Favoritos",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(productosFavoritos) { producto ->
                        ProductoItem(
                            producto = producto,
                            isFavorito = true,
                            onFavoritoClicked = { favoritosViewModel.toggleProductoFavorito(it) }
                        )
                    }
                }

                if (proveedoresFavoritos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Proveedores Favoritos",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(proveedoresFavoritos) { proveedor ->
                        ProveedorItem(
                            proveedor = proveedor,
                            isFavorito = true,
                            onFavoritoClicked = { favoritosViewModel.toggleProveedorFavorito(it) }
                        )
                    }
                }
            }
        }
    }
}


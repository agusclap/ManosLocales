package com.undef.manoslocales.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.undef.manoslocales.R
import com.undef.manoslocales.ui.navigation.CategoryDropdown


// Lista de ejemplo para poblar la LazyColumn
val emprendedoresList = listOf(
    Emprendedor(1, "Juan Pérez", "Córdoba", "Artesanías", "https://via.placeholder.com/150"),
    Emprendedor(2, "Ana García", "Buenos Aires", "Textiles", "https://via.placeholder.com/150"),
    Emprendedor(3, "Pedro López", "Mendoza", "Alimentos", "https://via.placeholder.com/150"),
    Emprendedor(1, "Juan Pérez", "Córdoba", "Artesanías", "https://via.placeholder.com/150"),
    Emprendedor(2, "Ana García", "Buenos Aires", "Textiles", "https://via.placeholder.com/150"),
    Emprendedor(3, "Pedro López", "Mendoza", "Alimentos", "https://via.placeholder.com/150")
)

@Composable
fun EmprendedoresScreen() {
    var selectedCategory by remember { mutableStateOf("Todas") }
    val categories = listOf("Todas", "Artesanías", "Textiles", "Alimentos")
    val filteredList = if (selectedCategory == "Todas") {
        emprendedoresList
    } else {
        emprendedoresList.filter { it.categoria == selectedCategory }
    }

    ManosLocalesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xff3E2C1C))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally // CENTRAR TODO
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

                Spacer(modifier = Modifier.height(0.dp))

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
                    horizontalAlignment = Alignment.CenterHorizontally // CENTRAR CADA ITEM
                ) {
                    items(filteredList) { emprendedor ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmprendedorItem(emprendedor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmprendedorItem(emprendedor: Emprendedor) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(320.dp)
            ,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // SIN SOMBRA
        shape = RoundedCornerShape(0.dp), // ESQUINAS RECTAS (opcional)
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff3E2C1C) // MISMO COLOR QUE EL FONDO
        )
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

            // Info
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
                TextButton(
                    onClick = { /* Acción al hacer click */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "See Details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewEmprendedoresScreen() {
    EmprendedoresScreen()
}
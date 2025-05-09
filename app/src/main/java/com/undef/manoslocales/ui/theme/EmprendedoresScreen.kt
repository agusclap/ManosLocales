package com.undef.manoslocales.rodeyromacedo.ui.screens

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
import com.undef.manoslocales.ui.theme.ManosLocalesTheme

// Data class que representa un Emprendedor
data class Emprendedor(
    val id: Int,
    val nombre: String,
    val ubicacion: String,
    val categoria: String,
    val imagenUrl: String
)

// Lista de ejemplo para poblar la LazyColumn
val emprendedoresList = listOf(
    Emprendedor(1, "Juan Pérez", "Córdoba", "Artesanías", "https://via.placeholder.com/150"),
    Emprendedor(2, "Ana García", "Buenos Aires", "Textiles", "https://via.placeholder.com/150"),
    Emprendedor(3, "Pedro López", "Mendoza", "Alimentos", "https://via.placeholder.com/150")
)

@Composable
fun EmprendedoresScreen() {
    ManosLocalesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(Color(0xff3E2C1C))
                    .padding(8.dp)
            ) {
                items(emprendedoresList) { emprendedor ->
                    EmprendedorItem(emprendedor)
                }
            }
        }
    }
}

@Composable
fun EmprendedorItem(emprendedor: Emprendedor) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Imagen del Emprendedor
            /*Image(
                painter = rememberAsyncImagePainter(emprendedor.imagenUrl),
                contentDescription = "Imagen de ${emprendedor.nombre}",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )*/

            Spacer(modifier = Modifier.width(8.dp))

            // Información del Emprendedor
            Column {
                Text(text = emprendedor.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = emprendedor.ubicacion, style = MaterialTheme.typography.bodyMedium)
                Text(text = emprendedor.categoria, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmprendedoresScreen() {
    EmprendedoresScreen()
}
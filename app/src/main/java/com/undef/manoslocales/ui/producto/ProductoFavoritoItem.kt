package com.undef.manoslocales.ui.producto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.undef.manoslocales.ui.dataclasses.Product

@Composable
fun ProductoFavoritoItem(
    producto: Product,
    isFavorito: Boolean,
    onFavoritoClicked: (Product) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(producto.imageUrl),
            contentDescription = "Imagen del producto",
            modifier = Modifier
                .size(80.dp)
                .padding(end = 8.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(text = producto.name, style = MaterialTheme.typography.titleMedium)
            Text(text = producto.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$${producto.price}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = if (isFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Toggle favorito",
            tint = if (isFavorito) Color.Red else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable { onFavoritoClicked(producto) }
        )
    }
}

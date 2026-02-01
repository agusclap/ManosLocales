package com.undef.manoslocales.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.undef.manoslocales.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // El wrapper de MaterialTheme asegura que el fondo de la etiqueta (label mask) 
    // coincida con el color del contenedor del OutlinedTextField (Crema).
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(surface = Crema)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        "Categoría",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Cafe
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
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

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .exposedDropdownSize(true)
                    .background(Crema)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                category,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Cafe
                            )
                        },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

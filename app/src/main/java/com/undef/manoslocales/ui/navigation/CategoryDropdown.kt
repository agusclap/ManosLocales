package com.undef.manoslocales.ui.navigation

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
                    style = MaterialTheme.typography.bodyMedium
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
                unfocusedLabelColor = GrisSuave
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (category == selectedCategory) Cafe else CafeOscuro
                        )
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Cafe
                    )
                )
            }
        }
    }
}

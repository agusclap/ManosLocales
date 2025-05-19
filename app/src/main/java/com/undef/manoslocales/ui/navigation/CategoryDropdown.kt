package com.undef.manoslocales.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String, //Categoria seleccionada
    onCategorySelected: (String) -> Unit, //Callback cuando se selecciona una categoria
    categories: List<String>, //Lista de las categorias
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) } //controla si esl menu esta abierto o no

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField( //Cuando lo tocamos, se abre la lista de categorias
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach {
                category -> DropdownMenuItem( //Por cada categoria crea un item
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

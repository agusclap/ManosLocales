package com.undef.manoslocales.ui.theme

data class Emprendedor(
    val id: Int,
    val nombre: String,
    val ubicacion: String,
    val categoria: String,
    val imagenUrl: String
)

data class Proveedor(
    val id: Int,
    val nombre: String,
    val ubicacion: String,
    val categoria: String,
    val imagenUrl: String,
    val favorito: Boolean = false
)

data class User(
    val name: String,
    val email: String,
    val location: String,
    val profileImageUrl: String
)

fun getUser(): User {
    // Aca podes obtener los datos desde tu fuente de datos, por ejemplo una base de datos, API o valores estáticos
    return User(
        name = "Juan Pérez",
        email = "juan.perez@example.com",
        location = "Córdoba, Argentina",
        profileImageUrl = "file:///android_asset/sample_image.jpg"
    )
}

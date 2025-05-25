package com.undef.manoslocales.ui.users

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val imagenUrl: Int
)

data class Proveedor(
    val id: Int,
    val nombre: String,
    val ubicacion: String,
    val categoria: String,
    val imagenUrl: Int,
    val favorito: Boolean = false
)

data class User(
    val name: String,
    val lastname: String,
    val email: String,
    val phonenumber: String,
    val profileImageUrl: String
)

fun getUser(): User {
    // Aca podes obtener los datos desde tu fuente de datos, por ejemplo una base de datos, API o valores estáticos
    return User(
        name = "Juan",
        lastname = "Perez",
        email = "juan.perez@example.com",
        phonenumber = "2994090836",
        profileImageUrl = "file:///android_asset/sample_image.jpg"
    )
}

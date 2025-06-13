// src/main/java/com/undef/manoslocales.ui.database/UserRepository.kt
package com.undef.manoslocales.ui.database

import com.undef.manoslocales.ui.utils.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser == null) {
                // HASH LA CONTRASEÑA ANTES DE INSERTARLA
                val hashedPassword = PasswordHasher.hashPassword(user.password)
                val userToInsert = user.copy(password = hashedPassword) // Crea una copia con la contraseña hasheada
                userDao.insert(userToInsert)
                true
            } else {
                false // El email ya está registrado
            }
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }

    // NUEVA FUNCIÓN: Para manejar la verificación de inicio de sesión
    suspend fun loginUser(email: String, passwordPlain: String): User? {
        return withContext(Dispatchers.IO) {
            val user = userDao.getUserByEmail(email)
            if (user != null) {
                // VERIFICA LA CONTRASEÑA PROPORCIONADA CONTRA EL HASH ALMACENADO
                if (PasswordHasher.verifyPassword(passwordPlain, user.password)) {
                    return@withContext user // La contraseña coincide, retorna el usuario
                }
            }
            return@withContext null // Usuario no encontrado o contraseña incorrecta
        }
    }
}
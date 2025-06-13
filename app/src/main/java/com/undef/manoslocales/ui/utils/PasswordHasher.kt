package com.undef.manoslocales.ui.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    // Genera un hash con un salt aleatorio y un costo de trabajo (work factor)
    // El work factor (e.g., 10) determina cuán costoso es el hash.
    // 10 es un buen punto de partida, pero puedes incrementarlo a 12 o más si la CPU lo permite.
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(10)) // 10 es el "log_rounds" por defecto
    }

    // Verifica una contraseña de texto plano contra un hash almacenado
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        // BCrypt.checkpw se encarga de extraer el salt del hashedPassword y usarlo
        return BCrypt.checkpw(password, hashedPassword)
    }
}
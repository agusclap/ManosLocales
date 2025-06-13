// src/main/java/com/undef/manoslocales/ui/database/UserRepository.kt
package com.undef.manoslocales.ui.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Boolean {
        return withContext(Dispatchers.IO) { // Ensure this runs on a background thread
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser == null) {
                userDao.insert(user)
                true
            } else {
                false // Email already registered
            }
        }
    }

    // Add other user-related database operations here (e.g., login, get user by ID)
    suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }
}
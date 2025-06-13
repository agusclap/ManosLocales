package com.undef.manoslocales.ui.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val PREF_NAME = "UserSession"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"
    private val KEY_USER_EMAIL = "userEmail" // O Key_USER_ID, dependiendo de lo que quieras guardar

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Guarda el estado de login del usuario.
     */
    fun saveLoginState(isLoggedIn: Boolean, userEmail: String? = null) {
        val editor = sharedPrefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.putString(KEY_USER_EMAIL, userEmail)
        editor.apply() // Guarda los cambios de forma asíncrona
    }

    /**
     * Obtiene el estado de login actual del usuario.
     */
    fun isLoggedIn(): Boolean {
        return sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Obtiene el email del usuario logueado.
     */
    fun getLoggedInUserEmail(): String? {
        return sharedPrefs.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Cierra la sesión del usuario, limpiando los datos.
     */
    fun logout() {
        val editor = sharedPrefs.edit()
        editor.clear() // Elimina todos los datos de SharedPreferences
        editor.apply()
    }
}
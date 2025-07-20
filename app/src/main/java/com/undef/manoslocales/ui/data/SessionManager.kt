package com.undef.manoslocales.ui.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

/**
 * Gestor de sesión mejorado.
 *
 * Esta versión utiliza Firebase Authentication como la fuente principal de verdad
 * para saber si el usuario está logueado y para obtener sus datos, lo que es más
 * seguro y robusto.
 *
 * Se mantienen las funciones originales para no romper la compatibilidad con el
 * resto de la aplicación.
 */
class SessionManager(context: Context) {

    private val PREF_NAME = "UserSession"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"
    private val KEY_USER_EMAIL = "userEmail"

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Instancia de Firebase Auth para consultar el estado real de la sesión.
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Guarda el estado de login del usuario.
     * ESTA FUNCIÓN SE MANTIENE IGUAL para no romper tu flujo de login actual.
     * La llamas después de que un usuario inicia sesión exitosamente.
     */
    fun saveLoginState(isLoggedIn: Boolean, userEmail: String? = null) {
        val editor = sharedPrefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.putString(KEY_USER_EMAIL, userEmail)
        editor.apply()
    }

    /**
     * Obtiene el estado de login actual del usuario.
     * MODIFICADO: Ahora consulta directamente a Firebase, que es más fiable.
     * Devuelve true si Firebase tiene un usuario activo.
     */
    fun isLoggedIn(): Boolean {
        // La fuente de verdad más confiable es Firebase.
        return firebaseAuth.currentUser != null
    }

    /**
     * Obtiene el email del usuario logueado.
     * MODIFICADO: Ahora obtiene el email directamente del usuario de Firebase.
     * Esto asegura que siempre tengas el email más actualizado.
     */
    fun getLoggedInUserEmail(): String? {
        // Obtenemos la información directamente del objeto de usuario de Firebase.
        return firebaseAuth.currentUser?.email
    }

    /**
     * NUEVA FUNCIÓN (Recomendada): Obtiene el ID de usuario único (UID) de Firebase.
     * Este es el mejor identificador para usar en la base de datos (Firestore),
     * ya que nunca cambia, a diferencia del email.
     */
    fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    /**
     * Cierra la sesión del usuario.
     * MODIFICADO: Ahora realiza ambas acciones para un logout completo y seguro.
     */
    fun logout() {
        // 1. Cierra la sesión en Firebase. Este es el paso más importante.
        firebaseAuth.signOut()

        // 2. Limpia los datos locales guardados en SharedPreferences.
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }
}

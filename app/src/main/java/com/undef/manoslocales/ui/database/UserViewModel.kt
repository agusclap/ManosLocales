package com.undef.manoslocales.ui.database

import android.app.Application
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.undef.manoslocales.ui.data.AuthManager
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.utils.FileUtils

class UserViewModel(
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val authManager = AuthManager()

    fun registerUser(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        phone: String,
        role: String,
        categoria: String? = null,
        ciudad: String? = null
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                val userMap = mutableMapOf<String, Any>(
                    "email" to email,
                    "password" to password,
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "phone" to phone,
                    "role" to role
                )
                categoria?.let { userMap["categoria"] = it }
                ciudad?.let { userMap["city"] = it.trim().lowercase() }

                firestore.collection("users").document(uid).set(userMap)
            }
    }

    fun uploadUserProfileImage(uri: Uri, onResult: (String?) -> Unit) {
        val context = getApplication<Application>().applicationContext
        val filePath = FileUtils.getPath(context, uri)

        if (filePath != null) {
            MediaManager.get().upload(filePath)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val imageUrl = resultData["secure_url"] as? String
                        val uid = auth.currentUser?.uid
                        if (uid != null && imageUrl != null) {
                            firestore.collection("users")
                                .document(uid)
                                .update("profileImageUrl", imageUrl)
                                .addOnSuccessListener { onResult(imageUrl) }
                                .addOnFailureListener { onResult(null) }
                        } else {
                            onResult(null)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        onResult(null)
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        onResult(null)
                    }
                })
                .dispatch()
        } else {
            onResult(null)
        }
    }

    fun updateUserProfile(updated: User, onComplete: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mutableMapOf<String, Any>(
            "nombre" to updated.nombre,
            "apellido" to updated.apellido,
            "phone" to updated.phone
        )
        updated.role.let { updates["role"] = it }
        updated.categoria?.let { updates["categoria"] = it }
        updated.city?.let { updates["city"] = it.trim().lowercase() }
        if (updated.profileImageUrl.isNotBlank()) updates["profileImageUrl"] = updated.profileImageUrl
        updated.lat?.let { updates["lat"] = it }
        updated.lng?.let { updates["lng"] = it }

        firestore.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener { onComplete() }
    }

    fun fetchUserInfo(onResult: (User?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null)

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = User(
                        nombre = doc.getString("nombre") ?: "",
                        apellido = doc.getString("apellido") ?: "",
                        phone = doc.getString("phone") ?: "",
                        email = doc.getString("email") ?: "",
                        password = "",
                        profileImageUrl = doc.getString("profileImageUrl") ?: "",
                        categoria = doc.getString("categoria"),
                        city = doc.getString("city"),
                        role = doc.getString("role") ?: "",
                        lat = doc.getDouble("lat"),
                        lng = doc.getDouble("lng")
                    )
                    onResult(user)
                } else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }


    fun fetchNearbyProviders(lat: Double, lng: Double, onResult: (List<User>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("role", "provider")
            .get()
            .addOnSuccessListener { result ->
                val nearby = result.documents.mapNotNull { doc ->
                    val userLat = doc.getDouble("lat")
                    val userLng = doc.getDouble("lng")
                    val nombre = doc.getString("nombre") ?: ""
                    val apellido = doc.getString("apellido") ?: ""
                    val phone = doc.getString("phone") ?: ""
                    val email = doc.getString("email") ?: ""
                    val profileImageUrl = doc.getString("profileImageUrl") ?: ""
                    val categoria = doc.getString("categoria")
                    val city = doc.getString("city")
                    val role = doc.getString("role") ?: ""

                    if (userLat != null && userLng != null) {
                        val results = FloatArray(1)
                        Location.distanceBetween(lat, lng, userLat, userLng, results)
                        val distance = results[0]
                        if (distance <= 20000) { // 20 km
                            User(
                                nombre = nombre,
                                apellido = apellido,
                                phone = phone,
                                email = email,
                                profileImageUrl = profileImageUrl,
                                categoria = categoria,
                                city = city,
                                role = role,
                                lat = userLat,
                                lng = userLng
                            )
                        } else null
                    } else null
                }
                onResult(nearby)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }



    fun getUserById(uid: String, onResult: (User?) -> Unit) {
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = User(
                        nombre = doc.getString("nombre") ?: "",
                        apellido = doc.getString("apellido") ?: "",
                        phone = doc.getString("phone") ?: "",
                        email = doc.getString("email") ?: "",
                        password = "",
                        profileImageUrl = doc.getString("profileImageUrl") ?: "",
                        categoria = doc.getString("categoria"),
                        city = doc.getString("city"),
                        role = doc.getString("role") ?: "",
                        lat = doc.getDouble("lat"),
                        lng = doc.getDouble("lng")
                    )
                    onResult(user)
                } else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getUserByEmail(email: String, onResult: (User?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                val doc = result.documents.firstOrNull()
                if (doc != null) {
                    val user = User(
                        nombre = doc.getString("nombre") ?: "",
                        apellido = doc.getString("apellido") ?: "",
                        phone = doc.getString("phone") ?: "",
                        email = doc.getString("email") ?: "",
                        password = "",
                        profileImageUrl = doc.getString("profileImageUrl") ?: "",
                        categoria = doc.getString("categoria"),
                        city = doc.getString("city"),
                        role = doc.getString("role") ?: "",
                        lat = doc.getDouble("lat"),
                        lng = doc.getDouble("lng")
                    )
                    onResult(user)
                } else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun changePassword(currentPassword: String, newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        val email = user?.email ?: return onResult(false, "Usuario no autenticado")

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { onResult(false, "Contraseña actual incorrecta") }
    }

    // Agregá aquí métodos adicionales como productos, login, etc. si los usás en otras pantallas
}

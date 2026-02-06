package com.undef.manoslocales.ui.database

import android.app.Application
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.undef.manoslocales.ui.data.AuthManager
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.data.SettingsManager
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.network.RetrofitClient
import com.undef.manoslocales.ui.utils.EmailManager
import com.undef.manoslocales.utils.FileUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

class UserViewModel(
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val authManager = AuthManager()
    private val settingsManager = SettingsManager(application)

    var loginSuccess = mutableStateOf<Boolean?>(null)
        private set

    var authErrorMessage = mutableStateOf<String?>(null)
        private set

    var currentUser = mutableStateOf<FirebaseUser?>(null)
        private set

    var isUnverified = mutableStateOf(false)
        private set

    // --- PROVINCIAS (Retrofit / GeoRef API) ---
    var provincias = mutableStateOf<List<String>>(emptyList())
        private set

    var isProvinciasLoading = mutableStateOf(false)
        private set

    fun loadProvincias() {
        if (provincias.value.isNotEmpty()) return
        viewModelScope.launch {
            isProvinciasLoading.value = true
            try {
                val response = RetrofitClient.instance.getProvincias()
                provincias.value = listOf("Todas") + response.provincias
                    .map { it.nombre }
                    .sorted()
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error cargando provincias: ${e.message}")
                provincias.value = listOf(
                    "Todas", "Buenos Aires", "CABA", "Catamarca", "Chaco", "Chubut", "Córdoba", 
                    "Corrientes", "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", 
                    "Mendoza", "Misiones", "Neuquén", "Río Negro", "Salta", "San Juan", 
                    "San Luis", "Santa Cruz", "Santa Fe", "Santiago del Estero", 
                    "Tierra del Fuego", "Tucumán"
                )
            } finally {
                isProvinciasLoading.value = false
            }
        }
    }

    /*** AUTH ***/

    fun loginUser(email: String, password: String) {
        loginSuccess.value = null
        authErrorMessage.value = null
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    checkIfVerified(user.uid) { verified ->
                        if (verified) {
                            sessionManager.saveLoginState(true, user.uid)
                            currentUser.value = user
                            loginSuccess.value = true
                            isUnverified.value = false
                        } else {
                            isUnverified.value = true
                            loginSuccess.value = false
                            authErrorMessage.value = "Tu cuenta no ha sido verificada aún."
                        }
                    }
                } else {
                    loginSuccess.value = false
                }
            }
            .addOnFailureListener { e ->
                loginSuccess.value = false
                authErrorMessage.value = e.message
            }
    }

    private fun checkIfVerified(uid: String, onResult: (Boolean) -> Unit) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                onResult(doc.getBoolean("isVerified") ?: false)
            }
            .addOnFailureListener { onResult(false) }
    }

    fun logoutUser() {
        sessionManager.logout()
        auth.signOut()
        currentUser.value = null
        loginSuccess.value = null
    }

    fun isUserLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun getUserRole(onResult: (String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    onResult(doc.getString("role"))
                }
                .addOnFailureListener { onResult(null) }
        } else onResult(null)
    }

    fun getUserById(userId: String, onResult: (User?) -> Unit) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    user?.id = doc.id
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getProviders(onResult: (List<User>) -> Unit) {
        firestore.collection("users").whereEqualTo("role", "provider")
            .get()
            .addOnSuccessListener { snap ->
                viewModelScope.launch {
                    try {
                        val userProvince = withTimeoutOrNull(1000L) {
                            settingsManager.defaultCityFlow.firstOrNull()
                        }?.lowercase() ?: ""

                        val providerList = snap.documents.mapNotNull { doc ->
                            try {
                                val user = doc.toObject(User::class.java)
                                user?.id = doc.id
                                user
                            } catch (e: Exception) {
                                null
                            }
                        }.sortedByDescending { it.city?.lowercase() == userProvince }
                        
                        Log.d("UserViewModel", "Proveedores obtenidos: ${providerList.size}")
                        onResult(providerList)
                    } catch (e: Exception) {
                        Log.e("UserViewModel", "Error procesando proveedores", e)
                        onResult(emptyList())
                    }
                }
            }
            .addOnFailureListener {
                Log.e("UserViewModel", "Error al obtener proveedores de Firestore", it)
                onResult(emptyList())
            }
    }

    fun getProvidersByCategory(category: String, onResult: (List<User>) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("role", "provider")
            .get()
            .addOnSuccessListener { snap ->
                viewModelScope.launch {
                    try {
                        val userProvince = withTimeoutOrNull(1000L) {
                            settingsManager.defaultCityFlow.firstOrNull()
                        }?.lowercase() ?: ""

                        val list = snap.documents.mapNotNull { doc ->
                            val user = doc.toObject(User::class.java)
                            user?.id = doc.id
                            user
                        }.filter { 
                            it.categoria?.equals(category, ignoreCase = true) == true 
                        }.sortedByDescending { it.city?.lowercase() == userProvince }
                        
                        onResult(list)
                    } catch (e: Exception) {
                        Log.e("UserViewModel", "Error procesando proveedores por categoría", e)
                        onResult(emptyList())
                    }
                }
            }
            .addOnFailureListener { 
                Log.e("UserViewModel", "Error al obtener proveedores por categoría", it)
                onResult(emptyList()) 
            }
    }

    fun getProviderIdsByName(name: String, onResult: (List<String>) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("role", "provider")
            .get()
            .addOnSuccessListener { snap ->
                val ids = snap.documents.filter { doc ->
                    val nombre = doc.getString("nombre") ?: ""
                    val apellido = doc.getString("apellido") ?: ""
                    val fullName = "$nombre $apellido"
                    fullName.contains(name, ignoreCase = true)
                }.map { it.id }
                onResult(ids)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    /*** EMAILJS VERIFICATION FLOW ***/

    fun registerUserWithVerification(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        phone: String,
        role: String,
        categoria: String? = null,
        ciudad: String? = null,
        lat: Double? = null,
        lng: Double? = null,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                val code = EmailManager.generateCode()

                val userMap = mutableMapOf<String, Any>(
                    "email" to email,
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "phone" to phone,
                    "role" to role,
                    "isVerified" to false,
                    "verificationCode" to code
                )

                categoria?.let { userMap["categoria"] = it }
                ciudad?.let { userMap["city"] = it.trim().lowercase() }

                if (role == "provider") {
                    userMap["lat"] = lat ?: 0.0
                    userMap["lng"] = lng ?: 0.0
                }

                firestore.collection("users").document(uid).set(userMap)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            val sent = EmailManager.sendVerificationEmail(email, code)
                            if (sent) {
                                onComplete(true, "¡Código enviado! Revisa tu casilla de correo")
                            } else {
                                onComplete(true, "Usuario creado, pero hubo un error al enviar el código.")
                            }
                        }
                    }
                    .addOnFailureListener { e -> onComplete(false, e.message) }
            }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun verifyCode(uid: String, code: String, onResult: (Boolean, String?) -> Unit) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val savedCode = doc.getString("verificationCode")
                if (savedCode == code) {
                    firestore.collection("users").document(uid).update("isVerified", true)
                        .addOnSuccessListener { onResult(true, "Verificación exitosa") }
                        .addOnFailureListener { e -> onResult(false, e.message) }
                } else {
                    onResult(false, "Código incorrecto")
                }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun sendResetCode(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val snap = withTimeout(5000L) {
                    firestore.collection("users")
                        .whereEqualTo("email", email)
                        .limit(1)
                        .get()
                        .await()
                }

                if (snap.isEmpty) {
                    onResult(false, "El correo no está registrado")
                } else {
                    val uid = snap.documents[0].id
                    val code = EmailManager.generateCode()
                    
                    withTimeout(5000L) {
                        firestore.collection("users").document(uid)
                            .update("verificationCode", code)
                            .await()
                    }

                    Log.d("EmailJS_Status", "Intentando enviar correo...")
                    val sent = EmailManager.sendResetPasswordEmail(email, code)
                    if (sent) {
                        onResult(true, "¡Código enviado! Revisa tu casilla de correo")
                    } else {
                        onResult(false, "Error al enviar el correo")
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("UserViewModel", "Timeout en recuperación de contraseña")
                onResult(false, "Tiempo de espera agotado. Revisa tu conexión.")
            } catch (e: FirebaseFirestoreException) {
                Log.e("UserViewModel", "Error de Firestore: ${e.code}")
                onResult(false, "Error de base de datos: ${e.message}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error inesperado", e)
                onResult(false, "Ocurrió un error inesperado: ${e.message}")
            }
        }
    }

    fun validateResetCodeAndSendEmail(email: String, code: String, onResult: (Boolean, String?) -> Unit) {
        firestore.collection("users").whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    onResult(false, "Error al validar el usuario")
                    return@addOnSuccessListener
                }
                val doc = snap.documents[0]
                if (doc.getString("verificationCode") == code) {
                    auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            onResult(true, "¡Identidad confirmada! Revisa tu email para el último paso de seguridad")
                        }
                        .addOnFailureListener { e ->
                            onResult(false, "Error al enviar email de reset: ${e.message}")
                        }
                } else {
                    onResult(false, "Código incorrecto")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun resetPasswordWithCode(email: String, code: String, newPass: String, onResult: (Boolean, String?) -> Unit) {
        firestore.collection("users").whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) return@addOnSuccessListener onResult(false, "Error")
                val doc = snap.documents[0]
                if (doc.getString("verificationCode") == code) {
                    val user = auth.currentUser
                    if (user != null && user.email == email) {
                        user.updatePassword(newPass)
                            .addOnSuccessListener { onResult(true, "Contraseña actualizada") }
                            .addOnFailureListener { e -> onResult(false, e.message) }
                    } else {
                        onResult(false, "Se requiere sesión activa para cambiar contraseña por seguridad.")
                    }
                } else {
                    onResult(false, "Código inválido")
                }
            }
    }

    fun changePassword(currentPassword: String, newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email == null) return onResult(false, "Usuario no autenticado")

        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e -> onResult(false, "Contraseña actual incorrecta") }
    }

    fun uploadProductImage(uri: Uri, onResult: (String?) -> Unit) {
        val p = FileUtils.getPath(getApplication(), uri) ?: return onResult(null)
        MediaManager.get().upload(p)
            .callback(object : UploadCallback {
                override fun onStart(id: String?) {}
                override fun onProgress(id: String?, b: Long, t: Long) {}
                override fun onSuccess(id: String?, data: Map<*, *>) {
                    val u = data["secure_url"] as? String
                    onResult(u)
                }
                override fun onError(id: String?, err: ErrorInfo?) { onResult(null) }
                override fun onReschedule(id: String?, err: ErrorInfo?) { onResult(null) }
            })
            .dispatch()
    }

    fun uploadUserProfileImage(uri: Uri, onResult: (String?) -> Unit) {
        uploadProductImage(uri, onResult)
    }

    fun createProduct(name: String, description: String, price: Double, imageUrl: String, category: String, city: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser ?: return onResult(false, "No autenticado")
        val p = hashMapOf(
            "name" to name, "description" to description, "price" to price,
            "imageUrl" to imageUrl, "providerId" to user.uid,
            "createdAt" to System.currentTimeMillis(), "category" to category,
            "city" to city.trim().lowercase()
        )
        firestore.collection("products")
            .add(p)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getMyProducts(onResult: (List<Product>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(emptyList())
        firestore.collection("products")
            .whereEqualTo("providerId", uid)
            .get()
            .addOnSuccessListener { snap ->
                onResult(snap.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) })
            }
    }

    fun getProductsByProvider(providerId: String, onResult: (List<Product>) -> Unit) {
        firestore.collection("products")
            .whereEqualTo("providerId", providerId)
            .get()
            .addOnSuccessListener { snap ->
                onResult(snap.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) })
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun deleteProduct(productId: String, onResult: (Boolean, String?) -> Unit) {
        firestore.collection("products").document(productId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getProductById(productId: String, onResult: (Product?) -> Unit) {
        firestore.collection("products").document(productId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    onResult(doc.toObject(Product::class.java)?.copy(id = doc.id))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    fun updateProduct(product: Product, onResult: (Boolean, String?) -> Unit) {
        val p = hashMapOf(
            "name" to product.name,
            "description" to product.description,
            "price" to product.price,
            "imageUrl" to product.imageUrl,
            "category" to product.category,
            "city" to product.city.trim().lowercase()
        )
        firestore.collection("products").document(product.id)
            .update(p as Map<String, Any>)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getFilteredProducts(categoria: String?, ciudad: String?, proveedorId: String?, onComplete: (List<Product>) -> Unit) {
        firestore.collection("products").get()
            .addOnSuccessListener { snap ->
                viewModelScope.launch {
                    try {
                        val userProvince = withTimeoutOrNull(1000L) {
                            settingsManager.defaultCityFlow.firstOrNull()
                        }?.lowercase() ?: ""

                        var list = snap.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
                        
                        if (categoria != null && categoria != "Todas") {
                            list = list.filter { it.category.equals(categoria, ignoreCase = true) }
                        }
                        if (!ciudad.isNullOrBlank()) {
                            list = list.filter { it.city.equals(ciudad, ignoreCase = true) }
                        }
                        if (!proveedorId.isNullOrBlank()) {
                            list = list.filter { it.providerId == proveedorId }
                        }
                        
                        list = list.sortedByDescending { it.city.lowercase() == userProvince }
                        onComplete(list)
                    } catch (e: Exception) {
                        onComplete(emptyList())
                    }
                }
            }
            .addOnFailureListener { onComplete(emptyList()) }
    }

    fun fetchUserInfo(onResult: (User?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null)
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    user?.id = doc.id
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    fun updateUserProfile(user: User, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false)
        val userMap = mutableMapOf<String, Any>(
            "nombre" to user.nombre,
            "apellido" to user.apellido,
            "phone" to user.phone,
            "city" to user.city?.trim()?.lowercase().orEmpty(),
            "categoria" to user.categoria.orEmpty(),
            "profileImageUrl" to user.profileImageUrl,
            "lat" to user.lat,
            "lng" to user.lng
        )
        firestore.collection("users").document(uid)
            .update(userMap)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun fetchNearbyProviders(lat: Double, lng: Double, onResult: (List<User>) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("role", "provider")
            .get()
            .addOnSuccessListener { snap ->
                val nearby = snap.documents.mapNotNull { doc ->
                    val ulat = doc.getDouble("lat")
                    val ulng = doc.getDouble("lng")
                    if (ulat != null && ulng != null && (ulat != 0.0 || ulng != 0.0)) {
                        val distance = FloatArray(1)
                        Location.distanceBetween(lat, lng, ulat, ulng, distance)
                        if (distance[0] <= 50000) { // Aumentado a 50km para pruebas
                            val user = doc.toObject(User::class.java)
                            user?.id = doc.id
                            user
                        } else null
                    } else null
                }
                Log.d("UserViewModel", "Proveedores cercanos encontrados: ${nearby.size}")
                onResult(nearby)
            }
            .addOnFailureListener { 
                Log.e("UserViewModel", "Error al buscar proveedores cercanos", it)
                onResult(emptyList()) 
            }
    }

    val defaultCityFlow = settingsManager.defaultCityFlow
}

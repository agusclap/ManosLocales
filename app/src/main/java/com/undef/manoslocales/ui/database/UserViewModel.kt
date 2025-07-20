package com.undef.manoslocales.ui.database

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.undef.manoslocales.ui.data.AuthManager
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.dataclasses.Product
import java.util.UUID
import com.undef.manoslocales.utils.FileUtils



class UserViewModel(
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val authManager = AuthManager()

    var loginSuccess = mutableStateOf<Boolean?>(null)
        private set

    var authErrorMessage = mutableStateOf<String?>(null)
        private set

    var currentUser = mutableStateOf<FirebaseUser?>(null)
        private set

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
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                val userMap = mutableMapOf(
                    "email" to email,
                    "password" to password,
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "phone" to phone,
                    "role" to role
                )

                if (role == "provider") {
                    categoria?.let { userMap["categoria"] = it }
                    ciudad?.let { userMap["city"] = it.trim().lowercase() }
                }

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(userMap)
            }
            .addOnFailureListener {
                // manejar error
            }
    }

    fun uploadProductImage(uri: Uri, onResult: (String?) -> Unit) {
        val context = getApplication<Application>().applicationContext
        val filePath = FileUtils.getPath(context, uri)

        if (filePath != null) {
            MediaManager.get().upload(filePath)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val imageUrl = resultData["secure_url"] as? String
                        onResult(imageUrl)
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
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null && imageUrl != null) {
                            FirebaseFirestore.getInstance()
                                .collection("users")
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


    fun loginUser(email: String, password: String) {
        authManager.loginUser(email, password) { success, user, error ->
            if (success && user != null) {
                sessionManager.saveLoginState(true, email)
                currentUser.value = user
            }
            loginSuccess.value = success
            authErrorMessage.value = error
        }
    }

    fun logoutUser() {
        sessionManager.logout()
        currentUser.value = null
    }

    fun isUserLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun getUserRole(onResult: (String?) -> Unit) {
        val uid = authManager.getCurrentUser()?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    onResult(doc.getString("role"))
                }
                .addOnFailureListener { onResult(null) }
        } else {
            onResult(null)
        }
    }

    fun getMyProducts(onResult: (List<Product>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(emptyList())
        FirebaseFirestore.getInstance()
            .collection("products")
            .whereEqualTo("providerId", uid)
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
                onResult(list)
            }
    }

    fun deleteProduct(productId: String, onResult: (Boolean, String?) -> Unit) {
        FirebaseFirestore.getInstance().collection("products")
            .document(productId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun createProduct(
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
        category: String,
        city: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val normalizedCity = city.trim().lowercase()

        val product = hashMapOf(
            "name" to name,
            "description" to description,
            "price" to price,
            "imageUrl" to imageUrl,
            "providerId" to currentUser.uid,
            "createdAt" to System.currentTimeMillis(),
            "category" to category,
            "city" to normalizedCity
        )

        FirebaseFirestore.getInstance()
            .collection("products")
            .add(product)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }


    fun fetchUserInfo(onResult: (User?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(null)

        FirebaseFirestore.getInstance().collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = User(
                        nombre = doc.getString("nombre") ?: "",
                        apellido = doc.getString("apellido") ?: "",
                        phone = doc.getString("phone") ?: "",
                        email = doc.getString("email") ?: "",
                        password = "", // no guardar
                        profileImageUrl = doc.getString("profileImageUrl") ?: "",
                        categoria = doc.getString("categoria"),
                        city = doc.getString("city"),
                        role = doc.getString("role") ?: ""
                    )
                    onResult(user)
                } else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getUserByEmail(email: String, onResult: (User?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()
                if (doc != null) {
                    val user = User(
                        nombre = doc.getString("nombre") ?: "",
                        apellido = doc.getString("apellido") ?: "",
                        phone = doc.getString("phone") ?: "",
                        email = doc.getString("email") ?: "",
                        password = doc.getString("password") ?: "",
                        profileImageUrl = doc.getString("profileImageUrl") ?: "",
                        categoria = doc.getString("categoria"),
                        city = doc.getString("city"),
                        role = doc.getString("role") ?: ""
                    )
                    onResult(user)
                } else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getProductById(productId: String, onResult: (Product?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.toObject(Product::class.java)?.copy(id = doc.id))
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getProductsByProvider(providerId: String, onResult: (List<Product>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("products")
            .whereEqualTo("providerId", providerId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
                onResult(list)
            }
    }

    fun getProviderIdsByName(query: String, onResult: (List<String>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("role", "provider")
            .get()
            .addOnSuccessListener { result ->
                val ids = result.documents.mapNotNull { doc ->
                    val nombre = doc.getString("nombre") ?: ""
                    val apellido = doc.getString("apellido") ?: ""
                    val fullName = "$nombre $apellido"
                    if (fullName.contains(query, ignoreCase = true)) doc.id else null
                }
                onResult(ids)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun updateProduct(product: Product, onResult: (Boolean, String?) -> Unit) {
        val data = mapOf(
            "name" to product.name,
            "description" to product.description,
            "price" to product.price,
            "category" to product.category,
            "city" to product.city
        )

        FirebaseFirestore.getInstance()
            .collection("products")
            .document(product.id)
            .update(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun updateUserProfile(updated: User, onComplete: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updates = mutableMapOf<String, Any>(
            "nombre" to updated.nombre,
            "apellido" to updated.apellido,
            "phone" to updated.phone
        )

        updated.role.let { updates["role"] = it } // solo si está presente
        updated.categoria?.let { updates["categoria"] = it }
        updated.city?.let { updates["city"] = it.trim().lowercase() }
        if (updated.profileImageUrl.isNotBlank()) {
            updates["profileImageUrl"] = updated.profileImageUrl
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(updates)
            .addOnSuccessListener { onComplete() }
    }



    fun getFilteredProducts(
        categoria: String?,
        ciudad: String?,
        proveedorId: String?,
        onComplete: (List<Product>) -> Unit
    ) {
        var query: Query = FirebaseFirestore.getInstance().collection("products")

        categoria?.takeIf { it != "Todas" }?.let {
            query = query.whereEqualTo("category", it)
        }

        ciudad?.takeIf { it.isNotBlank() }?.let {
            query = query.whereEqualTo("city", it.trim().lowercase())
        }

        proveedorId?.takeIf { it.isNotBlank() }?.let {
            query = query.whereEqualTo("providerId", it)
        }

        query.get().addOnSuccessListener { result ->
            val products = result.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
            onComplete(products)
        }.addOnFailureListener { onComplete(emptyList()) }
    }

    fun getProducts(onResult: (List<Product>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("products")
            .get()
            .addOnSuccessListener { result ->
                val products = result.documents.mapNotNull { doc ->
                    val name = doc.getString("name")
                    val description = doc.getString("description")
                    val price = doc.getDouble("price")
                    val imageUrl = doc.getString("imageUrl")
                    val providerId = doc.getString("providerId")
                    val createdAt = doc.getLong("createdAt")
                    val category = doc.getString("category") ?: "Sin categoría"
                    val city = doc.getString("city") ?: ""
                    if (name != null && description != null && price != null && imageUrl != null && providerId != null) {
                        Product(
                            id = doc.id,
                            name = name,
                            description = description,
                            price = price,
                            imageUrl = imageUrl,
                            providerId = providerId,
                            createdAt = createdAt ?: 0L,
                            category = category,
                            city = city
                        )
                    } else null
                }
                onResult(products)
            }
    }

    fun changePassword(currentPassword: String, newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (user == null || email == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e -> onResult(false, "Contraseña actual incorrecta") }
    }


    fun getUserById(uid: String, onResult: (User?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = User(
                        nombre = doc.getString("nombre") ?: "",
                        apellido = doc.getString("apellido") ?: "",
                        phone = doc.getString("phone") ?: "",
                        email = doc.getString("email") ?: "",
                        password = "", // nunca guardar esto en cliente
                        profileImageUrl = doc.getString("profileImageUrl") ?: "",
                        categoria = doc.getString("categoria"),
                        city = doc.getString("city"),
                        role = doc.getString("role") ?: ""
                    )
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }


}

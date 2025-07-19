package com.undef.manoslocales.ui.database

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.undef.manoslocales.ui.data.AuthManager
import com.undef.manoslocales.ui.data.ProductManager
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.dataclasses.Product
import java.util.UUID

class UserViewModel(
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val authManager = AuthManager()
    val productManager = ProductManager()

    var registrationSuccess = mutableStateOf<Boolean?>(null)
        private set

    var loginSuccess = mutableStateOf<Boolean?>(null)
        private set

    var authErrorMessage = mutableStateOf<String?>(null)
        private set

    var currentUser = mutableStateOf<FirebaseUser?>(null)
        private set

    // ✅ Registro de usuario con teléfono
    fun registerUser(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        phone: String,
        role: String,
        categoria: String? = null
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

                // solo agrega categoría si es proveedor
                if (role == "provider" && categoria != null) {
                    userMap["categoria"] = categoria
                }

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(userMap)
            }
            .addOnFailureListener {
                // manejar error de registro
            }
    }


    // 🔑 Login
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

    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    fun getLoggedInUserEmail(): String? {
        return sessionManager.getLoggedInUserEmail()
    }

    fun getUserRole(onResult: (String?) -> Unit) {
        val uid = authManager.getCurrentUser()?.uid
        if (uid != null) {
            Firebase.firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    onResult(role)
                }
                .addOnFailureListener {
                    onResult(null)
                }
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
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id) // asumí que Product incluye campo 'id'
                }
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
        onResult: (Boolean, String?) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onResult(false, "Usuario no autenticado")
            return
        }

        val product = hashMapOf(
            "name" to name,
            "description" to description,
            "price" to price,
            "imageUrl" to imageUrl,
            "providerId" to currentUser.uid,
            "createdAt" to System.currentTimeMillis(),
            "category" to category
        )

        FirebaseFirestore.getInstance()
            .collection("products")
            .add(product)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }



    fun uploadProductImage(imageUri: Uri, onResult: (String?) -> Unit) {
        val context = getApplication<Application>().applicationContext
        val fileName = "products/${UUID.randomUUID()}.jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child(fileName)

        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()

            if (bytes != null) {
                storageRef.putBytes(bytes)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            onResult(uri.toString())
                        }.addOnFailureListener {
                            onResult(null)
                        }
                    }
                    .addOnFailureListener {
                        onResult(null)
                    }
            } else {
                onResult(null)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            onResult(null)
        }
    }

    fun fetchUserInfo(onResult: (User?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(null)

        FirebaseFirestore.getInstance().collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fullName = document.getString("name") ?: ""
                    val parts = fullName.split(" ")

                    val nombre = parts.getOrNull(0) ?: ""
                    val apellido = parts.drop(1).joinToString(" ")

                    val user = User(
                        nombre = nombre,
                        apellido = apellido,
                        phone = document.getString("phone") ?: "",
                        email = document.getString("email") ?: "",
                        password = "", // No se guarda la contraseña en Firestore
                        profileImageUrl = document.getString("profileImageUrl") ?: "",
                        categoria = document.getString("categoria") ?: "",
                        role = document.getString("rol") ?: ""
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


    fun getProductById(productId: String, onResult: (Product?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener { doc ->
                val product = doc.toObject(Product::class.java)?.copy(id = doc.id)
                onResult(product)
            }
            .addOnFailureListener {
                onResult(null)
            }
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
                        categoria = doc.getString("categoria") ?: "",
                        role = doc.getString("role") ?: ""
                    )
                    onResult(user)
                } else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }


    fun getProductsByProvider(providerId: String, onResult: (List<Product>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("products")
            .whereEqualTo("providerId", providerId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                onResult(list)
            }
    }

    fun updateProduct(product: Product, onResult: (Boolean, String?) -> Unit) {
        val data = mapOf(
            "name" to product.name,
            "description" to product.description,
            "price" to product.price,
            "category" to product.category
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
        val updates = mapOf(
            "nombre" to updated.nombre,
            "apellido" to updated.apellido,
            "phone" to updated.phone,
            "categoria" to updated.categoria
        )
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(updates)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { /* manejar error si querés */ }
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

                    if (name != null && description != null && price != null && imageUrl != null && providerId != null) {
                        Product(
                            id = doc.id,
                            name = name,
                            description = description,
                            price = price,
                            imageUrl = imageUrl,
                            providerId = providerId,
                            createdAt = createdAt ?: 0L,
                            category = category
                        )

                    } else null
                }
                onResult(products)
            }
    }


}

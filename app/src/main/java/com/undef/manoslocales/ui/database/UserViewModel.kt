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
    fun registerUser(email: String, password: String, name: String, role: String, phone: String) {
        authManager.registerUser(email, password, name, role, phone) { success, error ->
            if (success) {
                sessionManager.saveLoginState(true, email)
            }
            registrationSuccess.value = success
            authErrorMessage.value = error
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

    fun createProduct(
        name: String,
        description: String,
        price: Double,
        imageUrl: String,
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
            "createdAt" to System.currentTimeMillis()
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
        val fileName = "products/${UUID.randomUUID()}.jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child(fileName)

        storageRef.putFile(imageUri)
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

                    if (name != null && description != null && price != null && imageUrl != null && providerId != null) {
                        Product(name, description, price, imageUrl, providerId, createdAt ?: 0L)
                    } else null
                }
                onResult(products)
            }
    }

}

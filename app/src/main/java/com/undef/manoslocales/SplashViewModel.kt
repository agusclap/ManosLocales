package com.undef.manoslocales

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.undef.manoslocales.ui.data.SessionManager
import com.undef.manoslocales.ui.dataclasses.Product
import com.undef.manoslocales.ui.database.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val isLoggedIn: Boolean) : UiState()
        data class Error(val message: String, val canContinue: Boolean) : UiState()
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun startInitialLoad() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val sessionManager = SessionManager(getApplication())
            val isLoggedIn = sessionManager.isLoggedIn()
            if (!isLoggedIn) {
                _uiState.value = UiState.Success(isLoggedIn = false)
                return@launch
            }
            val result = runCatching {
                withTimeout(8_000L) {
                    val products = async { fetchProducts() }
                    val providers = async { fetchProviders() }
                    products.await()
                    providers.await()
                }
            }
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(isLoggedIn) },
                onFailure = {
                    UiState.Error(
                        message = "No pudimos cargar los datos iniciales. Reintentá.",
                        canContinue = true
                    )
                }
            )
        }
    }

    private suspend fun fetchProducts(): List<Product> =
        suspendCancellableCoroutine { continuation ->
            firestore.collection("products")
                .get()
                .addOnSuccessListener { snapshot ->
                    val items = snapshot.documents.mapNotNull {
                        it.toObject(Product::class.java)?.copy(id = it.id)
                    }
                    if (continuation.isActive) {
                        continuation.resume(items)
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
        }

    private suspend fun fetchProviders(): List<User> =
        suspendCancellableCoroutine { continuation ->
            firestore.collection("users")
                .whereEqualTo("role", "provider")
                .get()
                .addOnSuccessListener { snapshot ->
                    val items = snapshot.documents.mapNotNull {
                        it.toObject(User::class.java)?.copy(id = it.id)
                    }
                    if (continuation.isActive) {
                        continuation.resume(items)
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
        }
}

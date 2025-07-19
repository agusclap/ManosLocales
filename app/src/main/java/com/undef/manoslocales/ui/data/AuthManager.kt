package com.undef.manoslocales.ui.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


//Maneja logica de firebase authentication
class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun registerUser(
        email: String,
        password: String,
        name: String,
        role: String,
        phone: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val userMap = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "role" to role,
                        "phone" to phone
                    )
                    firestore.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener { onComplete(true, null) }
                        .addOnFailureListener { e -> onComplete(false, e.message) }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onComplete: (Boolean, FirebaseUser?, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, auth.currentUser, null)
                } else {
                    onComplete(false, null, task.exception?.message)
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}

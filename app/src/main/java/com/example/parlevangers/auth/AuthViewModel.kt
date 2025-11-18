// AuthViewModel.kt
package com.example.parlevangers.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Authentication ViewModel for ParleVangers
 * Handles email/password and Google authentication and user persistence.
 */
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onResult(false)
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                if (user == null) {
                    onResult(false)
                    return@addOnCompleteListener
                }

                saveUserToFirestore(
                    uid = user.uid,
                    email = email,
                    onResult = onResult
                )
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onResult(false)
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                if (user == null) {
                    onResult(false)
                    return@addOnCompleteListener
                }

                val email = user.email ?: "no-email-${user.uid}@example.com"

                saveUserToFirestore(
                    uid = user.uid,
                    email = email,
                    onResult = onResult
                )
            }
    }

    private fun saveUserToFirestore(
        uid: String,
        email: String,
        onResult: (Boolean) -> Unit
    ) {
        val userData = mapOf(
            "userId" to uid,
            "email" to email,
            "username" to email.substringBefore("@"),
            "selectedBackground" to "default",
            "createdAt" to FieldValue.serverTimestamp()
        )

        // Match the existing structure: users / {uid} / users / {uid}
        firestore.collection("users")
            .document(uid)
            .collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}

package com.santosh.moviefirebaseapp.viewmodel

// AuthViewModel.kt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // LiveData to observe authentication state
    val authState = MutableLiveData<Boolean>()  // true=logged in, false=logged out
    val errorMessage = MutableLiveData<String>()  // For error messages

    // Register a new user
    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authState.value = true  // Registration + auto-login success
                } else {
                    errorMessage.value = task.exception?.message ?: "Registration failed"
                }
            }
    }

    // Login existing user
    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authState.value = true
                } else {
                    errorMessage.value = task.exception?.message ?: "Login failed"
                }
            }
    }

    // Logout
    fun logout() {
        auth.signOut()
        authState.value = false
    }
}
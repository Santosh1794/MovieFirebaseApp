package com.santosh.moviefirebaseapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.santosh.moviefirebaseapp.R

class LoginActivity : AppCompatActivity() {
    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Initialize UI components
        val emailField: EditText = findViewById(R.id.email_edit)
        val passwordField: EditText = findViewById(R.id.password_edit)
        val loginButton: Button = findViewById(R.id.login_button)
        val registerButton: Button = findViewById(R.id.register_button)

        // Check if user is already logged in
        if (auth.currentUser != null) {
            navigateToMovieList()
        }

        // Handle Login button click
        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            // Check for empty fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Attempt to log in with Firebase Auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    //successful login go to movie list
                    if (task.isSuccessful) {
                        navigateToMovieList()
                    } else {
                        //failed login show error message
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Redirect to RegisterActivity when Register button is clicked
        registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Navigate to Movie List Activity
    private fun navigateToMovieList() {
        val intent = Intent(this, MovieListActivity::class.java)
        startActivity(intent)
    }
}

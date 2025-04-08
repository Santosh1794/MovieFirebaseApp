package com.santosh.moviefirebaseapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.santosh.moviefirebaseapp.R
import com.santosh.moviefirebaseapp.model.Movie
import com.santosh.moviefirebaseapp.view.adapter.MovieAdapter

class MovieListActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        val userEmailTextView = findViewById<TextView>(R.id.user_email_textview)
        userEmailTextView.text = auth.currentUser?.email ?: "Guest"


        // Initialize views
        refreshButton = findViewById(R.id.refresh_button)
        recyclerView = findViewById(R.id.movie_recycler_view)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        movieAdapter = MovieAdapter(emptyList())
        recyclerView.adapter = movieAdapter
        setupAdapterClickListeners() // Set click listeners once

        // Set button click listeners
        refreshButton.setOnClickListener { refreshMovieList() }

        findViewById<Button>(R.id.add_movie_button).setOnClickListener {
            startActivity(Intent(this, AddEditMovieActivity::class.java))
        }

        findViewById<Button>(R.id.logout_button).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Load initial data
        refreshMovieList()
    }

    private fun refreshMovieList() {
        db.collection("movies").get()
            .addOnSuccessListener { querySnapshot ->
                val movies = querySnapshot.documents.map { doc ->
                    doc.toObject(Movie::class.java)!!.copy(id = doc.id)
                }
                movieAdapter.submitList(movies) // Update existing adapter's data
                Toast.makeText(this, "List refreshed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Refresh failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupAdapterClickListeners() {
        movieAdapter.onEditClick = { movie ->
            Intent(this, AddEditMovieActivity::class.java).apply {
                putExtra("movie_id", movie.id)
                startActivity(this)
            }
        }

        movieAdapter.onDeleteClick = { movie ->
            db.collection("movies").document(movie.id).delete()
                .addOnSuccessListener {
                    refreshMovieList() // Refresh after delete
                    Toast.makeText(this, "Movie deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error deleting movie", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
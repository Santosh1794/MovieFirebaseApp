package com.santosh.moviefirebaseapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.movie_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add Movie Button
        val addMovieButton: Button = findViewById(R.id.add_movie_button)
        addMovieButton.setOnClickListener {
            val intent = Intent(this@MovieListActivity, AddEditMovieActivity::class.java)
            startActivity(intent)
        }

        // Logout Button
        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this@MovieListActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close MovieListActivity
        }

        // Fetch movies from Firestore
        db.collection("movies").get()
            .addOnSuccessListener { querySnapshot ->
                val movies = querySnapshot.toObjects(Movie::class.java)
                movieAdapter = MovieAdapter(movies)
                recyclerView.adapter = movieAdapter
            }
            .addOnFailureListener {
                Toast.makeText(this@MovieListActivity, "Error loading movies", Toast.LENGTH_SHORT).show()
            }

        // Handle Movie Adapter Item Clicks for Edit/Delete
        movieAdapter.onEditClick = { movie ->
            val intent = Intent(this@MovieListActivity, AddEditMovieActivity::class.java)
            intent.putExtra("movie_id", movie.id)  // Pass the movie ID to edit
            startActivity(intent)
        }

        movieAdapter.onDeleteClick = { movie ->
            db.collection("movies").document(movie.id.toString()).delete()
                .addOnSuccessListener {
                    Toast.makeText(this@MovieListActivity, "Movie deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this@MovieListActivity, "Error deleting movie", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

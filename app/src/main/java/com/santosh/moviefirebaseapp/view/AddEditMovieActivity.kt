package com.santosh.moviefirebaseapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.santosh.moviefirebaseapp.R
import com.santosh.moviefirebaseapp.model.Movie

class AddEditMovieActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var titleField: EditText
    private lateinit var studioField: EditText
    private lateinit var ratingField: EditText
    private lateinit var thumbnailField: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var pageTitle: TextView // Added for dynamic title
    private var movieId: String? = null
    private var isEditMode = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_movie)

        // Initialize views
        pageTitle = findViewById(R.id.page_title)
        titleField = findViewById(R.id.movie_title_edit)
        studioField = findViewById(R.id.movie_studio_edit)
        ratingField = findViewById(R.id.movie_rating_edit)
        thumbnailField = findViewById(R.id.movie_thumbnail_edit)
        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)

        // Check if we're in edit mode
        movieId = intent.getStringExtra("movie_id")
        isEditMode = movieId != null

        // Set UI based on mode
        if (isEditMode) {
            pageTitle.text = "Edit Movie"
            saveButton.text = "Update"

            // Load movie data for editing
            db.collection("movies").document(movieId!!).get()
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.toObject(Movie::class.java)?.let { movie ->
                        titleField.setText(movie.title)
                        studioField.setText(movie.studio)
                        ratingField.setText(movie.rating.toString())
                        thumbnailField.setText(movie.thumbnailUrl)
                    }
                }
        } else {
            pageTitle.text = "Add Movie"
            saveButton.text = "Save"
        }

        cancelButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            saveMovie()
        }
    }

    private fun saveMovie() {
        val title = titleField.text.toString().trim()
        val studio = studioField.text.toString().trim()
        val rating = ratingField.text.toString().toDoubleOrNull()
        val thumbnail = thumbnailField.text.toString().trim()

        if (title.isEmpty() || studio.isEmpty() || rating == null) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val movie = Movie(
            id = movieId ?: "",
            title = title,
            studio = studio,
            rating = rating,
            thumbnailUrl = thumbnail
        )

        if (isEditMode) {
            // Update existing movie
            db.collection("movies").document(movieId!!).set(movie)
                .addOnSuccessListener {
                    Toast.makeText(this, "Movie updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update movie", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Add new movie
            db.collection("movies").add(movie)
                .addOnSuccessListener {
                    Toast.makeText(this, "Movie added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add movie", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
package com.santosh.moviefirebaseapp.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
    private lateinit var saveButton: Button
    private var movieId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_movie)

        titleField = findViewById(R.id.movie_title_edit)
        studioField = findViewById(R.id.movie_studio_edit)
        ratingField = findViewById(R.id.movie_rating_edit)
        saveButton = findViewById(R.id.save_button)

        movieId = intent.getStringExtra("movie_id")

        if (movieId != null) {
            // Fetch movie data for editing
            db.collection("movies").document(movieId!!).get()
                .addOnSuccessListener { documentSnapshot ->
                    val movie = documentSnapshot.toObject(Movie::class.java)
                    if (movie != null) {
                        titleField.setText(movie.title)
                        studioField.setText(movie.studio)
                        ratingField.setText(movie.rating.toString())
                    }
                }
        }

        saveButton.setOnClickListener {
            val title = titleField.text.toString()
            val studio = studioField.text.toString()
            val rating = ratingField.text.toString().toIntOrNull()

            if (rating != null) {
                val movie = Movie(
                    title, studio, rating.toString(),
                    rating = TODO()
                )
                if (movieId == null) {
                    // Add new movie
                    db.collection("movies").add(movie)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Movie Added", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error adding movie", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Update existing movie
                    db.collection("movies").document(movieId!!).set(movie)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Movie Updated", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error updating movie", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Invalid rating", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.santosh.moviefirebaseapp.view

import android.annotation.SuppressLint
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
    private lateinit var thumbnailField: EditText   // ✅ Added
    private lateinit var saveButton: Button
    private var movieId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_movie)

        titleField = findViewById(R.id.movie_title_edit)
        studioField = findViewById(R.id.movie_studio_edit)
        ratingField = findViewById(R.id.movie_rating_edit)
        thumbnailField = findViewById(R.id.movie_thumbnail_edit) // ✅ Initialized
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
                        thumbnailField.setText(movie.thumbnailUrl) // ✅ Fixed
                    }
                }
        }

        saveButton.setOnClickListener {
            val title = titleField.text.toString().trim()
            val studio = studioField.text.toString().trim()
            val rating = ratingField.text.toString().toDoubleOrNull()
            val thumbnail = thumbnailField.text.toString().trim()

            if (title.isNotEmpty() && studio.isNotEmpty() && rating != null) {
                val movie = Movie(
                    id = movieId ?: "",
                    title = title,
                    studio = studio,
                    rating = rating,
                    thumbnailUrl = thumbnail
                )

                if (movieId == null) {
                    db.collection("movies").add(movie)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Movie added", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add movie", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    db.collection("movies").document(movieId!!).set(movie)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Movie updated", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update movie", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.santosh.moviefirebaseapp.viewmodel

// MovieViewModel.kt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.santosh.moviefirebaseapp.model.Movie

class MovieViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val moviesCollection = db.collection("movies")

    // LiveData to observe the list of movies
    val movieList = MutableLiveData<List<Movie>>()

    val errorMessage = MutableLiveData<String>()

    // Fetch all movies
    fun fetchMovies() {
        moviesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                errorMessage.value = "Error fetching movies: ${error.message}"
                return@addSnapshotListener
            }
            val movies = snapshot?.documents?.map { doc ->
                doc.toObject<Movie>()?.copy(id = doc.id)  // Include Firestore document ID
            }?.filterNotNull() ?: emptyList()
            movieList.value = movies
        }
    }

    // Add a new movie
    fun addMovie(movie: Movie) {
        moviesCollection.add(movie)
            .addOnSuccessListener { fetchMovies() }  // Refresh list
            .addOnFailureListener { e ->
                errorMessage.value = "Failed to add movie: ${e.message}"
            }
    }

    // Update a movie (full or partial update)
    fun updateMovie(movieId: String, updates: Map<String, Any>) {
        moviesCollection.document(movieId)
            .update(updates)
            .addOnSuccessListener { fetchMovies() }  // Refresh list
            .addOnFailureListener { e ->
                errorMessage.value = "Failed to update movie: ${e.message}"
            }
    }

    // Delete a movie
    fun deleteMovie(movieId: String) {
        moviesCollection.document(movieId)
            .delete()
            .addOnSuccessListener { fetchMovies() }  // Refresh list
            .addOnFailureListener { e ->
                errorMessage.value = "Failed to delete movie: ${e.message}"
            }
    }
}
package com.santosh.moviefirebaseapp.model

data class Movie(
    val id: String = "",
    val title: String = "",
    val studio: String = "",
    val rating: Double = 0.0,
    val thumbnailUrl: String = "" // optional, used for displaying a movie poster image
)

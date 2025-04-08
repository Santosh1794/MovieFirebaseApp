package com.santosh.moviefirebaseapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.santosh.moviefirebaseapp.R
import com.santosh.moviefirebaseapp.model.Movie
import com.squareup.picasso.Picasso

class MovieAdapter(private var movieList: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    var onDeleteClick: ((Movie) -> Unit)? = null
    var onEditClick: ((Movie) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.title.text = movie.title
        holder.studio.text = movie.studio
        holder.rating.text = movie.rating.toString()
        Picasso.get().load(movie.thumbnailUrl).into(holder.thumbnail)

        // Edit button click
        holder.editButton.setOnClickListener {
            onEditClick?.invoke(movie)
        }

        // Delete button click
        holder.deleteButton.setOnClickListener {
            onDeleteClick?.invoke(movie)
        }
    }

    fun submitList(newList: List<Movie>) {
        movieList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.movie_title)
        val studio: TextView = itemView.findViewById(R.id.movie_studio)
        val rating: TextView = itemView.findViewById(R.id.movie_rating)
        val thumbnail: ImageView = itemView.findViewById(R.id.movie_thumbnail)
        val editButton: Button = itemView.findViewById(R.id.edit_button)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }
}

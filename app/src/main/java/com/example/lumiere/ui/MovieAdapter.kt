package com.example.lumiere.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lumiere.data.Movie
import com.example.lumiere.databinding.ItemMovieBinding

class MovieAdapter(private val onClick: (Movie) -> Unit) :
    ListAdapter<Movie, MovieAdapter.VH>(Diff) {
    class VH(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val movie = getItem(position)
        holder.itemView.setOnClickListener { onClick(movie) }
        holder.binding.tvTitle.text = movie.title
        holder.binding.tvRating.text = "★ %.1f".format(movie.voteAverage)
        Glide.with(holder.itemView)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(holder.binding.ivPoster)
    }

    object Diff : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(a: Movie, b: Movie) = a.id == b.id
        override fun areContentsTheSame(a: Movie, b: Movie) = a == b
    }
}
package com.example.lumiere.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lumiere.data.MovieEntry
import com.example.lumiere.databinding.ItemMovieBinding

class EntryAdapter(private val onClick: (MovieEntry) -> Unit) :
    ListAdapter<MovieEntry, EntryAdapter.VH>(Diff) {

    class VH(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val entry = getItem(position)
        holder.binding.tvTitle.text = entry.title
        holder.binding.tvRating.text = when {
            entry.rating > 0f -> "Sua nota: ★ %.1f".format(entry.rating)
            entry.watched -> "Sem nota ainda"
            else -> ""
        }

        if (entry.posterPath != null) {
            Glide.with(holder.itemView)
                .load("https://image.tmdb.org/t/p/w500${entry.posterPath}")
                .into(holder.binding.ivPoster)
        } else {
            Glide.with(holder.itemView).clear(holder.binding.ivPoster)
        }

        holder.itemView.setOnClickListener { onClick(entry) }
    }

    object Diff : DiffUtil.ItemCallback<MovieEntry>() {
        override fun areItemsTheSame(a: MovieEntry, b: MovieEntry) = a.movieId == b.movieId
        override fun areContentsTheSame(a: MovieEntry, b: MovieEntry) = a == b
    }
}
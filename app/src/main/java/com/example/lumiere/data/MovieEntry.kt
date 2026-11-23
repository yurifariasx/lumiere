package com.example.lumiere.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_entries")
data class MovieEntry(
    @PrimaryKey val movieId: Int,
    val title: String,
    val posterPath: String?,
    val watched: Boolean = false,
    val wantToWatch: Boolean = false,
    val favorite: Boolean = false,
    val rating: Float = 0f,
    val updatedAt: Long = System.currentTimeMillis()
)
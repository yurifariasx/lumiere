package com.example.lumiere.data

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String?
)

data class MovieResponse(val results: List<Movie>)

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String?,
    val runtime: Int?,
    val genres: List<Genre> = emptyList(),
    val credits: Credits?
)

data class Genre(val name: String)
data class Credits(val cast: List<CastMember> = emptyList())
data class CastMember(val name: String, val character: String?)
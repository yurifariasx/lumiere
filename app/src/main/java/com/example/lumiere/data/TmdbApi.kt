package com.example.lumiere.data

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "pt-BR",
        @Query("page") page: Int = 1
    ): MovieResponse
}
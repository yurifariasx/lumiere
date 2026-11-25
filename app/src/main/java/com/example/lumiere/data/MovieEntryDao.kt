package com.example.lumiere.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieEntryDao {

    @Query("SELECT * FROM movie_entries WHERE movieId = :id")
    fun observe(id: Int): Flow<MovieEntry?>

    @Query("SELECT * FROM movie_entries WHERE movieId = :id")
    suspend fun get(id: Int): MovieEntry?

    @Upsert
    suspend fun upsert(entry: MovieEntry)

    @Query("DELETE FROM movie_entries WHERE movieId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM movie_entries WHERE watched = 1 ORDER BY updatedAt DESC")
    fun observeWatched(): Flow<List<MovieEntry>>

    @Query("SELECT * FROM movie_entries WHERE wantToWatch = 1 ORDER BY updatedAt DESC")
    fun observeWantToWatch(): Flow<List<MovieEntry>>

    @Query("SELECT * FROM movie_entries WHERE favorite = 1 ORDER BY updatedAt DESC")
    fun observeFavorites(): Flow<List<MovieEntry>>



    @Query("SELECT COUNT(*) FROM movie_entries WHERE watched = 1")
    fun observeWatchedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM movie_entries WHERE wantToWatch = 1")
    fun observeWantToWatchCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM movie_entries WHERE favorite = 1")
    fun observeFavoriteCount(): Flow<Int>

    @Query("SELECT AVG(rating) FROM movie_entries WHERE rating > 0")
    fun observeAverageRating(): Flow<Double?>
}
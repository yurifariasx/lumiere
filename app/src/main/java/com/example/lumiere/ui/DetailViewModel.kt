package com.example.lumiere.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.lumiere.data.AppDatabase
import com.example.lumiere.data.MovieDetails
import com.example.lumiere.data.MovieEntry
import com.example.lumiere.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DetailState {
    data object Loading : DetailState
    data object Error : DetailState
    data class Success(val movie: MovieDetails) : DetailState
}

class DetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val movieId: Int = savedStateHandle["movieId"] ?: -1
    private val dao = AppDatabase.get(application).movieEntryDao()

    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state: StateFlow<DetailState> = _state

    val entry: StateFlow<MovieEntry?> = dao.observe(movieId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = DetailState.Loading
            _state.value = try {
                DetailState.Success(RetrofitClient.api.getMovieDetails(movieId))
            } catch (e: Exception) {
                DetailState.Error
            }
        }
    }

    fun toggleWatched() = update {
        if (it.watched) it.copy(watched = false, favorite = false, rating = 0f)
        else it.copy(watched = true, wantToWatch = false)
    }

    fun toggleWantToWatch() = update {
        if (it.wantToWatch) it.copy(wantToWatch = false)
        else it.copy(wantToWatch = true, watched = false, favorite = false, rating = 0f)
    }

    fun toggleFavorite() = update {
        if (it.favorite) it.copy(favorite = false)
        else it.copy(favorite = true, watched = true, wantToWatch = false)
    }

    fun setRating(rating: Float) = update {
        if (rating > 0f) it.copy(rating = rating, watched = true, wantToWatch = false)
        else it.copy(rating = 0f)
    }

    private fun update(change: (MovieEntry) -> MovieEntry) {
        val movie = (state.value as? DetailState.Success)?.movie ?: return
        viewModelScope.launch {
            val current = dao.get(movie.id)
                ?: MovieEntry(
                    movieId = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath
                )
            val updated = change(current).copy(updatedAt = System.currentTimeMillis())
            val empty = !updated.watched && !updated.wantToWatch &&
                    !updated.favorite && updated.rating == 0f
            if (empty) dao.delete(movie.id) else dao.upsert(updated)
        }
    }
}
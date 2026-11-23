package com.example.lumiere.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumiere.data.MovieDetails
import com.example.lumiere.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface DetailState {
    data object Loading : DetailState
    data object Error : DetailState
    data class Success(val movie: MovieDetails) : DetailState
}

class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val movieId: Int = savedStateHandle["movieId"] ?: -1

    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state: StateFlow<DetailState> = _state

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
}
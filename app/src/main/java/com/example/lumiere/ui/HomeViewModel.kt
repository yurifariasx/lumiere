package com.example.lumiere.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumiere.data.Movie
import com.example.lumiere.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface HomeState {
    data object Loading : HomeState
    data class Success(val movies: List<Movie>) : HomeState
    data object Error : HomeState
}

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            _state.value = try {
                HomeState.Success(RetrofitClient.api.getPopularMovies().results)
            } catch (e: Exception) {
                HomeState.Error
            }
        }
    }
}
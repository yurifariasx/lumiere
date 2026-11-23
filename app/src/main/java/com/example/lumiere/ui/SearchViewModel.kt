package com.example.lumiere.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumiere.data.Movie
import com.example.lumiere.data.RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data object Empty : SearchState
    data object Error : SearchState
    data class Success(val movies: List<Movie>) : SearchState
}

@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {

    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    init {
        viewModelScope.launch {
            query.debounce(400).distinctUntilChanged().collectLatest { q ->
                if (q.isBlank()) {
                    _state.value = SearchState.Idle
                    return@collectLatest
                }
                _state.value = SearchState.Loading
                _state.value = try {
                    val results = RetrofitClient.api.searchMovies(q).results
                    if (results.isEmpty()) SearchState.Empty else SearchState.Success(results)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    SearchState.Error
                }
            }
        }
    }

    fun onQueryChanged(text: String) {
        query.value = text.trim()
    }
}
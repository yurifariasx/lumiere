package com.example.lumiere.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumiere.data.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProfileStats(
    val watched: Int = 0,
    val wantToWatch: Int = 0,
    val favorites: Int = 0,
    val averageRating: Double? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.get(application).movieEntryDao()

    val stats: StateFlow<ProfileStats> = combine(
        dao.observeWatchedCount(),
        dao.observeWantToWatchCount(),
        dao.observeFavoriteCount(),
        dao.observeAverageRating()
    ) { watched, want, favorites, average ->
        ProfileStats(watched, want, favorites, average)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileStats())
}
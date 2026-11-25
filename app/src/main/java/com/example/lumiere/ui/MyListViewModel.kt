package com.example.lumiere.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumiere.data.AppDatabase
import com.example.lumiere.data.MovieEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class MyListViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.get(application).movieEntryDao()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    val entries: StateFlow<List<MovieEntry>> = _selectedTab
        .flatMapLatest { tab ->
            when (tab) {
                0 -> dao.observeWatched()
                1 -> dao.observeWantToWatch()
                else -> dao.observeFavorites()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }
}
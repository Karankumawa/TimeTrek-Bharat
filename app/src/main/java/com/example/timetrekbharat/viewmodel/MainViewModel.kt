package com.example.timetrekbharat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

import com.example.timetrekbharat.model.State
import com.example.timetrekbharat.repository.StateRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StateRepository(application)

    private val _searchQuery = MutableLiveData("")
    private val _selectedFilter = MutableLiveData("All")

    val states = MediatorLiveData<List<State>>()

    val isLoading: LiveData<Boolean> = repository.isLoading
    val errorMessage: LiveData<String?> = repository.errorMessage

    private var currentSource: LiveData<List<State>>? = null

    init {
        updateStatesSource()

        states.addSource(_searchQuery) { updateStatesSource() }
        states.addSource(_selectedFilter) { updateStatesSource() }

        refreshStates()
    }

    private fun updateStatesSource() {
        val query = _searchQuery.value ?: ""
        val filter = _selectedFilter.value ?: "All"

        if (currentSource != null) {
            states.removeSource(currentSource!!)
        }

        val newSource: LiveData<List<State>> = when {
            filter.equals("Favorites", ignoreCase = true) -> repository.getFavoriteStates()
            !filter.equals("All", ignoreCase = true) -> repository.getStatesByRegion(filter)
            query.isNotBlank() -> repository.searchCachedStates(query)
            else -> repository.getCachedStates()
        }

        currentSource = newSource
        states.addSource(newSource) { list ->
            var filtered = list ?: emptyList()
            if (query.isNotBlank() && !filter.equals("Favorites", ignoreCase = true)) {
                filtered = filtered.filter {
                    (it.name?.contains(query, ignoreCase = true) == true) ||
                    (it.region?.contains(query, ignoreCase = true) == true) ||
                    (it.shortDescription?.contains(query, ignoreCase = true) == true)
                }
            }
            states.value = filtered
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setRegionFilter(filterName: String) {
        _selectedFilter.value = filterName
    }

    fun toggleFavorite(state: State) {
        repository.toggleFavorite(state.slug, !state.isFavorite)
    }

    fun refreshStates() {
        val filter = _selectedFilter.value
        val region = if (filter != null && filter != "All" && filter != "Favorites") filter else null
        repository.fetchStatesFromNetwork(_searchQuery.value, region)
    }
}

package com.example.timetrekbharat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

import com.example.timetrekbharat.model.HistoryCategory
import com.example.timetrekbharat.model.HistoryTopicItem
import com.example.timetrekbharat.model.State
import com.example.timetrekbharat.repository.HistoryContentRepository
import com.example.timetrekbharat.repository.StateRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val stateRepository = StateRepository(application)

    private val _selectedCategory = MutableLiveData(HistoryCategory.STATES)
    private val _searchQuery = MutableLiveData("")
    private val _selectedFilter = MutableLiveData("All")

    val selectedCategory: LiveData<HistoryCategory> = _selectedCategory
    val searchQuery: LiveData<String> = _searchQuery
    val selectedFilter: LiveData<String> = _selectedFilter

    val states = MediatorLiveData<List<State>>()
    val historyTopics = MediatorLiveData<List<HistoryTopicItem>>()

    val isLoading: LiveData<Boolean> = stateRepository.isLoading
    val errorMessage: LiveData<String?> = stateRepository.errorMessage

    private var currentStateSource: LiveData<List<State>>? = null

    init {
        updateStatesSource()
        updateHistoryTopics()

        states.addSource(_searchQuery) { updateStatesSource() }
        states.addSource(_selectedFilter) { updateStatesSource() }

        historyTopics.addSource(_selectedCategory) { updateHistoryTopics() }
        historyTopics.addSource(_searchQuery) { updateHistoryTopics() }

        refreshStates()
    }

    fun setCategory(category: HistoryCategory) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            updateHistoryTopics()
        }
    }

    private fun updateHistoryTopics() {
        val category = _selectedCategory.value ?: HistoryCategory.STATES
        if (category == HistoryCategory.STATES) {
            historyTopics.value = emptyList()
            return
        }
        val query = _searchQuery.value ?: ""
        val filteredTopics = HistoryContentRepository.searchTopics(category, query)
        historyTopics.value = filteredTopics
    }

    private fun updateStatesSource() {
        val query = _searchQuery.value ?: ""
        val filter = _selectedFilter.value ?: "All"

        if (currentStateSource != null) {
            states.removeSource(currentStateSource!!)
        }

        val newSource: LiveData<List<State>> = when {
            filter.equals("Favorites", ignoreCase = true) -> stateRepository.getFavoriteStates()
            !filter.equals("All", ignoreCase = true) -> stateRepository.getStatesByRegion(filter)
            query.isNotBlank() -> stateRepository.searchCachedStates(query)
            else -> stateRepository.getCachedStates()
        }

        currentStateSource = newSource
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
        stateRepository.toggleFavorite(state.slug, !state.isFavorite)
    }

    fun refreshStates() {
        val filter = _selectedFilter.value
        val region = if (filter != null && filter != "All" && filter != "Favorites") filter else null
        stateRepository.fetchStatesFromNetwork(_searchQuery.value, region)
    }
}

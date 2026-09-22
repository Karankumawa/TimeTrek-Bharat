package com.example.timetrekbharat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.timetrekbharat.model.State
import com.example.timetrekbharat.repository.StateRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StateRepository(application)
    private val searchQuery = MutableLiveData("")

    val states: LiveData<List<State>> = searchQuery.switchMap { query ->
        repository.searchCachedStates(query)
    }

    val isLoading: LiveData<Boolean> = repository.isLoading
    val errorMessage: LiveData<String?> = repository.errorMessage

    init {
        refreshStates()
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun refreshStates() {
        repository.fetchStatesFromNetwork(searchQuery.value)
    }
}

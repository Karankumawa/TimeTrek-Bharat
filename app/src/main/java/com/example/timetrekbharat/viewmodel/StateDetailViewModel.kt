package com.example.timetrekbharat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.timetrekbharat.model.State
import com.example.timetrekbharat.repository.StateRepository

class StateDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StateRepository(application)
    private val stateSlug = MutableLiveData<String?>(null)

    val stateDetail: LiveData<State> = stateSlug.switchMap { slug ->
        if (slug.isNullOrEmpty()) {
            MutableLiveData()
        } else {
            repository.getCachedStateDetail(slug)
        }
    }

    val isLoading: LiveData<Boolean> = repository.isLoading
    val errorMessage: LiveData<String?> = repository.errorMessage

    fun setStateSlug(slug: String?) {
        if (!slug.isNullOrEmpty() && slug != stateSlug.value) {
            stateSlug.value = slug
            repository.fetchStateDetailFromNetwork(slug)
        }
    }
}

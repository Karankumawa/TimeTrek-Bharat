package com.example.timetrekbharat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.timetrekbharat.model.CommunityPost
import com.example.timetrekbharat.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    private val _posts = MutableLiveData<List<CommunityPost>>()
    val posts: LiveData<List<CommunityPost>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _postSuccess = MutableLiveData<Boolean>()
    val postSuccess: LiveData<Boolean> = _postSuccess

    init {
        fetchPosts(null)
    }

    fun fetchPosts(stateSlug: String? = null) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val filter = if (!stateSlug.isNullOrBlank()) "eq.$stateSlug" else null
                val response = RetrofitClient.api.getCommunityPosts(filter)
                if (response.isSuccessful && response.body() != null) {
                    _posts.postValue(response.body()!!)
                } else {
                    _errorMessage.postValue("Failed to fetch community updates.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Network error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addPost(title: String, description: String, category: String, location: String, stateSlug: String, author: String, imageUrl: String) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)

        val post = CommunityPost(
            id = null,
            title = title,
            description = description,
            category = category.ifBlank { "🏛️ Historical Update" },
            locationName = location,
            stateSlug = stateSlug,
            authorName = author,
            imageUrl = imageUrl
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.addCommunityPost(post)
                if (response.isSuccessful) {
                    _postSuccess.postValue(true)
                } else {
                    _errorMessage.postValue("Failed to submit lore.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Network error: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

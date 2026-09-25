package com.example.timetrekbharat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timetrekbharat.model.CommunityPost
import com.example.timetrekbharat.network.RetrofitClient
import java.util.concurrent.Executors

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    private val executor = Executors.newSingleThreadExecutor()

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

    fun fetchPosts(stateSlug: String?) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)
        
        executor.execute {
            try {
                val response = RetrofitClient.api.getCommunityPosts(stateSlug).execute()
                if (response.isSuccessful && response.body() != null && response.body()!!.isSuccess) {
                    _posts.postValue(response.body()!!.data ?: emptyList())
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

    fun addPost(title: String, description: String, location: String, stateSlug: String, author: String, imageUrl: String) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)
        
        val post = CommunityPost(
            title = title,
            description = description,
            locationName = location,
            stateSlug = stateSlug,
            authorName = author,
            imageUrl = imageUrl
        )
        
        executor.execute {
            try {
                val response = RetrofitClient.api.addCommunityPost(post).execute()
                if (response.isSuccessful && response.body() != null && response.body()!!.isSuccess) {
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

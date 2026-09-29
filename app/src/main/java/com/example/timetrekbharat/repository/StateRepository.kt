package com.example.timetrekbharat.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timetrekbharat.db.AppDatabase
import com.example.timetrekbharat.model.State
import com.example.timetrekbharat.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StateRepository(context: Context) {

    private val stateDao = AppDatabase.getInstance(context).stateDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>(null)

    init {
        // Automatically fetch states online from MongoDB backend API
        fetchStatesFromNetwork()
    }

    fun getCachedStates(): LiveData<List<State>> = stateDao.getAllStates()

    fun getFavoriteStates(): LiveData<List<State>> = stateDao.getFavoriteStates()

    fun getStatesByRegion(region: String): LiveData<List<State>> {
        return if (region.equals("All", ignoreCase = true)) {
            stateDao.getAllStates()
        } else {
            stateDao.getStatesByRegion(region)
        }
    }

    fun searchCachedStates(query: String?): LiveData<List<State>> {
        return if (query.isNullOrBlank()) {
            stateDao.getAllStates()
        } else {
            stateDao.searchStates(query.trim())
        }
    }

    fun getCachedStateDetail(slug: String): LiveData<State> = stateDao.getStateBySlug(slug)

    fun toggleFavorite(slug: String, isFavorite: Boolean) {
        scope.launch {
            stateDao.updateFavoriteStatus(slug, isFavorite)
        }
    }

    fun fetchStatesFromNetwork(searchQuery: String? = null, regionQuery: String? = null) {
        isLoading.postValue(true)
        errorMessage.postValue(null)
        scope.launch {
            try {
                val response = RetrofitClient.api.getAllStates(searchQuery, regionQuery)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val networkStates = response.body()?.data
                    if (!networkStates.isNullOrEmpty()) {
                        // Preserve favorite status from local Room cache
                        for (netState in networkStates) {
                            val local = stateDao.getStateBySlugSync(netState.slug)
                            if (local != null) {
                                netState.isFavorite = local.isFavorite
                            }
                        }
                        stateDao.insertStates(networkStates)
                    }
                } else {
                    errorMessage.postValue("Unable to fetch state data from MongoDB server.")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Network connection offline.")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun fetchStateDetailFromNetwork(stateSlug: String) {
        isLoading.postValue(true)
        errorMessage.postValue(null)
        scope.launch {
            try {
                val response = RetrofitClient.api.getStateDetail(stateSlug)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val networkState = response.body()?.data
                    if (networkState != null) {
                        val local = stateDao.getStateBySlugSync(networkState.slug)
                        if (local != null) {
                            networkState.isFavorite = local.isFavorite
                        }
                        stateDao.insertState(networkState)
                    }
                } else {
                    errorMessage.postValue("Unable to fetch state detail from MongoDB server.")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Network connection offline.")
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}

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

    private val appContext = context.applicationContext
    private val stateDao = AppDatabase.getInstance(appContext).stateDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>(null)

    init {
        // Fetch states directly from Supabase Production Database
        scope.launch {
            fetchStatesFromNetwork()
        }
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
        scope.launch {
            try {
                val response = RetrofitClient.api.getAllStates()
                if (response.isSuccessful && response.body() != null) {
                    val networkStates = response.body()!!
                    if (networkStates.isNotEmpty()) {
                        for (netState in networkStates) {
                            val local = stateDao.getStateBySlugSync(netState.slug)
                            if (local != null) {
                                netState.isFavorite = local.isFavorite
                            }
                        }
                        stateDao.insertStates(networkStates)
                    }
                }
            } catch (_: Exception) {
                // Silently fallback to cached Room DB data
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun fetchStateDetailFromNetwork(stateSlug: String) {
        isLoading.postValue(true)
        scope.launch {
            try {
                val response = RetrofitClient.api.getStateDetail("eq.$stateSlug")
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val networkState = response.body()!![0]
                    val local = stateDao.getStateBySlugSync(networkState.slug)
                    if (local != null) {
                        networkState.isFavorite = local.isFavorite
                    }
                    stateDao.insertState(networkState)
                }
            } catch (_: Exception) {
                // Silently fallback to local Room data
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}

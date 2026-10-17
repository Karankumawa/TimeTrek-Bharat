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
        // Fetch states from Supabase Database & auto-connect local assets if available
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

    /**
     * Searches local assets/ directory and subfolders (e.g. rajasthanimages/).
     * Returns file:///android_asset/... URI if local image exists, else returns fallback Supabase URL.
     */
    private fun resolveStateImageUrl(slug: String, fallbackUrl: String): String {
        val cleanSlug = slug.replace("-", "").replace("_", "").lowercase()

        // 1. Check state-specific subfolders FIRST
        val subDirs = listOf(
            "statesimage/${cleanSlug}images",
            "statesimages/${cleanSlug}images",
            "statesimage/${slug}images",
            "statesimages/${slug}images",
            "statesimage/$slug",
            "statesimages/$slug",
            "${cleanSlug}images",
            "${slug}images",
            cleanSlug,
            slug
        )

        for (dir in subDirs) {
            try {
                val files = appContext.assets.list(dir)?.toList() ?: emptyList()
                val imageFile = files.firstOrNull { f ->
                    !f.startsWith(".") && (
                        f.endsWith(".jpg", ignoreCase = true) ||
                        f.endsWith(".jpeg", ignoreCase = true) ||
                        f.endsWith(".png", ignoreCase = true) ||
                        f.endsWith(".webp", ignoreCase = true)
                    )
                }
                if (imageFile != null) {
                    return "file:///android_asset/$dir/$imageFile"
                }
            } catch (_: Exception) {}
        }

        // 2. Check main statesimage folders for direct matching filename
        val mainDirs = listOf("statesimage", "statesimages")
        for (dir in mainDirs) {
            try {
                val files = appContext.assets.list(dir)?.toList() ?: emptyList()
                val matchedFile = files.firstOrNull { fileName ->
                    val nameWithoutExt = fileName.substringBeforeLast(".")
                    nameWithoutExt.equals(slug, ignoreCase = true) ||
                    nameWithoutExt.replace("-", "").replace("_", "").equals(cleanSlug, ignoreCase = true) ||
                    nameWithoutExt.contains(cleanSlug, ignoreCase = true)
                }
                if (matchedFile != null) {
                    return "file:///android_asset/$dir/$matchedFile"
                }
            } catch (_: Exception) {}
        }

        return fallbackUrl
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
                            // Connect local asset image if present in assets folder
                            val assetUrl = resolveStateImageUrl(netState.slug, netState.imageUrl ?: "")
                            if (assetUrl.startsWith("file:///android_asset/")) {
                                netState.imageUrl = assetUrl
                                netState.bannerUrl = resolveStateImageUrl(netState.slug, netState.bannerUrl ?: assetUrl)
                            }
                        }
                        stateDao.insertStates(networkStates)
                    }
                }
            } catch (_: Exception) {
                // Silently fallback to cached Room DB data
            } finally {
                // Resolve assets for cached states if offline
                try {
                    val cachedStates = stateDao.getAllStatesSync()
                    for (cState in cachedStates) {
                        val assetUrl = resolveStateImageUrl(cState.slug, cState.imageUrl ?: "")
                        if (assetUrl.startsWith("file:///android_asset/") && cState.imageUrl != assetUrl) {
                            cState.imageUrl = assetUrl
                            cState.bannerUrl = resolveStateImageUrl(cState.slug, cState.bannerUrl ?: assetUrl)
                            stateDao.insertState(cState)
                        }
                    }
                } catch (_: Exception) {}
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
                    val assetUrl = resolveStateImageUrl(networkState.slug, networkState.imageUrl ?: "")
                    if (assetUrl.startsWith("file:///android_asset/")) {
                        networkState.imageUrl = assetUrl
                        networkState.bannerUrl = resolveStateImageUrl(networkState.slug, networkState.bannerUrl ?: assetUrl)
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

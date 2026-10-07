package com.example.timetrekbharat.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timetrekbharat.db.AppDatabase
import com.example.timetrekbharat.model.State
import com.example.timetrekbharat.model.TimelineEntry
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
        // Instant check and pre-population if Room DB is empty
        scope.launch {
            checkAndPrepopulateDefaultStates()
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

    private fun checkAndPrepopulateDefaultStates() {
        val existing = stateDao.getStateBySlugSync("rajasthan")
        if (existing == null) {
            val defaultStates = listOf(
                State(
                    slug = "rajasthan",
                    name = "Rajasthan",
                    capital = "Jaipur",
                    region = "North-West",
                    shortDescription = "Land of Kings, renowned for majestic hill forts, Thar Desert, royal Rajput heritage, and vibrant culture.",
                    imageUrl = "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?q=80&w=800",
                    bannerUrl = "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?q=80&w=1200",
                    timeline = listOf(
                        TimelineEntry(1, "Ancient Era", "c. 2500 BCE – 1500 BCE", "Indus Valley & Kalibangan Civilization", "Earliest urban settlements and terracotta fire altars along ancient Saraswati riverbed.", listOf("Indus Valley Trade", "Agricultural revolution"), listOf("Local Clan Chieftains")),
                        TimelineEntry(2, "Medieval Era", "8th – 18th Century CE", "Rajput Clans & Hill Fort Resistance", "Establishment of Mewar, Marwar, Dhundhar, and Amber kingdoms with hill fort fortifications.", listOf("Kumbhalgarh Wall", "Battle of Haldighati 1576"), listOf("Maharana Pratap", "Rana Sanga", "Maharana Kumbha"))
                    )
                ),
                State(
                    slug = "gujarat",
                    name = "Gujarat",
                    capital = "Gandhinagar",
                    region = "West",
                    shortDescription = "Land of Solanki Architecture, Dholavira Indus Valley site, Rani ki Vav stepwell, and Sabarmati Ashram.",
                    imageUrl = "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?q=80&w=800",
                    bannerUrl = "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?q=80&w=1200",
                    timeline = listOf(
                        TimelineEntry(1, "Ancient Era", "c. 2600 BCE", "Dholavira & Lothal Port City", "Ancient maritime port with world's oldest tidal dockyard.", listOf("Harappan Maritime Trade", "Lothal Dockyard"), listOf("Harappan Merchants")),
                        TimelineEntry(2, "Medieval Era", "10th – 13th Century CE", "Solanki Dynasty Golden Age", "Construction of Rani ki Vav stepwell and Modhera Sun Temple.", listOf("Rani ki Vav 1063 CE", "Modhera Temple"), listOf("Queen Udayamati", "King Bhimdev I"))
                    )
                ),
                State(
                    slug = "karnataka",
                    name = "Karnataka",
                    capital = "Bengaluru",
                    region = "South",
                    shortDescription = "Cradle of Vijayanagara Empire, Chalukya rock-cut architecture at Badami, and Hampi UNESCO site.",
                    imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?q=80&w=800",
                    bannerUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?q=80&w=1200",
                    timeline = listOf(
                        TimelineEntry(1, "Medieval Era", "1336 – 1646 CE", "Vijayanagara Golden Empire", "Global trade metropolis, stone chariot, musical pillars, and grand Ranga Mantapa.", listOf("Foundation of Hampi", "Pattadakal Temples"), listOf("Emperor Krishnadevaraya", "Harihara I", "Bukka I"))
                    )
                ),
                State(
                    slug = "maharashtra",
                    name = "Maharashtra",
                    capital = "Mumbai",
                    region = "West",
                    shortDescription = "Land of Maratha Empire, Chhatrapati Shivaji Maharaj hill forts, Ajanta & Ellora cave temples.",
                    imageUrl = "https://images.unsplash.com/photo-1567157577867-05ccb1388e66?q=80&w=800",
                    bannerUrl = "https://images.unsplash.com/photo-1567157577867-05ccb1388e66?q=80&w=1200",
                    timeline = listOf(
                        TimelineEntry(1, "Ancient Era", "2nd Century BCE", "Ajanta & Ellora Cave Monasteries", "Monolithic rock-cut Buddhist and Kailash temple architecture.", listOf("Kailasa Temple Carving", "Ajanta Frescoes"), listOf("Rashtrakuta Kings", "Satavahanas")),
                        TimelineEntry(2, "Medieval Era", "1674 CE", "Maratha Swarajya Establishment", "Coronation of Chhatrapati Shivaji Maharaj at Raigad Fort.", listOf("Raigad Coronation 1674", "Guerrilla Warfare"), listOf("Chhatrapati Shivaji Maharaj", "Sambhaji Maharaj"))
                    )
                ),
                State(
                    slug = "delhi",
                    name = "Delhi",
                    capital = "New Delhi",
                    region = "North",
                    shortDescription = "Historic seat of seven empires, Qutub Minar, Red Fort, Humayun's Tomb, and Indraprastha heritage.",
                    imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=800",
                    bannerUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=1200",
                    timeline = listOf(
                        TimelineEntry(1, "Medieval Era", "1192 – 1857 CE", "Sultanate & Mughal Imperial Seat", "Construction of Qutub Minar, Lal Qila, and Jama Masjid.", listOf("Qutub Minar 1199 CE", "Red Fort 1648 CE"), listOf("Prithviraj Chauhan", "Qutb-ud-din Aibak", "Shah Jahan"))
                    )
                )
            )
            stateDao.insertStates(defaultStates)
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
                        for (netState in networkStates) {
                            val local = stateDao.getStateBySlugSync(netState.slug)
                            if (local != null) {
                                netState.isFavorite = local.isFavorite
                            }
                        }
                        stateDao.insertStates(networkStates)
                    }
                } else {
                    errorMessage.postValue("Using cached offline database.")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Offline Mode Active")
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
                    errorMessage.postValue("Using cached state details.")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Offline Mode Active")
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}

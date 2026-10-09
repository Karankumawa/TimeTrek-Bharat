package com.example.timetrekbharat.repository

import android.content.Context
import android.util.Log
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

    private val appContext = context.applicationContext
    private val stateDao = AppDatabase.getInstance(appContext).stateDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>(null)

    init {
        // Prepopulate or refresh default states with local asset images & fallback URLs
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

    /**
     * Lists files in assets/statesimage/ and dynamically matches slug with any extension (.png, .jpg, .jpeg, .webp).
     * Returns file:///android_asset/statesimage/... URI so manual edits in assets take effect on app run and in Room DB.
     */
    private fun resolveStateImageUrl(slug: String, fallbackUrl: String): String {
        try {
            val assetFiles = appContext.assets.list("statesimage")?.toList() ?: emptyList()
            val matchedFile = assetFiles.firstOrNull { fileName ->
                val nameWithoutExt = fileName.substringBeforeLast(".")
                nameWithoutExt.equals(slug, ignoreCase = true)
            }
            if (matchedFile != null) {
                return "file:///android_asset/statesimage/$matchedFile"
            }
        } catch (e: Exception) {
            Log.e("StateRepository", "Error resolving asset image for $slug", e)
        }
        return fallbackUrl
    }

    private fun checkAndPrepopulateDefaultStates() {
        val defaultStates = listOf(
            State(
                slug = "rajasthan",
                name = "Rajasthan",
                capital = "Jaipur",
                region = "North-West",
                shortDescription = "Land of Kings, renowned for majestic hill forts, Thar Desert, royal Rajput heritage, and vibrant culture.",
                imageUrl = "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?auto=format&fit=crop&w=1200&q=80",
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
                imageUrl = "https://images.unsplash.com/photo-1609949279531-cf48d64bed89?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1609949279531-cf48d64bed89?auto=format&fit=crop&w=1200&q=80",
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
                imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=1200&q=80",
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
                imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
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
                imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "1192 – 1857 CE", "Sultanate & Mughal Imperial Seat", "Construction of Qutub Minar, Lal Qila, and Jama Masjid.", listOf("Qutub Minar 1199 CE", "Red Fort 1648 CE"), listOf("Prithviraj Chauhan", "Qutb-ud-din Aibak", "Shah Jahan"))
                )
            ),
            State(
                slug = "uttar-pradesh",
                name = "Uttar Pradesh",
                capital = "Lucknow",
                region = "North",
                shortDescription = "Cradle of Vedic civilization, Kashi (Varanasi), Ayodhya, Mathura, Agra Fort, and Sarnath Deer Park.",
                imageUrl = "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "528 BCE", "First Sermon at Sarnath", "Gautama Buddha delivered his first teaching on Dhamma at Sarnath.", listOf("Dhammak Stupa", "Mauryan Pillars"), listOf("Gautama Buddha", "Emperor Ashoka")),
                    TimelineEntry(2, "Medieval Era", "1556 – 1658 CE", "Mughal Architecture Peak", "Construction of Taj Mahal, Fatehpur Sikri, and Agra Fort.", listOf("Taj Mahal 1632 CE", "Buland Darwaza"), listOf("Akbar the Great", "Shah Jahan"))
                )
            ),
            State(
                slug = "tamil-nadu",
                name = "Tamil Nadu",
                capital = "Chennai",
                region = "South",
                shortDescription = "Land of Dravidian Chola dynasty, Tanjore Big Temple, Shore Temple Mahabalipuram, and Sangam literature.",
                imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "850 – 1279 CE", "Imperial Chola Dynasty Golden Era", "Conquest of Southeast Asia and construction of Brihadeeswarar Temple.", listOf("Tanjore Big Temple 1010 CE", "Chola Bronze Sculptures"), listOf("Raja Raja Chola I", "Rajendra Chola I"))
                )
            ),
            State(
                slug = "kerala",
                name = "Kerala",
                capital = "Thiruvananthapuram",
                region = "South",
                shortDescription = "God's Own Country, Chera kingdom, Muziris spice port, Padmanabhaswamy Temple treasure, and Kalaripayattu.",
                imageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "300 BCE – 1100 CE", "Muziris International Spice Port", "Global spice trading hub with Romans, Greeks, Chinese, and Arabs.", listOf("Muziris Spice Route", "Chera Dynasty Capital"), listOf("Cheraman Perumal"))
                )
            ),
            State(
                slug = "punjab",
                name = "Punjab",
                capital = "Chandigarh",
                region = "North",
                shortDescription = "Land of Five Rivers, Golden Temple Amritsar, Sikh Empire of Maharaja Ranjit Singh, and Indus Ropar site.",
                imageUrl = "https://images.unsplash.com/photo-1588096344356-9a2f64e24296?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1588096344356-9a2f64e24296?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "1799 – 1849 CE", "Sikh Empire Sovereignty", "Unification of Punjab under Maharaja Ranjit Singh with gold leaf gilding of Harmandir Sahib.", listOf("Gold Gilding of Harmandir Sahib", "Sikh Khalsa Raj"), listOf("Maharaja Ranjit Singh", "Hari Singh Nalwa"))
                )
            ),
            State(
                slug = "west-bengal",
                name = "West Bengal",
                capital = "Kolkata",
                region = "East",
                shortDescription = "Cultural heart of Pala Empire, Bishnupur terracotta temples, Victoria Memorial, and Bengal Renaissance.",
                imageUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "750 – 1161 CE", "Pala Dynasty & Mahayana Buddhism", "Patronage of Vikramashila and Nalanda universities and Buddhist palm-leaf art.", listOf("Bishnupur Terracotta Temples", "Somapura Mahavihara"), listOf("Gopala I", "Dharmapala", "Devapala"))
                )
            ),
            State(
                slug = "madhya-pradesh",
                name = "Madhya Pradesh",
                capital = "Bhopal",
                region = "Central",
                shortDescription = "Heart of Bharat, Sanchi Stupa, Khajuraho temple carvings, Gwalior Fort, Bhimbetka rock shelters.",
                imageUrl = "https://images.unsplash.com/photo-1627894098902-86161860822e?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1627894098902-86161860822e?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "3rd Century BCE", "Sanchi Stupa Construction", "Emperor Ashoka erected the Great Stupa at Sanchi containing sacred relics.", listOf("Torana Gateway Carvings", "Bhimbetka Caves 10,000 BCE"), listOf("Emperor Ashoka"))
                )
            ),
            State(
                slug = "goa",
                name = "Goa",
                capital = "Panaji",
                region = "West",
                shortDescription = "Kadamba Dynasty heritage, Portuguese colonial forts, Aguada Fort, and UNESCO Basilica of Bom Jesus.",
                imageUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "10th – 14th Century CE", "Kadamba Dynasty Golden Age", "Maritime trade port at Chandor and Gopakapattana.", listOf("Tambdi Surla Temple 12th C", "Fort Aguada 1612 CE"), listOf("King Jayakeshi I"))
                )
            ),
            State(
                slug = "bihar",
                name = "Bihar",
                capital = "Patna",
                region = "East",
                shortDescription = "Ancient Magadha Empire, Nalanda University, Mahabodhi Temple Bodh Gaya, and Emperor Ashoka Pataliputra.",
                imageUrl = "https://images.unsplash.com/photo-1610484826917-0f101a7bf7f4?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1610484826917-0f101a7bf7f4?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "5th Century BCE – 5th Century CE", "Magadha & Nalanda University", "World's first residential international university with 10,000 scholars.", listOf("Nalanda Mahavihara", "Ashoka Pillar at Vaishali"), listOf("Chandragupta Maurya", "Emperor Ashoka", "Aryabhata"))
                )
            ),
            State(
                slug = "odisha",
                name = "Odisha",
                capital = "Bhubaneswar",
                region = "East",
                shortDescription = "Land of Kalinga architecture, Konark Sun Temple chariot, Jagannath Temple Puri, and Udayagiri caves.",
                imageUrl = "https://images.unsplash.com/photo-1620891549027-942fdc95d3f5?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1620891549027-942fdc95d3f5?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "1250 CE", "Konark Sun Temple Construction", "Eastern Ganga Dynasty constructed the massive stone chariot Sun Temple.", listOf("24 Stone Wheels of Konark", "Jagannath Temple Puri"), listOf("King Narasimhadeva I"))
                )
            ),
            State(
                slug = "assam",
                name = "Assam",
                capital = "Dispur",
                region = "North-East",
                shortDescription = "Ahom Kingdom 600-year reign, Kamakhya Shaktipeeth, Rang Ghar amphitheatre, and Kaziranga heritage.",
                imageUrl = "https://images.unsplash.com/photo-1616489932200-a5417937d58a?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1616489932200-a5417937d58a?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "1228 – 1826 CE", "Ahom Dynasty Sovereignty", "Defeated Mughal forces 17 times including Battle of Saraighat on Brahmaputra.", listOf("Rang Ghar Amphitheatre", "Battle of Saraighat 1671"), listOf("Sukaphaa", "Lachit Borphukan"))
                )
            ),
            State(
                slug = "telangana",
                name = "Telangana",
                capital = "Hyderabad",
                region = "South",
                shortDescription = "Kakatiya Dynasty stone gateways, Thousand Pillar Temple, Charminar, Golconda Fort, and Ramappa UNESCO site.",
                imageUrl = "https://images.unsplash.com/photo-1605367031782-93822a1b1b4b?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1605367031782-93822a1b1b4b?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "1163 – 1323 CE", "Kakatiya Kingdom Architecture", "Construction of Ramappa floating-brick temple and Warangal fort arches.", listOf("Ramappa Temple UNESCO", "Golconda Acoustic Fort"), listOf("Ganapatideva", "Rani Rudrama Devi"))
                )
            ),
            State(
                slug = "uttarakhand",
                name = "Uttarakhand",
                capital = "Dehradun",
                region = "North",
                shortDescription = "Devbhumi (Land of Gods), sacred Chota Char Dham, Kedarnath temple, Badrinath, and Rishikesh Ganga heritage.",
                imageUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "8th Century CE", "Adi Shankaracharya Revival", "Establishment of Char Dham pilgrimage shrines and Jyotirmath.", listOf("Kedarnath Shrine", "Badrinath Temple"), listOf("Adi Shankaracharya"))
                )
            ),
            State(
                slug = "himachal-pradesh",
                name = "Himachal Pradesh",
                capital = "Shimla",
                region = "North",
                shortDescription = "Kangra Fort resistance, Tabo Monastery, Naggar Castle, and Himalayan wooden pagoda temples.",
                imageUrl = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "4th Century BCE – 18th Century CE", "Kangra Royal Fort Citadel", "Oldest fort in India resisting Alexander, Mahmud of Ghazni and Jahangir.", listOf("Kangra Fort Carvings", "Tabo Monastery 996 AD"), listOf("Katoch Dynasty Kings"))
                )
            ),
            State(
                slug = "jammu-and-kashmir",
                name = "Jammu & Kashmir",
                capital = "Srinagar",
                region = "North",
                shortDescription = "Martand Sun Temple stone monoliths, Pari Mahal, Hari Parbat Fort, and Karkota Dynasty Empire.",
                imageUrl = "https://images.unsplash.com/photo-1595815771614-ade9d652a65d?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1595815771614-ade9d652a65d?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "724 – 760 CE", "Karkota Dynasty Imperial Reign", "Emperor Lalitaditya Muktapida constructed Martand Sun Temple and expanded empire across Asia.", listOf("Martand Sun Temple", "Pari Mahal"), listOf("Emperor Lalitaditya Muktapida"))
                )
            ),
            State(
                slug = "andhra-pradesh",
                name = "Andhra Pradesh",
                capital = "Amaravati",
                region = "South",
                shortDescription = "Satavahana Dynasty cradle, Tirumala Tirupati, Lepakshi hanging pillar, and Amaravati Stupa.",
                imageUrl = "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "230 BCE – 220 CE", "Satavahana Empire Rule", "Pioneered maritime trade and constructed Amaravati Mahachaitya Stupa.", listOf("Amaravati Stupa Carvings", "Lepakshi Temple 1530 AD"), listOf("Gautamiputra Satakarni"))
                )
            )
        )

        for (state in defaultStates) {
            val existing = stateDao.getStateBySlugSync(state.slug)
            if (existing != null) {
                state.isFavorite = existing.isFavorite
            }
            // Always resolve local asset image in assets/statesimage/ first
            state.imageUrl = resolveStateImageUrl(state.slug, state.imageUrl ?: "")
            state.bannerUrl = resolveStateImageUrl(state.slug, state.bannerUrl ?: state.imageUrl ?: "")
            stateDao.insertState(state)
        }
    }

    fun fetchStatesFromNetwork(searchQuery: String? = null, regionQuery: String? = null) {
        isLoading.postValue(true)
        scope.launch {
            try {
                val response = RetrofitClient.api.getAllStates(searchQuery, regionQuery)
                if (response.isSuccessful && (response.body()?.isSuccess == true)) {
                    val networkStates = response.body()?.data
                    if (!networkStates.isNullOrEmpty()) {
                        for (netState in networkStates) {
                            val local = stateDao.getStateBySlugSync(netState.slug)
                            if (local != null) {
                                netState.isFavorite = local.isFavorite
                            }
                            // Always check and apply local asset image if present in assets/statesimage/
                            val localAssetUrl = resolveStateImageUrl(netState.slug, netState.imageUrl ?: "")
                            if (localAssetUrl.startsWith("file:///android_asset/")) {
                                netState.imageUrl = localAssetUrl
                                netState.bannerUrl = resolveStateImageUrl(netState.slug, netState.bannerUrl ?: localAssetUrl)
                            }
                        }
                        stateDao.insertStates(networkStates)
                    }
                }
            } catch (_: Exception) {
                // Silently use cached local Room data without showing error toast
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun fetchStateDetailFromNetwork(stateSlug: String) {
        isLoading.postValue(true)
        scope.launch {
            try {
                val response = RetrofitClient.api.getStateDetail(stateSlug)
                if (response.isSuccessful && (response.body()?.isSuccess == true)) {
                    val networkState = response.body()?.data
                    if (networkState != null) {
                        val local = stateDao.getStateBySlugSync(networkState.slug)
                        if (local != null) {
                            networkState.isFavorite = local.isFavorite
                        }
                        val localAssetUrl = resolveStateImageUrl(networkState.slug, networkState.imageUrl ?: "")
                        if (localAssetUrl.startsWith("file:///android_asset/")) {
                            networkState.imageUrl = localAssetUrl
                            networkState.bannerUrl = resolveStateImageUrl(networkState.slug, networkState.bannerUrl ?: localAssetUrl)
                        }
                        stateDao.insertState(networkState)
                    }
                }
            } catch (_: Exception) {
                // Silently use cached state detail
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}

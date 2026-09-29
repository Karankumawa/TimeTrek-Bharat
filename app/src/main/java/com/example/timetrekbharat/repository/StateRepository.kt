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
import kotlinx.coroutines.withContext

class StateRepository(context: Context) {

    private val stateDao = AppDatabase.getInstance(context).stateDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>(null)

    init {
        scope.launch {
            seedDatabaseIfEmpty()
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
        errorMessage.postValue(null)
        scope.launch {
            try {
                val response = RetrofitClient.api.getAllStates(searchQuery, regionQuery)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val networkStates = response.body()?.data
                    if (!networkStates.isNullOrEmpty()) {
                        // Preserve favorite status from local cache
                        for (netState in networkStates) {
                            val local = stateDao.getStateBySlugSync(netState.slug)
                            if (local != null) {
                                netState.isFavorite = local.isFavorite
                            }
                        }
                        stateDao.insertStates(networkStates)
                    }
                } else {
                    seedDatabaseIfEmpty()
                }
            } catch (e: Exception) {
                seedDatabaseIfEmpty()
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
                    seedDatabaseIfEmpty()
                }
            } catch (e: Exception) {
                seedDatabaseIfEmpty()
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        withContext(Dispatchers.IO) {
            val sampleStates = getComprehensiveStatesList()
            // Only populate if missing states
            stateDao.insertStates(sampleStates)
        }
    }

    private fun getComprehensiveStatesList(): List<State> {
        val states = ArrayList<State>()

        // 1. Rajasthan
        states.add(
            State(
                slug = "rajasthan",
                name = "Rajasthan",
                region = "North-West India",
                capital = "Jaipur",
                shortDescription = "Land of Kings, renowned for majestic hill forts, Thar Desert, royal Rajput heritage, and vibrant culture.",
                imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 2500 BCE – 1500 BCE", title = "Indus Valley & Kalibangan Civilization",
                        description = "Kalibangan in Hanumangarh district was a major provincial capital of the Indus Valley Civilization featuring ploughed field surfaces and advanced brick fortifications.",
                        keyEvents = listOf("Early Harappan urban phases", "Earliest known ploughed field", "Fire altars ritual worship"),
                        keyRulers = listOf("Indus Valley Clan Leaders"),
                        imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
                        images = listOf("https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80", "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80")
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "700 AD – 1192 AD", title = "Rise of Rajput Dynasties & Prithviraj Chauhan",
                        description = "The Gurjara-Pratihara empire and later Chauhan, Guhila, and Rathore dynasties established fortified kingdoms across Mewar, Marwar, and Dhundhar.",
                        keyEvents = listOf("Battle of Tarain (1191 & 1192 AD)", "Construction of Chittorgarh Fort", "Defense against early Arab incursions"),
                        keyRulers = listOf("Prithviraj Chauhan", "Bappa Rawal", "Nagabhata I"),
                        imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80",
                        images = listOf("https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80", "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80")
                    ),
                    TimelineEntry(
                        id = 3, era = "Medieval Era", period = "1526 AD – 1707 AD", title = "Mewar Resistance & Maharana Pratap",
                        description = "Maharana Pratap of Mewar valiantly defended Rajput sovereignty against Mughal Emperor Akbar, refusing to submit to foreign vassalage.",
                        keyEvents = listOf("Battle of Haldighati (1576 AD)", "Establishment of Jaipur city (1727 AD)", "Construction of Jantar Mantar observatory"),
                        keyRulers = listOf("Maharana Pratap", "Rana Sanga", "Sawai Jai Singh II"),
                        imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                        images = listOf("https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80", "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?auto=format&fit=crop&w=800&q=80")
                    )
                )
            )
        )

        // 2. Gujarat
        states.add(
            State(
                slug = "gujarat",
                name = "Gujarat",
                region = "Western India",
                capital = "Gandhinagar",
                shortDescription = "Cradle of maritime trade, Harappan ports like Lothal, Solanki architecture, and birthplace of Mahatma Gandhi.",
                imageUrl = "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 2400 BCE – 1900 BCE", title = "Lothal & Dholavira Maritime Hubs",
                        description = "Lothal featured the world's earliest known tidal dockyard, while Dholavira displayed sophisticated water harvesting reservoirs.",
                        keyEvents = listOf("Lothal Dockyard maritime commerce", "Dholavira stone architecture", "Bead-making and shell ornament trade"),
                        keyRulers = listOf("Harappan Maritime Guilds"),
                        imageUrl = "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80",
                        images = listOf("https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80")
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "942 AD – 1244 AD", title = "Solanki Dynasty Golden Age",
                        description = "The Solanki (Chaulukya) Dynasty fostered architectural marvels such as Rani ki Vav stepwell in Patan and Sun Temple in Modhera.",
                        keyEvents = listOf("Rani ki Vav stepwell construction", "Modhera Sun Temple architecture", "Patronage of Jain scholars"),
                        keyRulers = listOf("Siddharaja Jayasimha", "Kumarapala", "Bhimdev I"),
                        imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                        images = listOf("https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80")
                    )
                )
            )
        )

        // 3. Delhi
        states.add(
            State(
                slug = "delhi",
                name = "Delhi",
                region = "Northern India",
                capital = "New Delhi",
                shortDescription = "Historic imperial capital of seven cities, seat of Tomar Rajputs, Delhi Sultanate, Mughals, and modern India.",
                imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 1000 BCE – 1052 AD", title = "Indraprastha & Tomar Rajput Foundation",
                        description = "Identified with mythological Indraprastha from Mahabharata; Anangpal Tomar founded Lal Kot in 1052 AD, establishing historic Delhi.",
                        keyEvents = listOf("Construction of Lal Kot fortress", "Surajkund reservoir construction", "Establishment of early urban habitation"),
                        keyRulers = listOf("Anangpal Tomar II", "Prithviraj Chauhan"),
                        imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "1206 AD – 1526 AD", title = "Delhi Sultanate & Qutub Minar",
                        description = "Five dynasties ruled from Delhi, erecting Qutub Minar, Tughlaqabad Fort, and establishing central administrative networks.",
                        keyEvents = listOf("Qutub Minar construction", "Reign of Razia Sultana", "Expansion under Alauddin Khalji"),
                        keyRulers = listOf("Qutb-ud-din Aibak", "Razia Sultana", "Alauddin Khalji"),
                        imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 4. Maharashtra
        states.add(
            State(
                slug = "maharashtra",
                name = "Maharashtra",
                region = "Western India",
                capital = "Mumbai",
                shortDescription = "Land of Maratha Empire, Chhatrapati Shivaji Maharaj, rock-cut Ajanta & Ellora caves, and economic power center.",
                imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "200 BCE – 800 AD", title = "Satavahana Dynasty & Ajanta Caves",
                        description = "Satavahana, Rashtrakuta, and Vakataka rulers carved rock-cut sanctuaries at Ajanta, Ellora (Kailash Temple), and Elephanta.",
                        keyEvents = listOf("Carving of Kailashnath Temple (Ellora Cave 16)", "Ajanta Cave Buddhist murals", "Roman maritime trade"),
                        keyRulers = listOf("Gautamiputra Satakarni", "Krishna I"),
                        imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "1674 AD – 1818 AD", title = "Maratha Empire & Hindavi Swarajya",
                        description = "Chhatrapati Shivaji Maharaj founded the Maratha Empire, pioneering guerilla tactics (Ganimi Kava) and formidable hill forts.",
                        keyEvents = listOf("Coronation of Chhatrapati Shivaji Maharaj at Raigad (1674 AD)", "Peshwa expansion", "Sea forts like Sindhudurg"),
                        keyRulers = listOf("Chhatrapati Shivaji Maharaj", "Chhatrapati Sambhaji Maharaj", "Peshwa Baji Rao I"),
                        imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 5. Uttar Pradesh
        states.add(
            State(
                slug = "uttar-pradesh",
                name = "Uttar Pradesh",
                region = "Northern India",
                capital = "Lucknow",
                shortDescription = "Heartland of Gangetic civilization, birthland of Lord Rama and Krishna, Sarnath, and Mughal architectural masterworks.",
                imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 1500 BCE – 500 BCE", title = "Vedic Age & Sacred Cities",
                        description = "Varanasi (Kashi) evolved as the world's oldest living cultural city, while Sarnath hosted Gautam Buddha's first sermon.",
                        keyEvents = listOf("Buddha's Dhamma Wheel sermon at Sarnath", "Vedic literature compilation", "Mathura sculpture school"),
                        keyRulers = listOf("Mahajanapada Kings", "Emperor Ashoka"),
                        imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "1526 AD – 1707 AD", title = "Mughal Imperial Renaissance at Agra",
                        description = "Agra served as imperial capital under Akbar and Shah Jahan, featuring Taj Mahal, Agra Fort, and Fatehpur Sikri.",
                        keyEvents = listOf("Construction of Taj Mahal", "Creation of Fatehpur Sikri", "Agra Fort expansion"),
                        keyRulers = listOf("Akbar", "Shah Jahan"),
                        imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 6. Tamil Nadu
        states.add(
            State(
                slug = "tamil-nadu",
                name = "Tamil Nadu",
                region = "Southern India",
                capital = "Chennai",
                shortDescription = "Cradle of Dravidian temple architecture, Sangam Tamil literature, Chola naval power, and classical Bharatnatyam.",
                imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 300 BCE – 300 AD", title = "Sangam Period & Chera Chola Pandya Triumvirate",
                        description = "Classical Tamil literature flourished during Sangam academies, alongside extensive maritime trade with Rome and Greece.",
                        keyEvents = listOf("Sangam literary assemblies at Madurai", "Port of Poompuhar trade", "Kallanai Dam construction"),
                        keyRulers = listOf("Karikala Chola", "Nedunjeliyan"),
                        imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "850 AD – 1279 AD", title = "Chola Imperial Golden Era",
                        description = "Rajaraja Chola I and Rajendra Chola I built naval armadas that expanded Chola influence to Southeast Asia and built Brihadeeswarar Temple.",
                        keyEvents = listOf("Brihadeeswarar Great Living Chola Temple construction", "Naval expedition to Southeast Asia", "Bronze casting mastery"),
                        keyRulers = listOf("Rajaraja Chola I", "Rajendra Chola I"),
                        imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 7. Goa
        states.add(
            State(
                slug = "goa",
                name = "Goa",
                region = "Western India",
                capital = "Panaji",
                shortDescription = "Pearl of the Orient, renowned for sun-kissed beaches, Portuguese colonial architecture, Basilica of Bom Jesus, and vibrant culture.",
                imageUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 300 BCE – 1312 AD", title = "Kadamba Dynasty & Ancient Gomantak",
                        description = "Ruled by Bhojas, Satavahanas, and Kadambas of Goa who developed Chandor and Gopakapattana as thriving international ports.",
                        keyEvents = listOf("Kadamba dynasty maritime expansion", "Construction of Tambdi Surla Mahadev Temple", "Arab & Persian trade interactions"),
                        keyRulers = listOf("Jayakeshi I", "Shivachitta Permadideva"),
                        imageUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Colonial Era", period = "1510 AD – 1961 AD", title = "Portuguese Era & Operation Vijay",
                        description = "Afonso de Albuquerque conquered Goa in 1510 AD. Goa served as the capital of Portuguese India for 451 years until liberation by Indian Armed Forces in 1961.",
                        keyEvents = listOf("Portuguese Conquest of Goa (1510 AD)", "Construction of Basilica of Bom Jesus", "Operation Vijay Liberation of Goa (December 19, 1961)"),
                        keyRulers = listOf("Afonso de Albuquerque", "General K. P. Candeth"),
                        imageUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 8. Punjab
        states.add(
            State(
                slug = "punjab",
                name = "Punjab",
                region = "North-West India",
                capital = "Chandigarh",
                shortDescription = "Land of Five Rivers, Golden Temple, Sikh Empire of Maharaja Ranjit Singh, and granary of India.",
                imageUrl = "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 2600 BCE – 1500 BCE", title = "Harappan Civilization & Sapta Sindhu",
                        description = "Ropar (Rupnagar) was a major Harappan urban center. Punjab formed the core of the ancient Vedic Sapta Sindhu.",
                        keyEvents = listOf("Harappan Ropar urban excavations", "Vedic Sapta Sindhu hymns composition", "Taxila trade routes"),
                        keyRulers = listOf("Ancient Vedic Clan Chiefs", "King Porus"),
                        imageUrl = "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "1799 AD – 1849 AD", title = "Sikh Empire & Maharaja Ranjit Singh",
                        description = "Maharaja Ranjit Singh unified Misls into a sovereign empire stretching from Sutlej to Khyber Pass, gold-plating Sri Harmandir Sahib.",
                        keyEvents = listOf("Unification of Sikh Misls", "Gold-plating of Sri Harmandir Sahib (Golden Temple)", "Secular rule & military modernization"),
                        keyRulers = listOf("Maharaja Ranjit Singh", "Hari Singh Nalwa"),
                        imageUrl = "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 9. Karnataka
        states.add(
            State(
                slug = "karnataka",
                name = "Karnataka",
                region = "Southern India",
                capital = "Bengaluru",
                shortDescription = "Cradle of stone architecture, Vijayanagara Empire at Hampi, Mysore Palace, and India's tech innovation hub.",
                imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "345 AD – 1000 AD", title = "Kadambas, Badami Chalukyas & Rashtrakutas",
                        description = "Badami Chalukyas built cave temples at Badami and stone structural temples at Pattadakal and Aihole.",
                        keyEvents = listOf("Carving of Badami Cave Temples", "Pattadakal UNESCO World Heritage temples", "Aihole temple experimentation"),
                        keyRulers = listOf("Pulakeshin II", "Mayurasharma"),
                        imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "1336 AD – 1565 AD", title = "Vijayanagara Empire Golden Age at Hampi",
                        description = "Harihara and Bukka founded Vijayanagara at Hampi on the banks of Tungabhadra, becoming a world metropolis.",
                        keyEvents = listOf("Construction of Stone Chariot & Vittala Temple", "Foreign trade in gems and horses", "Patronage of Carnatic music and literature"),
                        keyRulers = listOf("Krishnadevaraya", "Harihara I"),
                        imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 10. West Bengal
        states.add(
            State(
                slug = "west-bengal",
                name = "West Bengal",
                region = "Eastern India",
                capital = "Kolkata",
                shortDescription = "Cultural capital of India, Bengal Renaissance, Rabindranath Tagore, Victoria Memorial, and Sundarbans.",
                imageUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 300 BCE – 1150 AD", title = "Gangaridai, Maurya & Pala Dynasty",
                        description = "Gangaridai empire resisted Alexander the Great. Pala Empire made Bengal a global center for Mahayana Buddhist learning.",
                        keyEvents = listOf("Pala Buddhist monastic art", "Somapura Mahavihara patronage", "Maritime commerce via Tamralipta port"),
                        keyRulers = listOf("Gopala I", "Dharmapala"),
                        imageUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Colonial Era", period = "1757 AD – 1947 AD", title = "Bengal Renaissance & Freedom Movement",
                        description = "Battle of Plassey (1757) marked British East India Company rule. Bengal became the crucible of intellectual Renaissance and freedom movement.",
                        keyEvents = listOf("Battle of Plassey (1757 AD)", "Bengal Renaissance (Tagore, Vivekananda)", "Subhas Chandra Bose & Azad Hind Fauj"),
                        keyRulers = listOf("Nawab Siraj-ud-Daulah", "Rabindranath Tagore", "Netaji Subhas Chandra Bose"),
                        imageUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 11. Kerala
        states.add(
            State(
                slug = "kerala",
                name = "Kerala",
                region = "Southern India",
                capital = "Thiruvananthapuram",
                shortDescription = "God's Own Country, birthplace of Chera Dynasty, ancient maritime spice trade with Rome, Padmanabhaswamy Temple, and Kalarippayattu.",
                imageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 300 BCE – 1102 AD", title = "Chera Empire & Muziris Global Spice Port",
                        description = "Muziris served as the world's chief spice trading hub connecting India with Roman, Greek, and Arab empires.",
                        keyEvents = listOf("Muziris port pepper trade with Roman Empire", "Propagation of Kalarippayattu martial arts"),
                        keyRulers = listOf("Uthiyan Cheralathan", "Kulasekhara Varman"),
                        imageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Colonial Era", period = "1729 AD – 1949 AD", title = "Travancore Kingdom & Battle of Colachel",
                        description = "Marthanda Varma defeated the Dutch East India Company at Colachel (1741), marking the first Asian naval defeat of a European power.",
                        keyEvents = listOf("Battle of Colachel (1741 AD)", "Dedication of Travancore to Lord Padmanabha"),
                        keyRulers = listOf("Anizham Thirunal Marthanda Varma", "Swathi Thirunal"),
                        imageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 12. Madhya Pradesh
        states.add(
            State(
                slug = "madhya-pradesh",
                name = "Madhya Pradesh",
                region = "Central India",
                capital = "Bhopal",
                shortDescription = "Heart of India, home to UNESCO World Heritage Sanchi Stupa, Khajuraho erotic stone temples, Bhimbetka rock shelters, and Gwalior Fort.",
                imageUrl = "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 300 BCE – 100 BCE", title = "Maurya & Shunga Era at Sanchi Stupa",
                        description = "Emperor Ashoka commissioned the Great Stupa at Sanchi, creating a masterpiece of Buddhist architecture.",
                        keyEvents = listOf("Construction of Great Stupa 1 by Emperor Ashoka", "Shunga dynasty stone gateways creation"),
                        keyRulers = listOf("Emperor Ashoka", "Pushyamitra Shunga"),
                        imageUrl = "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "950 AD – 1050 AD", title = "Chandela Dynasty & Khajuraho Temples",
                        description = "Chandela Rajput kings built 85 intricate stone temples at Khajuraho celebrating spiritual art.",
                        keyEvents = listOf("Construction of Kandariya Mahadeva Temple", "Gwalior Fort defense under Tomar Rajputs"),
                        keyRulers = listOf("Yashovarman", "Dhanga", "Rani Durgavati"),
                        imageUrl = "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 13. Bihar
        states.add(
            State(
                slug = "bihar",
                name = "Bihar",
                region = "Eastern India",
                capital = "Patna",
                shortDescription = "Birthplace of Buddhism and Jainism, imperial seat of Maurya & Gupta Empires, Mahavira, Lord Buddha, and ancient Nalanda University.",
                imageUrl = "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 544 BCE – 232 BCE", title = "Magadha Empire & Emperor Ashoka",
                        description = "Pataliputra served as imperial capital of Magadha, Nanda, and Maurya empires under Chandragupta and Ashoka.",
                        keyEvents = listOf("Enlightenment of Gautam Buddha at Bodh Gaya", "Establishment of Maurya Empire under Chandragupta & Chanakya"),
                        keyRulers = listOf("Bimbisara", "Chandragupta Maurya", "Emperor Ashoka"),
                        imageUrl = "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Ancient Era", period = "240 AD – 550 AD", title = "Nalanda University Golden Era",
                        description = "Nalanda University flourished as a global residential seat of higher learning.",
                        keyEvents = listOf("Founding of Nalanda Mahavihara by Kumaragupta I", "Gupta Empire Golden Age of science"),
                        keyRulers = listOf("Chandragupta II (Vikramaditya)", "Kumaragupta I"),
                        imageUrl = "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 14. Odisha
        states.add(
            State(
                slug = "odisha",
                name = "Odisha",
                region = "Eastern India",
                capital = "Bhubaneswar",
                shortDescription = "Land of Kalinga, magnificent Konark Sun Temple chariot architecture, Jagannath Temple at Puri, and ancient seafaring Sadhabas.",
                imageUrl = "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 261 BCE", title = "Kalinga War & Ashoka's Transformation",
                        description = "The Kalinga War fought on Daya river led Emperor Ashoka to renounce warfare and adopt Dhamma.",
                        keyEvents = listOf("Kalinga War on Daya river", "Inscription of Dhauli Ashokan Edicts"),
                        keyRulers = listOf("Kalinga Generals", "Emperor Ashoka"),
                        imageUrl = "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "1035 AD – 1250 AD", title = "Eastern Ganga Dynasty & Konark Sun Temple",
                        description = "Narasimhadeva I built Konark Sun Temple as a giant stone chariot with 24 carved wheels.",
                        keyEvents = listOf("Konark Sun Temple UNESCO site construction", "Jagannath Puri Temple grand chariot procession"),
                        keyRulers = listOf("Anantavarman Chodaganga", "Narasimhadeva I"),
                        imageUrl = "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 15. Assam
        states.add(
            State(
                slug = "assam",
                name = "Assam",
                region = "North-East India",
                capital = "Dispur",
                shortDescription = "Land of the mighty Brahmaputra, 600-year invincible Ahom Kingdom, Lachit Borphukan, Kamakhya Temple, and tea estates.",
                imageUrl = "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 350 AD – 1140 AD", title = "Kamarupa Kingdom & Kamakhya Shrine",
                        description = "Kamarupa kings established a powerful Eastern kingdom visited by pilgrim Xuanzang.",
                        keyEvents = listOf("Reign of Bhaskaravarman of Kamarupa", "Patronage of ancient Kamakhya shrine"),
                        keyRulers = listOf("Pushyavarman", "Bhaskaravarman"),
                        imageUrl = "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80"
                    ),
                    TimelineEntry(
                        id = 2, era = "Medieval Era", period = "1228 AD – 1826 AD", title = "Ahom Dynasty & Battle of Saraighat",
                        description = "Chaolung Sukaphaa founded Ahom Kingdom. General Lachit Borphukan defeated Mughal forces at Battle of Saraighat (1671).",
                        keyEvents = listOf("Battle of Saraighat (1671 AD)", "Construction of Rang Ghar amphitheater at Sivasagar"),
                        keyRulers = listOf("Sukaphaa", "Lachit Borphukan", "Rudra Singha"),
                        imageUrl = "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 16. Jammu & Kashmir
        states.add(
            State(
                slug = "jammu-and-kashmir",
                name = "Jammu & Kashmir",
                region = "Northern India",
                capital = "Srinagar / Jammu",
                shortDescription = "Paradise on Earth, ancient Martand Sun Temple, Karkota dynasty empire of Lalitaditya, Silk Route trade, and Dal Lake.",
                imageUrl = "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "625 AD – 855 AD", title = "Karkota Dynasty & Emperor Lalitaditya",
                        description = "Lalitaditya Muktapida constructed Martand Sun Temple and expanded a trans-Himalayan empire.",
                        keyEvents = listOf("Construction of Martand Sun Temple", "Kashmir Shaivism philosophy compilation"),
                        keyRulers = listOf("Lalitaditya Muktapida", "Durlabhavardhana"),
                        imageUrl = "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 17. Telangana
        states.add(
            State(
                slug = "telangana",
                name = "Telangana",
                region = "Southern India",
                capital = "Hyderabad",
                shortDescription = "Land of Kakatiya Dynasty fortresses, Thousand Pillar Temple, Warangal Gateways, Charminar, and Koh-i-Noor diamond heritage.",
                imageUrl = "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Medieval Era", period = "1163 AD – 1323 AD", title = "Kakatiya Dynasty & Queen Rudrama Devi",
                        description = "The Kakatiyas built Warangal Fort, Ramappa Temple (UNESCO site), and advanced irrigation chain tanks.",
                        keyEvents = listOf("Construction of Ramappa Temple with floating bricks", "Warangal Fort Keerthi Toranas gateways"),
                        keyRulers = listOf("Rani Rudrama Devi", "Prataparudra", "Ganapati Deva"),
                        imageUrl = "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        // 18. Himachal Pradesh
        states.add(
            State(
                slug = "himachal-pradesh",
                name = "Himachal Pradesh",
                region = "Northern India",
                capital = "Shimla",
                shortDescription = "Devbhumi (Abode of Gods), ancient Trigarta Kingdom, rock-cut Masrur temples, Himalayan fortresses, and Kangra art.",
                imageUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(
                        id = 1, era = "Ancient Era", period = "c. 500 BCE – 1800 AD", title = "Trigarta Kingdom & Kangra Miniature Art",
                        description = "Katoch Dynasty ruled Kangra Fort, patronizing sublime Kangra miniature school of painting.",
                        keyEvents = listOf("Defense of Kangra Fort against ancient invasions", "Carving of Masrur Rock Cut Monolithic Temples"),
                        keyRulers = listOf("Maharaja Sansar Chand Katoch", "Susharma Chand"),
                        imageUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80"
                    )
                )
            )
        )

        return states
    }
}

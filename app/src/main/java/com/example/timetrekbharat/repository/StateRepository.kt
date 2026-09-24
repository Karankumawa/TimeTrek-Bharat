package com.example.timetrekbharat.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timetrekbharat.db.AppDatabase
import com.example.timetrekbharat.model.State
import com.example.timetrekbharat.model.TimelineEntry
import com.example.timetrekbharat.network.RetrofitClient
import java.util.concurrent.Executors

class StateRepository(context: Context) {

    private val stateDao = AppDatabase.getInstance(context).stateDao()
    private val executorService = Executors.newFixedThreadPool(2)

    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>(null)

    init {
        // Pre-populate database with comprehensive local state timeline dataset
        executorService.execute { seedDatabaseIfEmpty() }
    }

    fun getCachedStates(): LiveData<List<State>> = stateDao.getAllStates()

    fun searchCachedStates(query: String?): LiveData<List<State>> {
        return if (query.isNullOrBlank()) {
            stateDao.getAllStates()
        } else {
            stateDao.searchStates(query.trim())
        }
    }

    fun getCachedStateDetail(slug: String): LiveData<State> = stateDao.getStateBySlug(slug)

    fun fetchStatesFromNetwork(searchQuery: String?) {
        isLoading.postValue(true)
        errorMessage.postValue(null)
        executorService.execute {
            try {
                val response = RetrofitClient.api.getAllStates(searchQuery).execute()
                if (response.isSuccessful && response.body() != null && response.body()!!.isSuccess) {
                    val networkStates = response.body()!!.data
                    if (!networkStates.isNullOrEmpty()) {
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
        executorService.execute {
            try {
                val response = RetrofitClient.api.getStateDetail(stateSlug).execute()
                if (response.isSuccessful && response.body() != null && response.body()!!.isSuccess) {
                    val networkState = response.body()!!.data
                    if (networkState != null) {
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

    private fun seedDatabaseIfEmpty() {
        val sampleStates = getComprehensiveStatesList()
        stateDao.insertStates(sampleStates)
    }

    private fun getComprehensiveStatesList(): List<State> {
        val states = ArrayList<State>()

        // 1. Rajasthan
        val rajasthan = State(
            slug = "rajasthan",
            name = "Rajasthan",
            region = "North-West India",
            capital = "Jaipur",
            shortDescription = "Land of Kings, renowned for majestic hill forts, Thar Desert, royal Rajput heritage, and vibrant culture.",
            imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897703ca6?q=80&w=800&auto=format&fit=crop",
            bannerUrl = "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?q=80&w=1200&auto=format&fit=crop",
            timeline = listOf(
                TimelineEntry(
                    id = 1, era = "Ancient Era", period = "c. 2500 BCE – 1500 BCE", title = "Indus Valley & Kalibangan Civilization",
                    description = "Kalibangan in Hanumangarh district was a major provincial capital of the Indus Valley Civilization featuring ploughed field surfaces and advanced brick fortifications.",
                    keyEvents = listOf("Early Harappan urban phases", "Earliest known ploughed field", "Fire altars ritual worship"),
                    keyRulers = listOf("Indus Valley Clan Leaders"),
                    imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?q=80&w=800&auto=format&fit=crop"
                ),
                TimelineEntry(
                    id = 2, era = "Medieval Era", period = "700 AD – 1192 AD", title = "Rise of Rajput Dynasties & Prithviraj Chauhan",
                    description = "The Gurjara-Pratihara empire and later Chauhan, Guhila, and Rathore dynasties established fortified kingdoms across Mewar, Marwar, and Dhundhar.",
                    keyEvents = listOf("Battle of Tarain (1191 & 1192 AD)", "Construction of Chittorgarh Fort", "Defense against early Arab incursions"),
                    keyRulers = listOf("Prithviraj Chauhan", "Bappa Rawal", "Nagabhata I"),
                    imageUrl = "https://images.unsplash.com/photo-1599661046289-e31897703ca6?q=80&w=800&auto=format&fit=crop"
                ),
                TimelineEntry(
                    id = 3, era = "Medieval Era", period = "1526 AD – 1707 AD", title = "Mewar Resistance & Maharana Pratap",
                    description = "Maharana Pratap of Mewar valiantly defended Rajput sovereignty against Mughal Emperor Akbar, refusing to submit to foreign vassalage.",
                    keyEvents = listOf("Battle of Haldighati (1576 AD)", "Establishment of Jaipur city (1727 AD)", "Construction of Jantar Mantar observatory"),
                    keyRulers = listOf("Maharana Pratap", "Rana Sanga", "Sawai Jai Singh II"),
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?q=80&w=800&auto=format&fit=crop"
                )
            )
        )
        states.add(rajasthan)

        // 2. Gujarat
        val gujarat = State(
            slug = "gujarat",
            name = "Gujarat",
            region = "Western India",
            capital = "Gandhinagar",
            shortDescription = "Cradle of maritime trade, Harappan ports like Lothal, Solanki architecture, and birthplace of Mahatma Gandhi.",
            imageUrl = "https://images.unsplash.com/photo-1609946850020-f571342d326e?q=80&w=800&auto=format&fit=crop",
            bannerUrl = "https://images.unsplash.com/photo-1609946850020-f571342d326e?q=80&w=1200&auto=format&fit=crop",
            timeline = listOf(
                TimelineEntry(
                    id = 1, era = "Ancient Era", period = "c. 2400 BCE – 1900 BCE", title = "Lothal & Dholavira Maritime Hubs",
                    description = "Lothal featured the world's earliest known tidal dockyard, while Dholavira displayed sophisticated water harvesting reservoirs.",
                    keyEvents = listOf("Lothal Dockyard maritime commerce", "Dholavira stone architecture", "Bead-making and shell ornament trade"),
                    keyRulers = listOf("Harappan Maritime Guilds"),
                    imageUrl = "https://images.unsplash.com/photo-1609946850020-f571342d326e?q=80&w=800&auto=format&fit=crop"
                ),
                TimelineEntry(
                    id = 2, era = "Medieval Era", period = "942 AD – 1244 AD", title = "Solanki Dynasty Golden Age",
                    description = "The Solanki (Chaulukya) Dynasty fostered architectural marvels such as Rani ki Vav stepwell in Patan and Sun Temple in Modhera.",
                    keyEvents = listOf("Rani ki Vav stepwell construction", "Modhera Sun Temple architecture", "Patronage of Jain scholars"),
                    keyRulers = listOf("Siddharaja Jayasimha", "Kumarapala", "Bhimdev I"),
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?q=80&w=800&auto=format&fit=crop"
                )
            )
        )
        states.add(gujarat)

        // 3. Delhi
        val delhi = State(
            slug = "delhi",
            name = "Delhi",
            region = "Northern India",
            capital = "New Delhi",
            shortDescription = "Historic imperial capital of seven cities, seat of Tomar Rajputs, Delhi Sultanate, Mughals, and modern India.",
            imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=800&auto=format&fit=crop",
            bannerUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=1200&auto=format&fit=crop",
            timeline = listOf(
                TimelineEntry(
                    id = 1, era = "Ancient Era", period = "c. 1000 BCE – 1052 AD", title = "Indraprastha & Tomar Rajput Foundation",
                    description = "Identified with mythological Indraprastha from Mahabharata; Anangpal Tomar founded Lal Kot in 1052 AD, establishing historic Delhi.",
                    keyEvents = listOf("Construction of Lal Kot fortress", "Surajkund reservoir construction", "Establishment of early urban habitation"),
                    keyRulers = listOf("Anangpal Tomar II", "Prithviraj Chauhan"),
                    imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=800&auto=format&fit=crop"
                ),
                TimelineEntry(
                    id = 2, era = "Medieval Era", period = "1206 AD – 1526 AD", title = "Delhi Sultanate & Qutub Minar",
                    description = "Five dynasties ruled from Delhi, erecting Qutub Minar, Tughlaqabad Fort, and establishing central administrative networks.",
                    keyEvents = listOf("Qutub Minar construction", "Reign of Razia Sultana", "Expansion under Alauddin Khalji"),
                    keyRulers = listOf("Qutb-ud-din Aibak", "Razia Sultana", "Alauddin Khalji"),
                    imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=800&auto=format&fit=crop"
                )
            )
        )
        states.add(delhi)

        // 4. Maharashtra
        val maharashtra = State(
            slug = "maharashtra",
            name = "Maharashtra",
            region = "Western India",
            capital = "Mumbai",
            shortDescription = "Land of Maratha Empire, Chhatrapati Shivaji Maharaj, rock-cut Ajanta & Ellora caves, and economic power center.",
            imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=800&auto=format&fit=crop",
            bannerUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=1200&auto=format&fit=crop",
            timeline = listOf(
                TimelineEntry(
                    id = 1, era = "Ancient Era", period = "200 BCE – 800 AD", title = "Satavahana Dynasty & Ajanta Caves",
                    description = "Satavahana, Rashtrakuta, and Vakataka rulers carved rock-cut sanctuaries at Ajanta, Ellora (Kailash Temple), and Elephanta.",
                    keyEvents = listOf("Carving of Kailashnath Temple (Ellora Cave 16)", "Ajanta Cave Buddhist murals", "Roman maritime trade"),
                    keyRulers = listOf("Gautamiputra Satakarni", "Krishna I"),
                    imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=800&auto=format&fit=crop"
                ),
                TimelineEntry(
                    id = 2, era = "Medieval Era", period = "1674 AD – 1818 AD", title = "Maratha Empire & Hindavi Swarajya",
                    description = "Chhatrapati Shivaji Maharaj founded the Maratha Empire, pioneering guerilla tactics (Ganimi Kava) and formidable hill forts.",
                    keyEvents = listOf("Coronation of Chhatrapati Shivaji Maharaj at Raigad (1674 AD)", "Peshwa expansion", "Sea forts like Sindhudurg"),
                    keyRulers = listOf("Chhatrapati Shivaji Maharaj", "Chhatrapati Sambhaji Maharaj", "Peshwa Baji Rao I"),
                    imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=800&auto=format&fit=crop"
                )
            )
        )
        states.add(maharashtra)

        // 5. Uttar Pradesh
        val uttarPradesh = State(
            slug = "uttar-pradesh",
            name = "Uttar Pradesh",
            region = "Northern India",
            capital = "Lucknow",
            shortDescription = "Heartland of Gangetic civilization, birthland of Lord Rama and Krishna, Sarnath, and Mughal architectural masterworks.",
            imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=800&auto=format&fit=crop",
            bannerUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=1200&auto=format&fit=crop",
            timeline = listOf(
                TimelineEntry(
                    id = 1, era = "Ancient Era", period = "c. 1500 BCE – 500 BCE", title = "Vedic Age & Sacred Cities",
                    description = "Varanasi (Kashi) evolved as the world's oldest living cultural city, while Sarnath hosted Gautam Buddha's first sermon.",
                    keyEvents = listOf("Buddha's Dhamma Wheel sermon at Sarnath", "Vedic literature compilation", "Mathura sculpture school"),
                    keyRulers = listOf("Mahajanapada Kings", "Emperor Ashoka"),
                    imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=800&auto=format&fit=crop"
                ),
                TimelineEntry(
                    id = 2, era = "Medieval Era", period = "1526 AD – 1707 AD", title = "Mughal Imperial Renaissance at Agra",
                    description = "Agra served as imperial capital under Akbar and Shah Jahan, featuring Taj Mahal, Agra Fort, and Fatehpur Sikri.",
                    keyEvents = listOf("Construction of Taj Mahal", "Creation of Fatehpur Sikri", "Agra Fort expansion"),
                    keyRulers = listOf("Akbar", "Shah Jahan"),
                    imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=800&auto=format&fit=crop"
                )
            )
        )
        states.add(uttarPradesh)

        // 6. Tamil Nadu
        val tamilNadu = State(
            slug = "tamil-nadu",
            name = "Tamil Nadu",
            region = "Southern India",
            capital = "Chennai",
            shortDescription = "Cradle of Dravidian temple architecture, Sangam Tamil literature, Chola naval power, and classical Bharatnatyam.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?q=80&w=800&auto=format&fit=crop",
            bannerUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?q=80&w=1200&auto=format&fit=crop",
            timeline = listOf(
                TimelineEntry(
                    id = 1, era = "Ancient Era", period = "c. 300 BCE – 300 AD", title = "Sangam Period & Chera Chola Pandya Triumvirate",
                    description = "Classical Tamil literature flourished during Sangam academies, alongside extensive maritime trade with Rome and Greece.",
                    keyEvents = listOf("Sangam literary assemblies at Madurai", "Port of Poompuhar trade", "Kallanai Dam construction"),
                    keyRulers = listOf("Karikala Chola", "Nedunjeliyan"),
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?q=80&w=800&auto=format&fit=crop"
                ),
                TimelineEntry(
                    id = 2, era = "Medieval Era", period = "850 AD – 1279 AD", title = "Chola Imperial Golden Era",
                    description = "Rajaraja Chola I and Rajendra Chola I built naval armadas that expanded Chola influence to Southeast Asia and built Brihadeeswarar Temple.",
                    keyEvents = listOf("Brihadeeswarar Great Living Chola Temple construction", "Naval expedition to Southeast Asia", "Bronze casting mastery"),
                    keyRulers = listOf("Rajaraja Chola I", "Rajendra Chola I"),
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?q=80&w=800&auto=format&fit=crop"
                )
            )
        )
        states.add(tamilNadu)

        return states
    }
}

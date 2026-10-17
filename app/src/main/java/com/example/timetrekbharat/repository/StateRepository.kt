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
     * Lists files in assets/ and state-specific subfolders (e.g. rajasthanimages/).
     * Returns file:///android_asset/... URI so manual edits in assets take effect immediately on app run and in Room DB.
     */
    private fun resolveStateImageUrl(slug: String, fallbackUrl: String): String {
        val cleanSlug = slug.replace("-", "").replace("_", "").lowercase()
        val dirsToCheck = listOf(
            "statesimage",
            "statesimages",
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

        for (dir in dirsToCheck) {
            try {
                val files = appContext.assets.list(dir)?.toList() ?: emptyList()

                // 1. Direct match on filename without extension
                val matchedFile = files.firstOrNull { fileName ->
                    val nameWithoutExt = fileName.substringBeforeLast(".")
                    nameWithoutExt.equals(slug, ignoreCase = true) ||
                    nameWithoutExt.replace("-", "").replace("_", "").equals(cleanSlug, ignoreCase = true) ||
                    nameWithoutExt.contains(cleanSlug, ignoreCase = true)
                }
                if (matchedFile != null) {
                    return "file:///android_asset/$dir/$matchedFile"
                }

                // 2. If folder is state-specific (e.g. rajasthanimages) and contains images, take first image
                if (dir.contains("images") || dir.contains(slug) || dir.contains(cleanSlug)) {
                    val firstImage = files.firstOrNull { f ->
                        f.endsWith(".jpg", ignoreCase = true) ||
                        f.endsWith(".jpeg", ignoreCase = true) ||
                        f.endsWith(".png", ignoreCase = true) ||
                        f.endsWith(".webp", ignoreCase = true)
                    }
                    if (firstImage != null) {
                        return "file:///android_asset/$dir/$firstImage"
                    }
                }
            } catch (_: Exception) {
                // Ignore missing directory
            }
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
                shortDescription = "Land of Kings, renowned for majestic hill forts, Thar Desert, royal Rajput heritage, and vibrant folk culture.",
                imageUrl = "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "c. 2500 BCE – 1500 BCE", "Indus Valley & Kalibangan Civilization", "Earliest urban settlements and terracotta fire altars along ancient Saraswati riverbed.", listOf("Indus Valley Trade", "Agricultural revolution"), listOf("Local Clan Chieftains")),
                    TimelineEntry(2, "Medieval Era", "8th – 18th Century CE", "Rajput Clans & Hill Fort Resistance", "Establishment of Mewar, Marwar, Dhundhar, and Amber kingdoms with hill fort fortifications.", listOf("Kumbhalgarh Wall", "Battle of Haldighati 1576"), listOf("Maharana Pratap", "Rana Sanga", "Maharana Kumbha")),
                    TimelineEntry(3, "Modern Era", "1727 – Present", "Foundation of Pink City Jaipur & Integration", "Maharaja Sawai Jai Singh II founded Jaipur with Vastu Shastra planning. Integrated into Republic of India in 1949.", listOf("Hawa Mahal 1799", "UNESCO Hill Forts"), listOf("Sawai Jai Singh II", "Maharaja Man Singh II"))
                )
            ),
            State(
                slug = "gujarat",
                name = "Gujarat",
                capital = "Gandhinagar",
                region = "West",
                shortDescription = "Land of Solanki Architecture, Dholavira Indus Valley site, Rani ki Vav stepwell, Sabarmati Ashram, and Statue of Unity.",
                imageUrl = "https://images.unsplash.com/photo-1609949279531-cf48d64bed89?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1609949279531-cf48d64bed89?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "c. 2600 BCE", "Dholavira & Lothal Port City", "Ancient maritime port with world's oldest tidal dockyard.", listOf("Harappan Maritime Trade", "Lothal Dockyard"), listOf("Harappan Merchants")),
                    TimelineEntry(2, "Medieval Era", "10th – 13th Century CE", "Solanki Dynasty Golden Age", "Construction of Rani ki Vav stepwell and Modhera Sun Temple.", listOf("Rani ki Vav 1063 CE", "Modhera Temple"), listOf("Queen Udayamati", "King Bhimdev I")),
                    TimelineEntry(3, "Modern Era", "1930 – Present", "Dandi March & Sardar Patel Legacy", "Mahatma Gandhi's Sabarmati Salt March and Sardar Patel's integration of 565 princely states.", listOf("Dandi Salt March 1930", "Statue of Unity 2018"), listOf("Mahatma Gandhi", "Sardar Vallabhbhai Patel"))
                )
            ),
            State(
                slug = "karnataka",
                name = "Karnataka",
                capital = "Bengaluru",
                region = "South",
                shortDescription = "Cradle of Vijayanagara Empire, Chalukya rock-cut architecture at Badami, Hampi UNESCO site, and Silicon Valley Bengaluru.",
                imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "6th – 8th Century CE", "Badami Chalukya & Pattadakal Temples", "Pioneered Dravidian and Nagara cave temple architecture.", listOf("Badami Cave Carvings", "Pattadakal UNESCO Site"), listOf("Pulakeshin II", "Kirtivarman I")),
                    TimelineEntry(2, "Medieval Era", "1336 – 1646 CE", "Vijayanagara Golden Empire", "Global trade metropolis, stone chariot, musical pillars, and grand Ranga Mantapa.", listOf("Foundation of Hampi", "Vittala Temple Stone Chariot"), listOf("Emperor Krishnadevaraya", "Harihara I", "Bukka I")),
                    TimelineEntry(3, "Modern Era", "18th C – Present", "Mysore Kingdom & IT Revolution", "Rule of Hyder Ali, Tipu Sultan, and evolution of Bengaluru as India's premier tech hub.", listOf("Mysore Palace", "HAL & ISRO Headquarters"), listOf("Tipu Sultan", "Sir M. Visvesvaraya"))
                )
            ),
            State(
                slug = "maharashtra",
                name = "Maharashtra",
                capital = "Mumbai",
                region = "West",
                shortDescription = "Land of Maratha Swarajya, Chhatrapati Shivaji Maharaj hill forts, Ajanta & Ellora cave temples, and Financial Capital Mumbai.",
                imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "2nd Century BCE", "Ajanta & Ellora Cave Monasteries", "Monolithic rock-cut Buddhist and Kailash temple architecture.", listOf("Kailasa Temple Carving", "Ajanta Frescoes"), listOf("Rashtrakuta Kings", "Satavahanas")),
                    TimelineEntry(2, "Medieval Era", "1674 CE", "Maratha Swarajya Establishment", "Coronation of Chhatrapati Shivaji Maharaj at Raigad Fort.", listOf("Raigad Coronation 1674", "Guerrilla Warfare"), listOf("Chhatrapati Shivaji Maharaj", "Sambhaji Maharaj", "Peshwa Baji Rao I")),
                    TimelineEntry(3, "Modern Era", "1942 – Present", "Quit India Movement & Economic Engine", "Gowalia Tank Quit India Call and transformation of Mumbai into India's commercial hub.", listOf("Gowalia Tank 1942", "Gateway of India"), listOf("Bal Gangadhar Tilak", "Dr. B.R. Ambedkar"))
                )
            ),
            State(
                slug = "delhi",
                name = "Delhi",
                capital = "New Delhi",
                region = "North",
                shortDescription = "Historic seat of seven empires, Qutub Minar, Red Fort, Humayun's Tomb, Indraprastha heritage, and Capital of the Republic.",
                imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "c. 1450 BCE", "Indraprastha Epic Kingdom", "Capital of the Pandavas in Mahabharata epic history.", listOf("Iron Pillar of Delhi", "Qila Rai Pithora"), listOf("Anangpal Tomar", "Prithviraj Chauhan")),
                    TimelineEntry(2, "Medieval Era", "1192 – 1857 CE", "Sultanate & Mughal Imperial Seat", "Construction of Qutub Minar, Lal Qila, and Jama Masjid.", listOf("Qutub Minar 1199 CE", "Red Fort 1648 CE"), listOf("Prithviraj Chauhan", "Qutb-ud-din Aibak", "Shah Jahan")),
                    TimelineEntry(3, "Modern Era", "1911 – Present", "Capital of Modern Republic of India", "Shifting of capital from Calcutta to Delhi (1911) and framing of Constitution at Parliament Hall.", listOf("India Gate 1931", "Constituent Assembly 1946"), listOf("Dr. B.R. Ambedkar", "Jawaharlal Nehru"))
                )
            ),
            State(
                slug = "uttar-pradesh",
                name = "Uttar Pradesh",
                capital = "Lucknow",
                region = "North",
                shortDescription = "Cradle of Vedic civilization, Kashi (Varanasi), Ayodhya, Mathura, Agra Fort, Taj Mahal, and Sarnath Deer Park.",
                imageUrl = "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "528 BCE", "First Sermon at Sarnath & Sacred Rivers", "Gautama Buddha delivered his first teaching on Dhamma at Sarnath.", listOf("Dhammak Stupa", "Mauryan Pillars"), listOf("Gautama Buddha", "Emperor Ashoka")),
                    TimelineEntry(2, "Medieval Era", "1556 – 1658 CE", "Mughal Architecture Peak & Avadh Culture", "Construction of Taj Mahal, Fatehpur Sikri, and Agra Fort.", listOf("Taj Mahal 1632 CE", "Buland Darwaza"), listOf("Akbar the Great", "Shah Jahan")),
                    TimelineEntry(3, "Modern Era", "1857 – Present", "First War of Independence 1857", "Mangal Pandey mutiny at Meerut and Rani Lakshmibai's siege of Jhansi.", listOf("Siege of Jhansi 1857", "Kakori Train Action"), listOf("Rani Lakshmibai", "Mangal Pandey", "Tatya Tope"))
                )
            ),
            State(
                slug = "tamil-nadu",
                name = "Tamil Nadu",
                capital = "Chennai",
                region = "South",
                shortDescription = "Land of Dravidian Chola dynasty, Tanjore Big Temple, Shore Temple Mahabalipuram, Sangam literature, and Bharatanatyam.",
                imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "300 BCE – 300 CE", "Sangam Era Literature & Pallava Carvings", "Golden era of Tamil poetry and rock-cut Shore Temple Mahabalipuram.", listOf("Mahabalipuram Monoliths", "Thirukkural Composition"), listOf("Narasimhavarman I", "Thiruvalluvar")),
                    TimelineEntry(2, "Medieval Era", "850 – 1279 CE", "Imperial Chola Dynasty Golden Era", "Conquest of Southeast Asia and construction of Brihadeeswarar Temple.", listOf("Tanjore Big Temple 1010 CE", "Chola Bronze Sculptures"), listOf("Raja Raja Chola I", "Rajendra Chola I")),
                    TimelineEntry(3, "Modern Era", "19th C – Present", "Dravidian Cultural Revival & Industrial Hub", "Pioneered classical arts revival, Carnatic music, and automobile manufacturing.", listOf("Chennai Port 1881", "Meenakshi Temple Revival"), listOf("Subramania Bharati", "VO Chidambaram Pillai"))
                )
            ),
            State(
                slug = "kerala",
                name = "Kerala",
                capital = "Thiruvananthapuram",
                region = "South",
                shortDescription = "God's Own Country, Chera kingdom, Muziris spice port, Padmanabhaswamy Temple treasure, and Kalaripayattu martial arts.",
                imageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "300 BCE – 1100 CE", "Muziris International Spice Port", "Global spice trading hub with Romans, Greeks, Chinese, and Arabs.", listOf("Muziris Spice Route", "Chera Dynasty Capital"), listOf("Cheraman Perumal")),
                    TimelineEntry(2, "Medieval Era", "1741 CE", "Battle of Colachel & Travancore Sovereignty", "Anizham Thirunal Marthanda Varma defeated the Dutch East India Company fleet.", listOf("Colachel Victory 1741", "Padmanabhaswamy Temple Expansion"), listOf("Marthanda Varma", "Zamorin of Calicut")),
                    TimelineEntry(3, "Modern Era", "20th C – Present", "Social Reform & High Human Development", "Sree Narayana Guru's social equality movement and highest literacy model.", listOf("Vaikom Satyagraha 1924", "Kerala Model Development"), listOf("Sree Narayana Guru", "Chattampi Swamikal"))
                )
            ),
            State(
                slug = "punjab",
                name = "Punjab",
                capital = "Chandigarh",
                region = "North",
                shortDescription = "Land of Five Rivers, Golden Temple Amritsar, Sikh Empire of Maharaja Ranjit Singh, Indus Ropar site, and Granary of India.",
                imageUrl = "https://images.unsplash.com/photo-1588096344356-9a2f64e24296?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1588096344356-9a2f64e24296?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "2500 BCE", "Indus Ropar Site & Vedic Rivers", "Harappan settlements along the Sutlej and Saraswati rivers.", listOf("Ropar Excavations", "Vedic Hymn Compositions"), listOf("Harappan Settlers")),
                    TimelineEntry(2, "Medieval Era", "1799 – 1849 CE", "Sikh Empire Sovereignty", "Unification of Punjab under Maharaja Ranjit Singh with gold leaf gilding of Harmandir Sahib.", listOf("Gold Gilding of Harmandir Sahib", "Sikh Khalsa Raj"), listOf("Maharaja Ranjit Singh", "Hari Singh Nalwa", "Guru Gobind Singh Ji")),
                    TimelineEntry(3, "Modern Era", "1919 – Present", "Jallianwala Bagh, Freedom Struggle & Green Revolution", "Martyrdom of Bhagat Singh and Green Revolution leading Indian agriculture.", listOf("Jallianwala Bagh 1919", "Green Revolution 1960s"), listOf("Bhagat Singh", "Lala Lajpat Rai", "Udham Singh"))
                )
            ),
            State(
                slug = "west-bengal",
                name = "West Bengal",
                capital = "Kolkata",
                region = "East",
                shortDescription = "Cultural heart of Pala Empire, Bishnupur terracotta temples, Victoria Memorial, Bengal Renaissance, and Tagore Gitanjali.",
                imageUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "750 – 1161 CE", "Pala Dynasty & Mahayana Buddhism", "Patronage of Vikramashila and Nalanda universities and Buddhist palm-leaf art.", listOf("Bishnupur Terracotta Temples", "Somapura Mahavihara"), listOf("Gopala I", "Dharmapala", "Devapala")),
                    TimelineEntry(2, "Medieval Era", "1757 CE", "Battle of Plassey & Nawabs of Bengal", "Nawab Siraj-ud-Daulah fought East India Company at Plassey.", listOf("Battle of Plassey 1757", "Bishnupur Terracotta Architecture"), listOf("Siraj-ud-Daulah", "Rani Bhabani")),
                    TimelineEntry(3, "Modern Era", "1905 – Present", "Bengal Renaissance & Swadeshi Movement", "Swami Vivekananda, Rabindranath Tagore, and Subhash Chandra Bose's Azad Hind Fauj.", listOf("Partition Boycott 1905", "Tagore Nobel Prize 1913"), listOf("Swami Vivekananda", "Rabindranath Tagore", "Netaji Subhash Chandra Bose"))
                )
            ),
            State(
                slug = "madhya-pradesh",
                name = "Madhya Pradesh",
                capital = "Bhopal",
                region = "Central",
                shortDescription = "Heart of Bharat, Sanchi Stupa, Khajuraho temple carvings, Gwalior Fort, Bhimbetka rock shelters, and Ujjain Mahakal.",
                imageUrl = "https://images.unsplash.com/photo-1627894098902-86161860822e?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1627894098902-86161860822e?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "10,000 BCE – 3rd C BCE", "Bhimbetka Cave Art & Sanchi Stupa", "Oldest prehistoric rock paintings and Emperor Ashoka's Great Stupa at Sanchi.", listOf("Torana Gateway Carvings", "Bhimbetka Caves UNESCO"), listOf("Emperor Ashoka", "Prehistoric Cave Painters")),
                    TimelineEntry(2, "Medieval Era", "950 – 1050 CE", "Khajuraho Temples & Gwalior Citadel", "Chandela Rajput kings built monolithic Khajuraho sandstone temples.", listOf("Kandariya Mahadeva Temple", "Gwalior Fort Gujari Mahal"), listOf("King Dhanga", "Raja Man Singh Tomar")),
                    TimelineEntry(3, "Modern Era", "18th C – Present", "Ahilyabai Holkar Governance & Ujjain Revival", "Queen Ahilyabai Holkar rebuilt desecrated shrines across India from Indore.", listOf("Maheshwar Handloom", "Mahakaleshwar Corridor"), listOf("Rani Ahilyabai Holkar", "Rani Durgavati"))
                )
            ),
            State(
                slug = "goa",
                name = "Goa",
                capital = "Panaji",
                region = "West",
                shortDescription = "Kadamba Dynasty heritage, Portuguese colonial forts, Aguada Fort, UNESCO Basilica of Bom Jesus, and Dudhsagar Falls.",
                imageUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "10th – 14th Century CE", "Kadamba Dynasty Golden Age", "Maritime trade port at Chandor and Gopakapattana.", listOf("Tambdi Surla Temple 12th C", "Chandor Excavations"), listOf("King Jayakeshi I")),
                    TimelineEntry(2, "Medieval Era", "1510 – 1961 CE", "Portuguese Colonial Era & Fort Aguada", "Afonso de Albuquerque captured Goa, constructing coastal forts and churches.", listOf("Fort Aguada 1612 CE", "Basilica of Bom Jesus"), listOf("Afonso de Albuquerque")),
                    TimelineEntry(3, "Modern Era", "1961 – Present", "Operation Vijay & Statehood 1987", "Indian Armed Forces liberated Goa from Portuguese rule in December 1961.", listOf("Operation Vijay 1961", "Statehood Declaration 1987"), listOf("Dr. Ram Manohar Lohia", "TB Cunha"))
                )
            ),
            State(
                slug = "bihar",
                name = "Bihar",
                capital = "Patna",
                region = "East",
                shortDescription = "Ancient Magadha Empire, Nalanda University, Mahabodhi Temple Bodh Gaya, Emperor Ashoka Pataliputra, and Chhath Parv.",
                imageUrl = "https://images.unsplash.com/photo-1610484826917-0f101a7bf7f4?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1610484826917-0f101a7bf7f4?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "5th C BCE – 5th C CE", "Magadha, Enlightenment at Bodh Gaya & Nalanda", "Gautama Buddha attained Enlightenment; Nalanda hosted 10,000 scholars.", listOf("Nalanda Mahavihara", "Ashoka Pillar at Vaishali"), listOf("Chandragupta Maurya", "Emperor Ashoka", "Aryabhata")),
                    TimelineEntry(2, "Medieval Era", "1540 – 1545 CE", "Sher Shah Suri Empire & Grand Trunk Road", "Sher Shah Suri built the Grand Trunk Road and Rohtas Fort.", listOf("Grand Trunk Road", "Rohtasgarh Fort"), listOf("Sher Shah Suri")),
                    TimelineEntry(3, "Modern Era", "1917 – Present", "Champaran Satyagraha & JP Movement", "Mahatma Gandhi's first Satyagraha at Champaran and JP's Total Revolution 1974.", listOf("Champaran Satyagraha 1917", "JP Total Revolution 1974"), listOf("Mahatma Gandhi", "Jayaprakash Narayan", "Dr. Rajendra Prasad"))
                )
            ),
            State(
                slug = "odisha",
                name = "Odisha",
                capital = "Bhubaneswar",
                region = "East",
                shortDescription = "Land of Kalinga architecture, Konark Sun Temple chariot, Jagannath Temple Puri, Udayagiri caves, and Odissi classical dance.",
                imageUrl = "https://images.unsplash.com/photo-1620891549027-942fdc95d3f5?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1620891549027-942fdc95d3f5?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "261 BCE", "Kalinga War & Ashoka Transformation", "Battle of Kalinga along Daya river that transformed Ashoka to Buddhism.", listOf("Dhauli Peace Stupa", "Udayagiri Rock Inscriptions"), listOf("Emperor Ashoka", "King Kharavela")),
                    TimelineEntry(2, "Medieval Era", "1250 CE", "Konark Sun Temple & Jagannath Puri", "Eastern Ganga Dynasty constructed the massive stone chariot Sun Temple.", listOf("24 Stone Wheels of Konark", "Jagannath Temple Puri"), listOf("King Narasimhadeva I", "Anantavarman Chodaganga")),
                    TimelineEntry(3, "Modern Era", "1817 – Present", "Paika Rebellion & Modern Temple City", "Paika armed rebellion against British rule under Bakshi Jagabandhu.", listOf("Paika Rebellion 1817", "Bhubaneswar Temple City Plan"), listOf("Bakshi Jagabandhu", "Utkalmani Gopabandhu Das"))
                )
            ),
            State(
                slug = "assam",
                name = "Assam",
                capital = "Dispur",
                region = "North-East",
                shortDescription = "Ahom Kingdom 600-year reign, Kamakhya Shaktipeeth, Rang Ghar amphitheatre, Lachit Borphukan Saraighat victory, and Kaziranga.",
                imageUrl = "https://images.unsplash.com/photo-1616489932200-a5417937d58a?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1616489932200-a5417937d58a?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "4th – 12th Century CE", "Kamarupa Kingdom & Kamakhya Temple", "Pragjyotishpura capital referenced in Mahabharata and Xuanzang's travelogues.", listOf("Kamakhya Shrine", "Deopahar Sculpture Site"), listOf("King Bhaskaravarman")),
                    TimelineEntry(2, "Medieval Era", "1228 – 1826 CE", "Ahom Dynasty & Battle of Saraighat", "Defeated Mughal forces 17 times including naval Battle of Saraighat 1671.", listOf("Rang Ghar Amphitheatre", "Battle of Saraighat 1671"), listOf("Chaolung Sukaphaa", "Lachit Borphukan")),
                    TimelineEntry(3, "Modern Era", "1889 – Present", "Digboi Oil Fields & Assam Tea Industry", "Discovery of Asia's first oil well at Digboi and world-renowned Assam tea gardens.", listOf("Digboi Refinery 1901", "Kaziranga UNESCO Site"), listOf("Gopinath Bordoloi", "Kushal Konwar"))
                )
            ),
            State(
                slug = "telangana",
                name = "Telangana",
                capital = "Hyderabad",
                region = "South",
                shortDescription = "Kakatiya Dynasty stone gateways, Thousand Pillar Temple, Charminar, Golconda Fort, and Ramappa UNESCO floating-brick site.",
                imageUrl = "https://images.unsplash.com/photo-1605367031782-93822a1b1b4b?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1605367031782-93822a1b1b4b?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Medieval Era", "1163 – 1323 CE", "Kakatiya Kingdom Architecture", "Construction of Ramappa floating-brick temple and Warangal fort arches.", listOf("Ramappa Temple UNESCO", "Warangal Fort Toranas"), listOf("Ganapatideva", "Rani Rudrama Devi", "Prataparudra")),
                    TimelineEntry(2, "Medieval Era", "1591 CE", "Qutb Shahi Dynasty & Charminar", "Muhammad Quli Qutb Shah founded Hyderabad and built Charminar and Golconda Fort.", listOf("Charminar 1591", "Golconda Acoustic Fort"), listOf("Muhammad Quli Qutb Shah")),
                    TimelineEntry(3, "Modern Era", "1948 – Present", "Operation Polo Integration & IT Boom", "Sardar Patel's Operation Polo integrated Hyderabad state into India in Sept 1948.", listOf("Operation Polo 1948", "HITEC City Hyderabad"), listOf("Sardar Vallabhbhai Patel", "Komaram Bheem"))
                )
            ),
            State(
                slug = "uttarakhand",
                name = "Uttarakhand",
                capital = "Dehradun",
                region = "North",
                shortDescription = "Devbhumi (Land of Gods), sacred Chota Char Dham, Kedarnath temple, Badrinath, Rishikesh Ganga heritage, and Valley of Flowers.",
                imageUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "8th Century CE", "Adi Shankaracharya Revival", "Establishment of Char Dham pilgrimage shrines and Jyotirmath.", listOf("Kedarnath Shrine", "Badrinath Temple"), listOf("Adi Shankaracharya")),
                    TimelineEntry(2, "Medieval Era", "14th – 18th Century CE", "Garhwal & Kumaon Kingdoms", "Construction of Katyuri stone temples at Jageshwar and Chand dynasty hill forts.", listOf("Jageshwar Dham Monoliths", "Almora Citadel"), listOf("King Kalyan Chand", "Katyuri Kings")),
                    TimelineEntry(3, "Modern Era", "1973 – Present", "Chipko Environmental Movement & Statehood", "Gaura Devi and Sunderlal Bahuguna led the peaceful forest protection Chipko Movement.", listOf("Chipko Movement 1973", "State Formation 2000"), listOf("Gaura Devi", "Sunderlal Bahuguna", "Govind Ballabh Pant"))
                )
            ),
            State(
                slug = "himachal-pradesh",
                name = "Himachal Pradesh",
                capital = "Shimla",
                region = "North",
                shortDescription = "Kangra Fort resistance, Tabo Monastery 996 AD, Naggar Castle, Himalayan wooden pagoda temples, and Dharamshala.",
                imageUrl = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "4th Century BCE", "Kangra Royal Fort Citadel", "Oldest fort in India resisting Alexander, Mahmud of Ghazni and Jahangir.", listOf("Kangra Fort Carvings", "Masroor Rock Cut Temple"), listOf("Katoch Dynasty Kings")),
                    TimelineEntry(2, "Medieval Era", "996 CE", "Tabo Monastery & Wooden Pagodas", "Foundation of Tabo Monastery in Spiti Valley, known as the Ajanta of the Himalayas.", listOf("Tabo Monastery 996 AD", "Hadimba Temple Manali 1553"), listOf("Rinchen Zangpo")),
                    TimelineEntry(3, "Modern Era", "1864 – Present", "Shimla Summer Capital & Hydroelectric Power", "British designated Shimla as Summer Capital; evolved into a clean green energy state.", listOf("Kalka-Shimla Toy Train 1903", "Bhakra Nangal Dam"), listOf("Yashwant Singh Parmar"))
                )
            ),
            State(
                slug = "jammu-and-kashmir",
                name = "Jammu & Kashmir",
                capital = "Srinagar",
                region = "North",
                shortDescription = "Martand Sun Temple stone monoliths, Pari Mahal, Hari Parbat Fort, Kalhana Rajatarangini, and Karkota Dynasty Empire.",
                imageUrl = "https://images.unsplash.com/photo-1595815771614-ade9d652a65d?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1595815771614-ade9d652a65d?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "c. 1148 CE", "Kalhana's Rajatarangini Chronicles", "Composed Rajatarangini ('River of Kings'), India's earliest recorded chronicle.", listOf("Sharada Peeth Learning", "Rajatarangini Verses"), listOf("Kalhana", "Queen Didda")),
                    TimelineEntry(2, "Medieval Era", "724 – 760 CE", "Karkota Dynasty Imperial Reign", "Emperor Lalitaditya Muktapida constructed Martand Sun Temple and expanded empire across Asia.", listOf("Martand Sun Temple", "Pari Mahal"), listOf("Emperor Lalitaditya Muktapida")),
                    TimelineEntry(3, "Modern Era", "1947 – Present", "Accession to India & Crown of Bharat", "Maharaja Hari Singh signed the Instrument of Accession in October 1947.", listOf("Instrument of Accession 1947", "Shikara & Carpet Handicrafts"), listOf("Maharaja Hari Singh", "Brigadier Rajinder Singh"))
                )
            ),
            State(
                slug = "andhra-pradesh",
                name = "Andhra Pradesh",
                capital = "Amaravati",
                region = "South",
                shortDescription = "Satavahana Dynasty cradle, Tirumala Tirupati, Lepakshi hanging pillar, Amaravati Mahachaitya Stupa, and Kuchipudi dance.",
                imageUrl = "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80",
                bannerUrl = "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80",
                timeline = listOf(
                    TimelineEntry(1, "Ancient Era", "230 BCE – 220 CE", "Satavahana Empire & Amaravati Stupa", "Pioneered maritime trade and constructed Amaravati Mahachaitya Stupa.", listOf("Amaravati Stupa Carvings", "Nagarjunakonda Buddhist Site"), listOf("Gautamiputra Satakarni", "Acharya Nagarjuna")),
                    TimelineEntry(2, "Medieval Era", "1530 CE", "Lepakshi Hanging Pillar & Eastern Chalukya", "Virupanna built Lepakshi Temple famous for its miraculous hanging stone pillar.", listOf("Lepakshi Hanging Pillar", "Tirumala Temple Patronage"), listOf("Virupanna", "Vijayanagara Rulers")),
                    TimelineEntry(3, "Modern Era", "1922 – Present", "Rampa Rebellion & Coastal Ports", "Alluri Sitarama Raju led Rampa tribal rebellion against British forest oppression.", listOf("Rampa Rebellion 1922", "Visakhapatnam Port 1933"), listOf("Alluri Sitarama Raju", "Potti Sreeramulu"))
                )
            )
        )

        for (state in defaultStates) {
            val existing = stateDao.getStateBySlugSync(state.slug)
            if (existing != null) {
                state.isFavorite = existing.isFavorite
            }
            // Always resolve local asset image in assets/ first
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
                            // Always check and apply local asset image if present
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
                // Silently fallback to Room DB without error toast
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
                // Silently fallback to local Room data
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}

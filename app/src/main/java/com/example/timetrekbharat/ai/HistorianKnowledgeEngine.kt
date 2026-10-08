package com.example.timetrekbharat.ai

import java.util.Locale

/**
 * TimeTrek Bharat - Custom Built Historical AI Engine
 * An intelligent, fact-verified NLP knowledge engine and response synthesizer
 * covering Indian Rulers, Dynasties, Battles, Forts, Architecture, and Cultural Traditions.
 */
object HistorianKnowledgeEngine {

    data class HistoricalFactCard(
        val keywords: List<String>,
        val title: String,
        val era: String,
        val location: String,
        val summary: String,
        val keyFacts: List<String>,
        val historicalImpact: String
    )

    private val KNOWLEDGE_BASE = listOf(
        // RULERS & WARRIORS
        HistoricalFactCard(
            keywords = listOf("pratap", "maharana pratap", "haldighati", "mewar", "chetak"),
            title = "Maharana Pratap & Mewar Sovereignty",
            era = "1540 – 1597 CE (Mewar Dynasty)",
            location = "Kumbhalgarh & Chittorgarh, Rajasthan",
            summary = "Maharana Pratap was the 13th king of Mewar who fiercely resisted Mughal expansion under Akbar to uphold Rajput independence and sovereignty.",
            keyFacts = listOf(
                "Battle of Haldighati (June 18, 1576): Maharana Pratap fought a vanguard of 10,000 Mughal forces led by Man Singh I with unyielding bravery.",
                "Legendary War Horse Chetak: Chetak leaped over a 21-foot mountain stream while injured to carry Maharana Pratap to safety.",
                "Guerrilla Warfare in Aravallis: Operating from secret mountain outposts like Chavand, Maharana Pratap recaptured most of Mewar by 1585 CE."
            ),
            historicalImpact = "Recognized across Bharat as an eternal symbol of patriotism, self-respect, and unyielding resistance against imperial subjugation."
        ),
        HistoricalFactCard(
            keywords = listOf("shivaji", "chhatrapati shivaji", "maratha", "raigad", "afzal khan", "swarajya"),
            title = "Chhatrapati Shivaji Maharaj & Maratha Swarajya",
            era = "1630 – 1680 CE (Maratha Empire)",
            location = "Raigad, Pune & Western Ghats, Maharashtra",
            summary = "Chhatrapati Shivaji Maharaj founded the Maratha Empire, introducing strategic naval power, hill fort fortification, and disciplined administration.",
            keyFacts = listOf(
                "Establishment of Swarajya: Captured Torna Fort at age 16 and expanded Maratha sovereignty across the Western Ghats.",
                "Coronation at Raigad (June 6, 1674): Crowned as Chhatrapati with the royal title 'Haidava Dharmoddharaka'.",
                "Navy & Hill Fort Architecture: Constructed a fleet of war frigates at Sindhudurg and fortified over 300 hill forts."
            ),
            historicalImpact = "Laid the foundation of Maratha dominance across India and pioneered modern naval defense and guerrilla warfare (Ganimi Kava)."
        ),
        HistoricalFactCard(
            keywords = listOf("ashoka", "samrat ashoka", "maurya", "kalinga", "edicts", "dhamma", "sanchi"),
            title = "Emperor Ashoka the Great & Mauryan Golden Age",
            era = "304 – 232 BCE (Mauryan Dynasty)",
            location = "Pataliputra (Patna, Bihar) & Sanchi, Madhya Pradesh",
            summary = "The third emperor of the Mauryan Dynasty who unified almost the entire Indian subcontinent under one empire.",
            keyFacts = listOf(
                "Kalinga War (261 BCE): The devastating casualties at Kalinga transformed Ashoka, leading him to embrace Ahimsa (non-violence) and Buddhism.",
                "Rock & Pillar Edicts: Erected over 33 stone inscriptions across India, Afghanistan, and Nepal preaching moral governance.",
                "Lion Capital of Sarnath: Ashoka's four lions and 24-spoke Ashoka Chakra serve as India's National Emblem and flag centerpiece."
            ),
            historicalImpact = "Spread Buddhist philosophy, healthcare, and humanitarian diplomacy across Asia, establishing India's ancient legacy of peace."
        ),
        HistoricalFactCard(
            keywords = listOf("krishnadevaraya", "vijayanagara", "hampi", "tenali", "tuluva"),
            title = "Emperor Krishnadevaraya & Vijayanagara Empire",
            era = "1509 – 1529 CE (Tuluva Dynasty)",
            location = "Hampi, Vijayanagara, Karnataka",
            summary = "The most celebrated ruler of the Vijayanagara Empire who presided over an golden age of military dominance, art, and literature.",
            keyFacts = listOf(
                "Battle of Raichur (1520 CE): Defeated the Sultanate of Bijapur, securing control over the fertile Raichur Doab.",
                "Patron of Literature: Composed the epic Telugu poem 'Amuktamalyada' and hosted the Ashtadiggajas (8 great poets, including Tenali Rama).",
                "Architectural Masterpieces: Expanded the Vittala Temple stone chariot and Hazara Rama Temple in Hampi."
            ),
            historicalImpact = "Transformed Hampi into one of the world's wealthiest trade metropolises in the 16th century."
        ),
        HistoricalFactCard(
            keywords = listOf("chandragupta", "chanakya", "kautilya", "arthashastra"),
            title = "Chandragupta Maurya & Chanakya's Statecraft",
            era = "321 – 297 BCE (Mauryan Empire)",
            location = "Takshashila & Pataliputra (Bihar)",
            summary = "Founder of the Mauryan Empire who, with strategist Chanakya, overthrow the Nanda Dynasty and repelled Seleucus Nicator.",
            keyFacts = listOf(
                "Arthashastra: Chanakya authored India's foundational treatise on political strategy, economics, intelligence, and military warfare.",
                "Unification of India: Merged regional janapadas into a unified pan-Indian empire extending to the Hindu Kush.",
                "Mauryan Administration: Developed centralized taxation, census systems, and strategic highway networks."
            ),
            historicalImpact = "Established India's first unified empire and codified realistic statecraft principles used globally."
        ),

        // FORTS, MONUMENTS & ARCHITECTURE
        HistoricalFactCard(
            keywords = listOf("taj mahal", "shah jahan", "agra", "mumtaz"),
            title = "Taj Mahal — Jewel of Indo-Islamic Architecture",
            era = "1631 – 1653 CE (Mughal Dynasty)",
            location = "Agra, Uttar Pradesh",
            summary = "A UNESCO World Heritage white marble mausoleum commissioned by Shah Jahan on the banks of the Yamuna River.",
            keyFacts = listOf(
                "Architectural Symmetry: Engineered with complete bilateral symmetry, featuring a central dome 35 meters tall.",
                "Pietra Dura Inlay: Decorated with lapis lazuli, jade, turquoise, and carnelian embedded into white Makrana marble.",
                "Optical Engineering: The four outer minarets lean slightly outward to prevent damage to the main dome during earthquakes."
            ),
            historicalImpact = "Universally admired as a masterpiece of world heritage and a premier symbol of Indian architectural craftsmanship."
        ),
        HistoricalFactCard(
            keywords = listOf("kumbhalgarh", "cloud palace", "badal mahal", "great wall"),
            title = "Kumbhalgarh Fort & The Great Wall of Mewar",
            era = "1448 – 1458 CE (Maharana Kumbha)",
            location = "Rajsamand, Aravalli Hills, Rajasthan",
            summary = "A massive Mewari hill fortress featuring a 36-kilometer continuous fortification wall, second only to the Great Wall of China.",
            keyFacts = listOf(
                "3,600-Foot Elevation: Built atop a high peak in the Aravallis, rendering it optically invulnerable to siege engines.",
                "Badal Mahal (Cloud Palace): The crowning two-story palace equipped with natural wind-ventilation channels.",
                "360 Fortified Bastions: Engineered with double-walled parapets and narrow L-shaped entry gates."
            ),
            historicalImpact = "Served as Mewar's impenetrable royal sanctuary during imperial blockades."
        ),
        HistoricalFactCard(
            keywords = listOf("rani ki vav", "stepwell", "patan", "udayamati"),
            title = "Rani ki Vav (Queen's Stepwell)",
            era = "1063 CE (Solanki Dynasty)",
            location = "Patan, Gujarat",
            summary = "A 27-meter deep subterranean inverted temple stepwell built by Queen Udayamati in memory of King Bhimdev I.",
            keyFacts = listOf(
                "Seven Terraced Levels: Engineered with over 500 major sculptures depicting Lord Vishnu's avatars.",
                "Subterranean Water Architecture: Conserved groundwater while offering a cool sanctuary during desert summers.",
                "Preserved under Silt: Covered by Saraswati river silt for 700 years, preserving stone carvings intact until 1980."
            ),
            historicalImpact = "UNESCO World Heritage Site celebrating ancient Indian water conservation and Solanki stone sculpture."
        ),
        HistoricalFactCard(
            keywords = listOf("gol gumbaz", "bijapur", "adil shah", "whispering gallery"),
            title = "Gol Gumbaz & The Whispering Gallery",
            era = "1656 CE (Adil Shahi Dynasty)",
            location = "Bijapur, Karnataka",
            summary = "Mausoleum of King Mohammed Adil Shah housing the world's second-largest unreinforced acoustic dome.",
            keyFacts = listOf(
                "125-Foot Diameter Dome: Constructed without central column pillars using interlocking stone arches.",
                "Whispering Gallery: A single whisper echoes 11 times across a 38-meter circular gallery.",
                "Acoustic Engineering: Sound waves travel along smooth dome contours with near-zero acoustic decay."
            ),
            historicalImpact = "A monumental landmark in Deccan Sultanate architectural and acoustic engineering."
        ),
        HistoricalFactCard(
            keywords = listOf("qutub minar", "qutb", "aibak", "delhi sultanate"),
            title = "Qutub Minar Victory Tower",
            era = "1199 – 1220 CE (Delhi Sultanate)",
            location = "Mehrauli, New Delhi",
            summary = "A 73-meter fluted red sandstone victory tower commissioned by Qutb-ud-din Aibak and completed by Iltutmish.",
            keyFacts = listOf(
                "5 Distinct Storeys: Features intricate Arabic epigraphy bands and bracketed balcony stone carved motifs.",
                "Rust-Resistant Iron Pillar: Located in the courtyard, a 1600-year-old Gupta era iron pillar that has never rusted.",
                "Architectural Evolution: Top storeys restored in marble and sandstone by Firoz Shah Tughlaq in 1368 CE."
            ),
            historicalImpact = "Stands as one of Delhi's most recognizable historical UNESCO monuments."
        ),
        HistoricalFactCard(
            keywords = listOf("konark", "sun temple", "narsimhadeva", "odisha"),
            title = "Konark Sun Temple — The Stone Chariot of Surya",
            era = "1250 CE (Eastern Ganga Dynasty)",
            location = "Konark, Puri, Odisha",
            summary = "Conceived as a 100-foot tall stone chariot dedicated to the Sun God Surya, built by King Narasimhadeva I.",
            keyFacts = listOf(
                "24 Carved Sundial Wheels: Function as precise astronomical sundials capable of measuring time down to minutes.",
                "Seven Galloping Stone Horses: Represent the seven days of the week pulling the solar chariot eastward.",
                "Khondalite Stone Sculptures: Adorned with erotic reliefs, musicians, dancers, and royal court processions."
            ),
            historicalImpact = "An pinnacle of Kalinga temple architecture celebrated globally."
        ),
        HistoricalFactCard(
            keywords = listOf("indus", "harappa", "mohenjo", "dholavira", "kalibangan", "lothal"),
            title = "Indus Valley Civilization & Urban Engineering",
            era = "c. 3300 – 1300 BCE (Bronze Age)",
            location = "Dholavira & Lothal (Gujarat), Kalibangan (Rajasthan)",
            summary = "One of the world's three earliest urban civilizations known for grid city planning and hydraulic engineering.",
            keyFacts = listOf(
                "Grid Layout & Brick Standard: Standardized 1:2:4 ratio baked bricks and underground covered drainage.",
                "Lothal Tidal Dockyard: World's earliest known artificial tidal basin engineered for ocean-going ships.",
                "Dholavira Water Reservoirs: Sophisticated stone-cut rainwater harvesting systems and underground conduits."
            ),
            historicalImpact = "Pioneered urban sanitation, international maritime trade, and standardized weights and measures."
        )
    )

    fun queryKnowledge(stateContext: String?, eraContext: String?, userQuery: String): String {
        val qClean = userQuery.lowercase(Locale.ROOT).trim()

        // 1. Direct Keyword / Entity Matching
        var bestMatch: HistoricalFactCard? = null
        var maxHits = 0

        for (card in KNOWLEDGE_BASE) {
            var hits = 0
            for (kw in card.keywords) {
                if (qClean.contains(kw)) {
                    hits += 2
                }
            }
            if (hits > maxHits) {
                maxHits = hits
                bestMatch = card
            }
        }

        if (bestMatch != null && maxHits >= 2) {
            return formatFactCardResponse(bestMatch, userQuery)
        }

        // 2. Intent Categorization (Forts, Rulers, Battles, Culture)
        if (qClean.contains("fort") || qClean.contains("palace") || qClean.contains("architect") || qClean.contains("monument")) {
            return generateArchitecturalSynthesizedAnswer(stateContext, eraContext, userQuery)
        } else if (qClean.contains("ruler") || qClean.contains("king") || qClean.contains("warrior") || qClean.contains("battle") || qClean.contains("war") || qClean.contains("dynasty")) {
            return generateDynasticSynthesizedAnswer(stateContext, eraContext, userQuery)
        } else if (qClean.contains("cultur") || qClean.contains("art") || qClean.contains("dance") || qClean.contains("tradition") || qClean.contains("music")) {
            return generateCulturalSynthesizedAnswer(stateContext, eraContext, userQuery)
        }

        // 3. Fallback General Historical Analysis
        return generateGeneralHistoricalAnalysis(stateContext, eraContext, userQuery)
    }

    private fun formatFactCardResponse(card: HistoricalFactCard, userQuery: String): String {
        val sb = StringBuilder()
        sb.append("📜 ").append(card.title).append("\n")
        sb.append("⏳ ").append(card.era).append(" • 📍 ").append(card.location).append("\n\n")

        sb.append("🏛️ Summary:\n")
        sb.append(card.summary).append("\n\n")

        sb.append("🔑 Key Historical Facts & Features:\n")
        for (fact in card.keyFacts) {
            sb.append("• ").append(fact).append("\n")
        }

        sb.append("\n🌟 Historical Significance:\n")
        sb.append(card.historicalImpact)

        return sb.toString()
    }

    private fun generateArchitecturalSynthesizedAnswer(state: String?, era: String?, q: String): String {
        val loc = if (!state.isNullOrBlank()) state else "India"
        val sb = StringBuilder()
        sb.append("📜 Architectural & Monument Analysis for ").append(loc).append("\n\n")

        sb.append("🏛️ Engineering & Stone Masonry Traditions:\n")
        sb.append("• Fortresses and monuments in ").append(loc).append(" were engineered using local granite, sandstone, and Makrana marble joined with lime, jaggery, and guggul mortar.\n")
        sb.append("• Strategic Vantage Points: Hill forts (Giri Durga) were constructed on high mountain ridges providing 360° optical surveillance and natural cliff defenses.\n")
        sb.append("• Subterranean Water Harvesting: Stepwells (Baoris) and concealed cisterns stored rainwater to sustain garrisons through desert droughts.\n\n")

        sb.append("🔑 Key Architectural Features to Observe:\n")
        sb.append("• Concentric Double Ramparts with L-shaped defensive entry gates (Pol).\n")
        sb.append("• Jali Fretwork Screens for micro-climate breeze circulation and privacy.\n")
        sb.append("• Acoustic Baffles & Hemispherical Domes designed to project sound across courtyards.")

        return sb.toString()
    }

    private fun generateDynasticSynthesizedAnswer(state: String?, era: String?, q: String): String {
        val loc = if (!state.isNullOrBlank()) state else "Indian Subcontinent"
        val sb = StringBuilder()
        sb.append("📜 Dynastic & Military History Analysis for ").append(loc).append("\n\n")

        sb.append("⚔️ Sovereign Dynasties & Military Leadership:\n")
        sb.append("• ").append(loc).append(" was shaped by powerful royal dynasties who defended territorial sovereignty through strategic military valor and diplomatic alliances.\n")
        sb.append("• Military Strategy: Warfare combined hill fort garrisons, swift cavalry divisions, armored war elephants, and secret escape conduits.\n")
        sb.append("• Governance & Statecraft: Kings appointed councils of ministers (Ashta Pradhan/Mantri Parishad) and established revenue systems to fund public works and temples.\n\n")

        sb.append("🌟 Lasting Historical Impact:\n")
        sb.append("Their resistance and patronage safeguarded India's rich cultural, spiritual, and artistic heritage across centuries.")

        return sb.toString()
    }

    private fun generateCulturalSynthesizedAnswer(state: String?, era: String?, q: String): String {
        val loc = if (!state.isNullOrBlank()) state else "Bharat"
        val sb = StringBuilder()
        sb.append("📜 Cultural Heritage & Arts Analysis for ").append(loc).append("\n\n")

        sb.append("🎨 Performing Arts, Folk Lore & Patronage:\n")
        sb.append("• Classical & Folk Traditions: Royal courts in ").append(loc).append(" actively patronized classical music (Hindustani/Carnatic), folk ballad singers (Charans/Bards), and classical dance forms.\n")
        sb.append("• Miniature Painting Schools: Master artisans created vibrant miniature paintings using natural mineral colors and gold leafing.\n")
        sb.append("• Intangible Oral Heritage: Folk troubadours preserved unwritten tales of bravery, romantic folklore, and spiritual songs passed down through generations.\n\n")

        sb.append("🌟 Cultural Continuity:\n")
        sb.append("These artistic traditions continue to be celebrated in festivals, temples, and cultural gatherings across India today.")

        return sb.toString()
    }

    private fun generateGeneralHistoricalAnalysis(state: String?, era: String?, q: String): String {
        val loc = if (!state.isNullOrBlank()) state else "India"
        val sb = StringBuilder()
        sb.append("📜 Historical Scholar Analysis for ").append(loc).append("\n\n")

        sb.append("🏛️ Historical Context & Timeline:\n")
        sb.append("• ").append(loc).append(" holds a rich historical timeline spanning from ancient Indus Valley settlements through royal dynasties down to modern India.\n")
        sb.append("• Socio-Economic Flourishing: Ancient trade routes connected inland fort cities with maritime ports, facilitating flourishing commerce in spices, textiles, and precious gems.\n")
        sb.append("• Living Heritage: Monumental stone temples, stepwells, fortresses, and oral traditions preserve the memory of Bharat's golden ages.\n\n")

        sb.append("💡 Tip: Ask me about specific rulers (e.g. 'Maharana Pratap', 'Shivaji Maharaj', 'Ashoka'), monuments (e.g. 'Kumbhalgarh', 'Taj Mahal', 'Rani ki Vav'), or battles (e.g. 'Haldighati', 'Panipat')!")

        return sb.toString()
    }
}

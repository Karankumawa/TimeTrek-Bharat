package com.example.timetrekbharat.repository

import com.example.timetrekbharat.model.HistoryCategory
import com.example.timetrekbharat.model.HistoryTopicItem

object HistoryContentRepository {

    fun getTopicsByCategory(category: HistoryCategory): List<HistoryTopicItem> {
        return when (category) {
            HistoryCategory.KINGS -> getIndianKingsAndDynasties()
            HistoryCategory.POLITICS -> getIndianPoliticsAndFreedomMovement()
            HistoryCategory.ANCIENT -> getIndianAncientHistory()
            HistoryCategory.MODERN -> getIndianModernHistory()
            HistoryCategory.STATES -> emptyList() // States handled separately via StateRepository
        }
    }

    fun searchTopics(category: HistoryCategory, query: String): List<HistoryTopicItem> {
        val topics = getTopicsByCategory(category)
        if (query.isBlank()) return topics
        val q = query.trim().lowercase()
        return topics.filter { item ->
            item.title.lowercase().contains(q) ||
            item.subtitle.lowercase().contains(q) ||
            item.description.lowercase().contains(q) ||
            item.dynastyOrParty?.lowercase()?.contains(q) == true ||
            item.prominentFigures.any { it.lowercase().contains(q) } ||
            item.keyFacts.any { it.lowercase().contains(q) }
        }
    }

    private fun getIndianKingsAndDynasties(): List<HistoryTopicItem> {
        return listOf(
            HistoryTopicItem(
                id = "king_shivaji",
                category = HistoryCategory.KINGS,
                title = "Chhatrapati Shivaji Maharaj",
                subtitle = "Founder of Maratha Swarajya • Western Ghats & Raigad",
                periodEra = "1630 – 1680 CE",
                dynastyOrParty = "Maratha Empire (Bhonsle Dynasty)",
                location = "Maharashtra (Raigad, Pune, Western Ghats)",
                imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
                description = "Chhatrapati Shivaji Maharaj was the legendary founder of the Maratha Empire who crowned himself at Raigad Fort in 1674 CE. He pioneered naval warfare, fortified over 300 hill forts, and fought unyieldingly for Hindavi Swarajya.",
                keyFacts = listOf(
                    "Pioneered 'Ganimi Kava' (Guerrilla Warfare) tailored to the rugged terrain of the Western Ghats.",
                    "Established a naval fleet with sea forts at Sindhudurg and Vijaydurg, becoming the Father of the Indian Navy.",
                    "Created 'Ashta Pradhan' (Council of 8 Ministers) for progressive, welfare-driven administration."
                ),
                prominentFigures = listOf("Chhatrapati Shivaji Maharaj", "Jijabai", "Tanaji Malusare", "Baji Prabhu Deshpande"),
                stateSlug = "maharashtra"
            ),
            HistoryTopicItem(
                id = "king_maharana_pratap",
                category = HistoryCategory.KINGS,
                title = "Maharana Pratap Singh",
                subtitle = "Mewar Sovereignty & Battle of Haldighati",
                periodEra = "1540 – 1597 CE",
                dynastyOrParty = "Mewar Dynasty (Sisodia Rajput)",
                location = "Rajasthan (Kumbhalgarh, Chittorgarh, Udaipur)",
                imageUrl = "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?auto=format&fit=crop&w=1200&q=80",
                description = "The 13th ruler of Mewar who fiercely resisted Mughal expansion under Akbar. Renowned for his courage at the Battle of Haldighati (1576 CE) and his unyielding pledge to live in the Aravalli hills until Mewar was reclaimed.",
                keyFacts = listOf(
                    "Fought the historic Battle of Haldighati in 1576 with a smaller, fiercely loyal army.",
                    "His war horse Chetak saved Maharana Pratap's life by leaping across an 21-foot mountain stream.",
                    "Operated from secret outposts in Chavand and liberated almost all of Mewar by 1585 CE."
                ),
                prominentFigures = listOf("Maharana Pratap", "Chetak", "Rana Punja Bhil", "Bhamashah"),
                stateSlug = "rajasthan"
            ),
            HistoryTopicItem(
                id = "king_ashoka",
                category = HistoryCategory.KINGS,
                title = "Samrat Ashoka the Great",
                subtitle = "Mauryan Empire & Dhamma Rock Edicts",
                periodEra = "304 – 232 BCE",
                dynastyOrParty = "Mauryan Dynasty",
                location = "Bihar (Pataliputra), Madhya Pradesh (Sanchi)",
                imageUrl = "https://images.unsplash.com/photo-1627894098902-86161860822e?auto=format&fit=crop&w=1200&q=80",
                description = "Third emperor of the Mauryan Dynasty who unified almost the entire Indian subcontinent. After witnessing the casualties of the Kalinga War (261 BCE), he embraced Buddhism, Ahimsa (non-violence), and erected stone edicts across India.",
                keyFacts = listOf(
                    "Erected 33 Major Rock & Pillar Edicts preaching moral governance, tolerance, and medical care for humans and animals.",
                    "The Lion Capital of Ashoka at Sarnath serves as India's official National Emblem.",
                    "Built the Great Stupa at Sanchi and sent peace emissaries across Sri Lanka, Greece, and Central Asia."
                ),
                prominentFigures = listOf("Samrat Ashoka", "Chandragupta Maurya", "Empress Devi", "Mahinda & Sanghamitta"),
                stateSlug = "bihar"
            ),
            HistoryTopicItem(
                id = "king_krishnadevaraya",
                category = HistoryCategory.KINGS,
                title = "Emperor Krishnadevaraya",
                subtitle = "Golden Era of Vijayanagara Empire & Hampi",
                periodEra = "1509 – 1529 CE",
                dynastyOrParty = "Tuluva Dynasty (Vijayanagara Empire)",
                location = "Karnataka (Hampi, Vijayanagara)",
                imageUrl = "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=1200&q=80",
                description = "The most celebrated ruler of the Vijayanagara Empire who presided over a glorious golden age of art, literature, trade, and architectural marvels in Hampi.",
                keyFacts = listOf(
                    "Defeated the Sultanate of Bijapur at the Battle of Raichur (1520 CE), securing the fertile Raichur Doab.",
                    "Authored the epic Telugu poem 'Amuktamalyada' and patronized the Ashtadiggajas (Eight Great Court Poets, including Tenali Rama).",
                    "Constructed the Vittala Temple stone chariot and Hazara Rama Temple in Hampi."
                ),
                prominentFigures = listOf("Emperor Krishnadevaraya", "Tenali Rama", "Allasani Peddana", "Prime Minister Timmarusu"),
                stateSlug = "karnataka"
            ),
            HistoryTopicItem(
                id = "king_chandragupta_maurya",
                category = HistoryCategory.KINGS,
                title = "Chandragupta Maurya & Chanakya",
                subtitle = "Unification of Bharat & Arthashastra",
                periodEra = "321 – 297 BCE",
                dynastyOrParty = "Mauryan Dynasty",
                location = "Bihar (Pataliputra) & Punjab (Takshashila)",
                imageUrl = "https://images.unsplash.com/photo-1610484826917-0f101a7bf7f4?auto=format&fit=crop&w=1200&q=80",
                description = "Founder of the Mauryan Empire who, guided by master strategist Chanakya (Kautilya), overthrew the Nanda Dynasty and repelled Seleucus Nicator, unifying India into a centralized empire.",
                keyFacts = listOf(
                    "Chanakya authored 'Arthashastra', India's foundational treaty on political strategy, espionage, and economics.",
                    "Repelled the Greek forces of Seleucus Nicator, securing borders up to the Hindu Kush mountains.",
                    "Established a pan-Indian administrative network with centralized taxation, census, and royal highways."
                ),
                prominentFigures = listOf("Chandragupta Maurya", "Chanakya (Kautilya)", "Megasthenes", "Dhana Nanda"),
                stateSlug = "bihar"
            ),
            HistoryTopicItem(
                id = "king_rajendra_chola",
                category = HistoryCategory.KINGS,
                title = "Rajendra Chola I",
                subtitle = "Imperial Chola Navy & Gangaikonda Cholapuram",
                periodEra = "1014 – 1044 CE",
                dynastyOrParty = "Imperial Chola Dynasty",
                location = "Tamil Nadu (Thanjavur, Gangaikonda Cholapuram)",
                imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
                description = "One of India's greatest maritime conquerors who extended the Chola Empire across Northern India to the River Ganga and launched a naval expedition across Southeast Asia (Srivijaya, Malaya, Sumatra).",
                keyFacts = listOf(
                    "Built a powerful blue-water navy that controlled the Bay of Bengal, turning it into a 'Chola Lake'.",
                    "Founded the new capital city 'Gangaikonda Cholapuram' ('The City of the Chola who took the Ganges').",
                    "Constructed grand Dravidian temples with intricate bronze sculptures and granite monoliths."
                ),
                prominentFigures = listOf("Rajendra Chola I", "Raja Raja Chola I", "Srivijaya Emperor"),
                stateSlug = "tamil-nadu"
            ),
            HistoryTopicItem(
                id = "king_samudragupta",
                category = HistoryCategory.KINGS,
                title = "Samudragupta — Golden Age of Guptas",
                subtitle = "The Indian Napoleon & Cultural Patron",
                periodEra = "335 – 375 CE",
                dynastyOrParty = "Gupta Empire",
                location = "Uttar Pradesh (Prayagraj) & Bihar (Pataliputra)",
                imageUrl = "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1200&q=80",
                description = "Second ruler of the Gupta Empire, described as undefeated in military campaigns across North and South India. Immortalized on the Prayagraj Pillar Inscription (Eran Prasasti) composed by Harishena.",
                keyFacts = listOf(
                    "Issued gold coins depicting himself playing the Veena (Indian lute), highlighting his musical patronage.",
                    "Conducted the Ashvamedha Yajna to celebrate unyielding sovereignty across the subcontinent.",
                    "Presided over the dawn of classical Sanskrit literature, astronomy, and temple sculpture."
                ),
                prominentFigures = listOf("Samudragupta", "Chandragupta I", "Harishena", "Chandragupta II Vikramaditya"),
                stateSlug = "uttar-pradesh"
            ),
            HistoryTopicItem(
                id = "king_prithviraj_chauhan",
                category = HistoryCategory.KINGS,
                title = "Prithviraj Chauhan III",
                subtitle = "Valor of Ajmer & Delhi • First Battle of Tarain",
                periodEra = "1178 – 1192 CE",
                dynastyOrParty = "Chahamana (Chauhan) Dynasty",
                location = "Delhi & Rajasthan (Ajmer, Qila Rai Pithora)",
                imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                description = "The heroic Rajput king of Ajmer and Delhi legendary for his chivalry, marksmanship, and leadership during the Battles of Tarain against Muhammad Ghori.",
                keyFacts = listOf(
                    "Decisively defeated Muhammad Ghori at the First Battle of Tarain in 1191 CE.",
                    "Built Qila Rai Pithora, the first fortified city of medieval Delhi.",
                    "Celebrated in the epic ballad 'Prithviraj Raso' composed by court poet Chand Bardai."
                ),
                prominentFigures = listOf("Prithviraj Chauhan", "Chand Bardai", "Samyukta"),
                stateSlug = "delhi"
            ),
            HistoryTopicItem(
                id = "king_rani_lakshmibai",
                category = HistoryCategory.KINGS,
                title = "Rani Lakshmibai of Jhansi",
                subtitle = "Heroine of 1857 War of Independence",
                periodEra = "1828 – 1858 CE",
                dynastyOrParty = "Princely State of Jhansi (Maratha)",
                location = "Uttar Pradesh (Jhansi, Gwalior)",
                imageUrl = "https://images.unsplash.com/photo-1627894098902-86161860822e?auto=format&fit=crop&w=1200&q=80",
                description = "The fierce Queen of Jhansi who became the quintessential symbol of Indian resistance against the British East India Company during the 1857 War of Independence.",
                keyFacts = listOf(
                    "Refused to surrender Jhansi under the unjust 'Doctrine of Lapse' declared by Lord Dalhousie.",
                    "Fought on horseback with her infant son Damodar Rao strapped to her back during the Siege of Jhansi.",
                    "British General Hugh Rose described her as 'the bravest and most dangerous of all rebel leaders'."
                ),
                prominentFigures = listOf("Rani Lakshmibai", "Gangadhar Rao", "Tatya Tope", "Jhalkari Bai"),
                stateSlug = "uttar-pradesh"
            ),
            HistoryTopicItem(
                id = "king_ranjit_singh",
                category = HistoryCategory.KINGS,
                title = "Maharaja Ranjit Singh",
                subtitle = "Sher-e-Punjab & Unification of Sikh Empire",
                periodEra = "1780 – 1839 CE",
                dynastyOrParty = "Sikh Empire (Sukerchakia Misl)",
                location = "Punjab (Amritsar, Lahore)",
                imageUrl = "https://images.unsplash.com/photo-1588096344356-9a2f64e24296?auto=format&fit=crop&w=1200&q=80",
                description = "Known as 'Sher-e-Punjab' (Lion of Punjab), he united regional Sikh misls into a sovereign Sikh Empire with secular administration and modernized artillery under general Hari Singh Nalwa.",
                keyFacts = listOf(
                    "Gilded the sanctum of Harmandir Sahib (Golden Temple) in Amritsar with pure gold leaf in 1830 CE.",
                    "Maintained a secular court where ministers were selected from Sikh, Hindu, and Muslim communities.",
                    "Protected the Northwest Frontier against foreign invasions and recovered the Koh-i-Noor diamond."
                ),
                prominentFigures = listOf("Maharaja Ranjit Singh", "Hari Singh Nalwa", "Akali Phula Singh"),
                stateSlug = "punjab"
            )
        )
    }

    private fun getIndianPoliticsAndFreedomMovement(): List<HistoryTopicItem> {
        return listOf(
            HistoryTopicItem(
                id = "pol_1857_revolt",
                category = HistoryCategory.POLITICS,
                title = "First War of Indian Independence (1857)",
                subtitle = "The Great Uprising & Fall of East India Company",
                periodEra = "1857 – 1858 CE",
                dynastyOrParty = "Indian Freedom Fighters & Princely Alliances",
                location = "Meerut, Delhi, Kanpur, Jhansi, Lucknow",
                imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                description = "A massive armed rebellion ignited at Meerut by sepoy Mangal Pandey that spread across Northern and Central India, ending the rule of the British East India Company and transferring control to the British Crown.",
                keyFacts = listOf(
                    "Mangal Pandey's defiance at Barrackpore triggered the nationwide sepoy mutiny in May 1857.",
                    "United rulers like Rani Lakshmibai, Nana Saheb, Tatya Tope, and Begum Hazrat Mahal under Bahadur Shah Zafar.",
                    "Led to the abolition of the British East India Company via the Government of India Act 1858."
                ),
                prominentFigures = listOf("Mangal Pandey", "Rani Lakshmibai", "Tatya Tope", "Nana Saheb", "Bahadur Shah Zafar"),
                stateSlug = "uttar-pradesh"
            ),
            HistoryTopicItem(
                id = "pol_inc_swadeshi",
                category = HistoryCategory.POLITICS,
                title = "Formation of Congress & Swadeshi Movement",
                subtitle = "Early Political Awakening & Vande Mataram",
                periodEra = "1885 – 1911 CE",
                dynastyOrParty = "Indian National Congress & Nationalist Front",
                location = "Mumbai, Kolkata, Bengal",
                imageUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
                description = "The Indian National Congress was founded in 1885 in Bombay. The 1905 Partition of Bengal sparked the fiery Swadeshi Movement, popularizing native industries, boycott of foreign goods, and Bankim Chandra's 'Vande Mataram'.",
                keyFacts = listOf(
                    "First INC session held in Bombay in December 1885 with 72 delegates under WC Bonnerjee.",
                    "Lal-Bal-Pal triumvirate (Lala Lajpat Rai, Bal Gangadhar Tilak, Bipin Chandra Pal) demanded 'Purna Swaraj'.",
                    "Tilak proclaimed: 'Swaraj is my birthright and I shall have it!' during the Swadeshi boycott."
                ),
                prominentFigures = listOf("Bal Gangadhar Tilak", "Lala Lajpat Rai", "Bipin Chandra Pal", "Gopal Krishna Gokhale", "AO Hume"),
                stateSlug = "west-bengal"
            ),
            HistoryTopicItem(
                id = "pol_satyagraha_dandi",
                category = HistoryCategory.POLITICS,
                title = "Non-Cooperation & Dandi Salt March",
                subtitle = "Satyagraha, Ahimsa & Civil Disobedience",
                periodEra = "1920 – 1931 CE",
                dynastyOrParty = "Satyagraha Movement",
                location = "Gujarat (Sabarmati, Dandi) & Pan-India",
                imageUrl = "https://images.unsplash.com/photo-1609949279531-cf48d64bed89?auto=format&fit=crop&w=1200&q=80",
                description = "Mahatma Gandhi launched the Non-Cooperation Movement in 1920 and the historic 240-mile Dandi Salt March in March 1930, breaking the British salt tax and mobilizing millions in peaceful resistance.",
                keyFacts = listOf(
                    "Walked 240 miles from Sabarmati Ashram to Dandi beach over 24 days with 78 marchers.",
                    "Broke the British salt monopoly by making natural salt from sea water on April 6, 1930.",
                    "Inspired non-violent freedom movements and civil rights protests worldwide."
                ),
                prominentFigures = listOf("Mahatma Gandhi", "Sarojini Naidu", "Sardar Vallabhbhai Patel", "Jawaharlal Nehru"),
                stateSlug = "gujarat"
            ),
            HistoryTopicItem(
                id = "pol_azad_hind_fauj",
                category = HistoryCategory.POLITICS,
                title = "Azad Hind Fauj & Revolutionary Front",
                subtitle = "Netaji Subhash Chandra Bose & Freedom Martyrs",
                periodEra = "1928 – 1945 CE",
                dynastyOrParty = "Indian National Army (INA) & HSRA",
                location = "Singapore, Assam (Kohima, Imphal), Punjab",
                imageUrl = "https://images.unsplash.com/photo-1616489932200-a5417937d58a?auto=format&fit=crop&w=1200&q=80",
                description = "Netaji Subhash Chandra Bose formed the Azad Hind Government and INA in 1943 with the rallying cry 'Tum Mujhe Khoon Do, Main Tumhe Azadi Doonga'. Alongside revolutionaries like Bhagat Singh and Azad, they rattled British rule.",
                keyFacts = listOf(
                    "Netaji established the Rani of Jhansi Regiment, the first all-women combat unit in Asia.",
                    "Bhagat Singh, Sukhdev, and Rajguru sacrificed their lives for 'Inquilab Zindabad' in March 1931.",
                    "The INA Trials at Red Fort in 1945 sparked mutinies in the Royal Indian Navy."
                ),
                prominentFigures = listOf("Netaji Subhash Chandra Bose", "Bhagat Singh", "Chandrashekhar Azad", "Captain Lakshmi Sahgal"),
                stateSlug = "assam"
            ),
            HistoryTopicItem(
                id = "pol_integration_states",
                category = HistoryCategory.POLITICS,
                title = "Integration of 565 Princely States",
                subtitle = "Sardar Patel & Architect of Unified Bharat",
                periodEra = "1947 – 1950 CE",
                dynastyOrParty = "Ministry of States & Republic Integration",
                location = "New Delhi, Hyderabad, Junagadh, Kashmir",
                imageUrl = "https://images.unsplash.com/photo-1599661046827-dacff0c0f09a?auto=format&fit=crop&w=1200&q=80",
                description = "Sardar Vallabhbhai Patel, the 'Iron Man of India', dynamically integrated 565 disparate princely states into the Indian Union, preventing Balkanization and shaping modern political India.",
                keyFacts = listOf(
                    "Successfully negotiated Instruments of Accession with royal rulers through diplomatic firmness and patriotism.",
                    "Executed Operation Polo in September 1948 to integrate the princely state of Hyderabad.",
                    "Commemorated by the 182-meter Statue of Unity in Gujarat, the tallest statue in the world."
                ),
                prominentFigures = listOf("Sardar Vallabhbhai Patel", "VP Menon", "Lord Mountbatten"),
                stateSlug = "gujarat"
            ),
            HistoryTopicItem(
                id = "pol_constitution_republic",
                category = HistoryCategory.POLITICS,
                title = "Framing of the Indian Constitution",
                subtitle = "Dr. B.R. Ambedkar & Birth of the Sovereign Republic",
                periodEra = "1946 – 1950 CE",
                dynastyOrParty = "Constituent Assembly of India",
                location = "New Delhi (Constitution Hall)",
                imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                description = "The Constituent Assembly spent 2 years, 11 months, and 18 days drafting the Constitution of India under Dr. B.R. Ambedkar. Adopted on Nov 26, 1949 and enacted on Jan 26, 1950, founding the world's largest democracy.",
                keyFacts = listOf(
                    "Dr. B.R. Ambedkar served as the Chairman of the Drafting Committee, embedding fundamental rights and secular democracy.",
                    "The original Constitution was handwritten in calligraphy by Prem Behari Narain Raizada.",
                    "Enacted on January 26, 1950, celebrating Republic Day with universal adult suffrage."
                ),
                prominentFigures = listOf("Dr. B.R. Ambedkar", "Dr. Rajendra Prasad", "Jawaharlal Nehru", "BN Rau"),
                stateSlug = "delhi"
            ),
            HistoryTopicItem(
                id = "pol_modern_republic",
                category = HistoryCategory.POLITICS,
                title = "Post-Independence Political & Democratic Era",
                subtitle = "Elections, Green Revolution & Digital Bharat",
                periodEra = "1951 – Present",
                dynastyOrParty = "Democratic Electoral Republic",
                location = "Pan-India",
                imageUrl = "https://images.unsplash.com/photo-1588096344356-9a2f64e24296?auto=format&fit=crop&w=1200&q=80",
                description = "From India's first historic General Election in 1951–52 to the Green Revolution in agriculture, Pokhran nuclear tests, and space missions, India evolved into a global democratic superpower.",
                keyFacts = listOf(
                    "First General Elections in 1951-52 mobilized 173 million voters with universal suffrage.",
                    "MS Swaminathan led the Green Revolution, transforming India from food shortage to agricultural exporter.",
                    "APJ Abdul Kalam and ISRO propelled India into space and defense nuclear capability."
                ),
                prominentFigures = listOf("Dr. APJ Abdul Kalam", "MS Swaminathan", "Atal Bihari Vajpayee", "Lal Bahadur Shastri"),
                stateSlug = "delhi"
            )
        )
    }

    private fun getIndianAncientHistory(): List<HistoryTopicItem> {
        return listOf(
            HistoryTopicItem(
                id = "anc_indus_saraswati",
                category = HistoryCategory.ANCIENT,
                title = "Indus Valley & Saraswati Civilization",
                subtitle = "Urban Planning, Dholavira & Lothal Port",
                periodEra = "c. 3300 – 1300 BCE (Peak 2600–1900 BCE)",
                dynastyOrParty = "Harappan Bronze Age Civilization",
                location = "Gujarat (Dholavira, Lothal), Punjab (Ropar), Rajasthan (Kalibangan)",
                imageUrl = "https://images.unsplash.com/photo-1609949279531-cf48d64bed89?auto=format&fit=crop&w=1200&q=80",
                description = "One of the world's earliest urban civilizations, famous for grid-based street architecture, underground drainage, standardized terracotta weights, and international maritime trade at Lothal dockyard.",
                keyFacts = listOf(
                    "Lothal features the world's oldest engineered tidal dockyard connecting to the Arabian Sea.",
                    "Dholavira in the Rann of Kutch boasts advanced stone water reservoirs and signboards.",
                    "Pioneered copper metallurgy, seals with the Pashupati figure, and uniform town planning."
                ),
                prominentFigures = listOf("Harappan Engineers", "Maritime Traders", "Artisans & Craftsmen"),
                stateSlug = "gujarat"
            ),
            HistoryTopicItem(
                id = "anc_vedic_epics",
                category = HistoryCategory.ANCIENT,
                title = "Vedic Period & Sacred Indian Epics",
                subtitle = "Rigveda, Upanishads, Ramayana & Mahabharata",
                periodEra = "1500 – 500 BCE",
                dynastyOrParty = "Vedic Rishis & Kurukshetra Monarchies",
                location = "Saraswati & Ganga Basins (Haryana, UP, Bihar)",
                imageUrl = "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
                description = "Composition of the four Vedas, Upanishads, and the epic narratives Mahabharata and Ramayana, laying the philosophical foundations of Dharma, Karma, Yoga, and Indian classical thought.",
                keyFacts = listOf(
                    "The Rigveda is recognized as one of the oldest extant texts in any Indo-European language.",
                    "Upanishads explored metaphysical philosophy, introducing concepts of Atman and Brahman.",
                    "Kurukshetra in Haryana stands as the legendary battlefield of the Mahabharata and Bhagavad Gita."
                ),
                prominentFigures = listOf("Maharishi Ved Vyasa", "Maharishi Valmiki", "Yagnavalkya", "Gargi Vachaknavi"),
                stateSlug = "uttarakhand"
            ),
            HistoryTopicItem(
                id = "anc_mahajanapadas",
                category = HistoryCategory.ANCIENT,
                title = "Mahajanapadas & Magadha Hegemony",
                subtitle = "16 Great Kingdoms & Democratic Republics",
                periodEra = "600 – 322 BCE",
                dynastyOrParty = "Haryanka, Shishunaga & Nanda Dynasties",
                location = "Bihar (Rajgir, Pataliputra, Vaishali)",
                imageUrl = "https://images.unsplash.com/photo-1610484826917-0f101a7bf7f4?auto=format&fit=crop&w=1200&q=80",
                description = "Ancient India was organized into 16 Mahajanapadas (Great Kingdoms and Gana-Sangha Republics like Vajji at Vaishali). Magadha emerged as the dominant empire due to iron ore mines and riverine trade.",
                keyFacts = listOf(
                    "Vaishali in Bihar was the world's earliest recorded republican democracy with elected assemblies.",
                    "Bimbisara and Ajatashatru built cyclopean stone walls at Rajgir and fortified Pataliputra.",
                    "Contemporary with the spiritual awakenings of Gautama Buddha and Bhagwan Mahavira."
                ),
                prominentFigures = listOf("Bimbisara", "Ajatashatru", "Gautama Buddha", "Mahavira"),
                stateSlug = "bihar"
            ),
            HistoryTopicItem(
                id = "anc_gupta_golden_age",
                category = HistoryCategory.ANCIENT,
                title = "Gupta Empire — Golden Age of Science & Art",
                subtitle = "Aryabhata, Kalidasa, Ajanta & Monolithic Iron Pillar",
                periodEra = "320 – 550 CE",
                dynastyOrParty = "Gupta Dynasty",
                location = "Madhya Pradesh (Ujjain), UP, Maharashtra (Ajanta)",
                imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
                description = "Presided over unparalleled achievements in mathematics, astronomy, classical Sanskrit drama, and temple architecture. Produced the zero, decimal system, and rust-resistant Iron Pillar of Delhi.",
                keyFacts = listOf(
                    "Aryabhata calculated the value of Pi to 4 decimal places and proved the Earth rotates on its axis.",
                    "Kalidasa authored immortal Sanskrit masterpieces like 'Shakuntala' and 'Meghaduta' at Ujjain court.",
                    "The rustless Iron Pillar of Delhi erected under Chandragupta II displays advanced ancient metallurgy."
                ),
                prominentFigures = listOf("Aryabhata", "Kalidasa", "Chandragupta II Vikramaditya", "Varahamihira"),
                stateSlug = "madhya-pradesh"
            ),
            HistoryTopicItem(
                id = "anc_nalanda_takshashila",
                category = HistoryCategory.ANCIENT,
                title = "Ancient Universities of Bharat",
                subtitle = "Nalanda Mahavihara, Takshashila & Vikramashila",
                periodEra = "5th Century BCE – 12th Century CE",
                dynastyOrParty = "Gupta & Pala Royal Patronage",
                location = "Bihar (Nalanda), Punjab (Takshashila)",
                imageUrl = "https://images.unsplash.com/photo-1610484826917-0f101a7bf7f4?auto=format&fit=crop&w=1200&q=80",
                description = "Bharat was the world's premier destination for higher learning. Nalanda University accommodated 10,000 students and 2,000 international scholars studying astronomy, medicine, logic, and philosophy.",
                keyFacts = listOf(
                    "Nalanda's multi-story library 'Dharmaganja' housed millions of manuscript scrolls.",
                    "Takshashila taught medicine under Jivaka and military diplomacy under Chanakya.",
                    "Scholars like Xuanzang (Hiuen Tsang) traveled thousands of miles across Silk Route to study at Nalanda."
                ),
                prominentFigures = listOf("Xuanzang", "Dharmapala", "Aryabhata", "Nagarjuna"),
                stateSlug = "bihar"
            )
        )
    }

    private fun getIndianModernHistory(): List<HistoryTopicItem> {
        return listOf(
            HistoryTopicItem(
                id = "mod_maratha_expansion",
                category = HistoryCategory.MODERN,
                title = "Maratha Empire Expansion across India",
                subtitle = "Peshwa Baji Rao I & Pan-Indian Sovereignty",
                periodEra = "1707 – 1818 CE",
                dynastyOrParty = "Maratha Confederacy & Peshwas",
                location = "Maharashtra (Pune, Shaniwar Wada) & Central India",
                imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
                description = "Under Peshwa Baji Rao I and the Maratha Confederacy (Scindias, Holkars, Gaekwads, Bhonsles), Maratha military power expanded across Gujarat, Malwa, Bundelkhand, and reached Attock on the Indus River.",
                keyFacts = listOf(
                    "Peshwa Baji Rao I fought 41 major battles and remained undefeated in his entire military career.",
                    "Expanded Maratha saffron flag ('Bhagwa Dhwaj') from Pune to Attock in present-day Northwest.",
                    "Reconstructed desecrated heritage temples across Kashi, Somnath, and Mathura under Ahilyabai Holkar."
                ),
                prominentFigures = listOf("Peshwa Baji Rao I", "Ahilyabai Holkar", "Mahadaji Shinde", "Peshwa Balaji Vishwanath"),
                stateSlug = "maharashtra"
            ),
            HistoryTopicItem(
                id = "mod_bengal_renaissance",
                category = HistoryCategory.MODERN,
                title = "Bengal Renaissance & Social Reformers",
                subtitle = "Swami Vivekananda, Rabindranath Tagore & Reform",
                periodEra = "19th – Early 20th Century",
                dynastyOrParty = "Brahmo Samaj, Ramakrishna Mission & Literary Movement",
                location = "Kolkata, West Bengal",
                imageUrl = "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
                description = "A cultural, intellectual, and social awakening centered in Bengal. Spearheaded reforms against social evils, ignited modern Indian literature, and introduced Indian philosophy to the Western world.",
                keyFacts = listOf(
                    "Swami Vivekananda represented Hinduism at the Parliament of Religions in Chicago in 1893 with his electrifying address.",
                    "Rabindranath Tagore won the Nobel Prize in Literature in 1913 for 'Gitanjali' and composed National Anthems.",
                    "Raja Ram Mohan Roy and Ishwar Chandra Vidyasagar championed women's education and abolition of Sati."
                ),
                prominentFigures = listOf("Swami Vivekananda", "Rabindranath Tagore", "Raja Ram Mohan Roy", "Ramakrishna Paramahamsa"),
                stateSlug = "west-bengal"
            ),
            HistoryTopicItem(
                id = "mod_quit_india_1942",
                category = HistoryCategory.MODERN,
                title = "Quit India Movement (1942)",
                subtitle = "Do or Die Call & Final Push for Independence",
                periodEra = "1942 – 1945 CE",
                dynastyOrParty = "All India Congress Committee",
                location = "Mumbai (Gowalia Tank Maidan) & Pan-India",
                imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                description = "Launched at Gowalia Tank Maidan in Bombay on August 8, 1942. Mahatma Gandhi gave the clarion call 'Do or Die' (Karo Ya Maro), paralyzing British administration across the subcontinent.",
                keyFacts = listOf(
                    "Aruna Asaf Ali courageously hoisted the Indian National Flag at Gowalia Tank Maidan.",
                    "Parallel secret governments ('Prati Sarkar') were established in Satara, Ballia, and Tamluk.",
                    "Underground radio transmissions operated by Usha Mehta kept nationalist communications alive."
                ),
                prominentFigures = listOf("Mahatma Gandhi", "Aruna Asaf Ali", "Usha Mehta", "Jayaprakash Narayan"),
                stateSlug = "maharashtra"
            ),
            HistoryTopicItem(
                id = "mod_independence_1947",
                category = HistoryCategory.MODERN,
                title = "Partition & Indian Independence (15 August 1947)",
                subtitle = "Tryst with Destiny & Dawn of Free India",
                periodEra = "August 15, 1947",
                dynastyOrParty = "Dominion of India",
                location = "New Delhi (Red Fort)",
                imageUrl = "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
                description = "At the stroke of midnight on August 15, 1947, India achieved independence after nearly two centuries of British colonial rule, marked by Jawaharlal Nehru's famous 'Tryst with Destiny' speech at Parliament.",
                keyFacts = listOf(
                    "First Prime Minister Jawaharlal Nehru unfurled the Tricolor Flag at Lahori Gate, Red Fort on Aug 15, 1947.",
                    "Accompanied by the tragic Partition of Punjab and Bengal, leading to mass migrations.",
                    "Established India as an independent sovereign democracy."
                ),
                prominentFigures = listOf("Jawaharlal Nehru", "Sardar Vallabhbhai Patel", "Lord Mountbatten", "Sarojini Naidu"),
                stateSlug = "delhi"
            ),
            HistoryTopicItem(
                id = "mod_isro_space_era",
                category = HistoryCategory.MODERN,
                title = "Modern Scientific & Space Conquest (ISRO)",
                subtitle = "Vikram Sarabhai, Chandrayaan & Mangalyaan",
                periodEra = "1962 – Present",
                dynastyOrParty = "Indian Space Research Organisation (ISRO)",
                location = "Karnataka (Bengaluru) & Andhra Pradesh (Sriharikota)",
                imageUrl = "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80",
                description = "Founded by Dr. Vikram Sarabhai, ISRO transformed India into a global space power. From transporting satellite parts on bicycles in 1963 to discovering water on the Moon (Chandrayaan-1) and reaching Mars on first attempt (Mangalyaan).",
                keyFacts = listOf(
                    "Chandrayaan-1 (2008) discovered water molecules on the lunar surface.",
                    "Mangalyaan (Mars Orbiter Mission 2013) made India the first nation to reach Martian orbit on its maiden attempt.",
                    "Chandrayaan-3 (2023) landed successfully at the Moon's South Pole, making India the 1st country to accomplish south pole lunar landing."
                ),
                prominentFigures = listOf("Dr. Vikram Sarabhai", "Dr. APJ Abdul Kalam", "Satish Dhawan", "K. Sivan", "S. Somanath"),
                stateSlug = "karnataka"
            )
        )
    }
}

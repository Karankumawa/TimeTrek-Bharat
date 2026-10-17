package com.example.timetrekbharat.repository

import android.content.Context
import com.example.timetrekbharat.model.HistoryCategory
import com.example.timetrekbharat.model.HistoryTopicItem
import com.example.timetrekbharat.network.RetrofitClient

object HistoryContentRepository {

    suspend fun fetchTopicsFromSupabase(category: HistoryCategory, context: Context? = null): List<HistoryTopicItem> {
        val folderName = when (category) {
            HistoryCategory.KINGS -> "indian_kings"
            HistoryCategory.POLITICS -> "indian_politics"
            HistoryCategory.ANCIENT -> "ancient_history"
            HistoryCategory.MODERN -> "modern_history"
            else -> "statesimage"
        }

        try {
            val response = RetrofitClient.api.getHistoryTopics("eq.${category.id}")
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                val networkTopics = response.body()!!
                return networkTopics.map { topic ->
                    val resolvedUrl = resolveAssetImage(context, folderName, topic.id, topic.title, topic.stateSlug, topic.imageUrl)
                    topic.copy(category = category, imageUrl = resolvedUrl)
                }
            }
        } catch (_: Exception) {
            // Fallback to local offline topics if network fails
        }

        val fallbackTopics = getLocalFallbackTopicsByCategory(category)
        return fallbackTopics.map { topic ->
            val resolvedUrl = resolveAssetImage(context, folderName, topic.id, topic.title, topic.stateSlug, topic.imageUrl)
            topic.copy(category = category, imageUrl = resolvedUrl)
        }
    }

    private fun resolveAssetImage(
        context: Context?,
        folderName: String,
        topicId: String,
        title: String,
        stateSlug: String?,
        fallbackUrl: String
    ): String {
        if (context != null) {
            try {
                val files = context.assets.list(folderName)?.toList() ?: emptyList()
                val cleanId = topicId.lowercase().replace("-", "_")
                val cleanTitle = title.lowercase().replace(" ", "_").replace("-", "_")

                val matchedFile = files.firstOrNull { fileName ->
                    !fileName.startsWith(".") && (
                        fileName.endsWith(".jpg", ignoreCase = true) ||
                        fileName.endsWith(".jpeg", ignoreCase = true) ||
                        fileName.endsWith(".png", ignoreCase = true) ||
                        fileName.endsWith(".webp", ignoreCase = true)
                    ) && (
                        fileName.substringBeforeLast(".").lowercase().replace("-", "_") == cleanId ||
                        fileName.substringBeforeLast(".").lowercase().replace("-", "_") == cleanTitle ||
                        cleanId.contains(fileName.substringBeforeLast(".").lowercase().replace("-", "_"))
                    )
                }
                if (matchedFile != null) {
                    return "file:///android_asset/$folderName/$matchedFile"
                }
            } catch (_: Exception) {}

            // Fallback check in statesimage directory if linked to a state
            if (!stateSlug.isNullOrBlank()) {
                val cleanStateSlug = stateSlug.lowercase().replace("-", "").replace("_", "")
                val dirsToCheck = listOf(
                    "statesimage",
                    "statesimages",
                    "statesimage/${cleanStateSlug}images",
                    "statesimages/${cleanStateSlug}images",
                    "statesimage/${stateSlug}images",
                    "statesimages/${stateSlug}images"
                )

                for (dir in dirsToCheck) {
                    try {
                        val files = context.assets.list(dir)?.toList() ?: emptyList()
                        val matchedFile = files.firstOrNull { fileName ->
                            !fileName.startsWith(".") && (
                                fileName.substringBeforeLast(".").equals(stateSlug, ignoreCase = true) ||
                                fileName.substringBeforeLast(".").replace("-", "").equals(cleanStateSlug, ignoreCase = true)
                            )
                        }
                        if (matchedFile != null) {
                            return "file:///android_asset/$dir/$matchedFile"
                        }
                    } catch (_: Exception) {}
                }
            }
        }

        return fallbackUrl
    }

    private fun getLocalFallbackTopicsByCategory(category: HistoryCategory): List<HistoryTopicItem> {
        return when (category) {
            HistoryCategory.KINGS -> getIndianKingsAndDynasties()
            HistoryCategory.POLITICS -> getIndianPoliticsAndFreedomMovement()
            HistoryCategory.ANCIENT -> getIndianAncientHistory()
            HistoryCategory.MODERN -> getIndianModernHistory()
            HistoryCategory.STATES -> emptyList()
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
                periodEra = "c. 3300 – 1300 BCE",
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

import sys
from pymongo import MongoClient
from config import Config

# Exam-oriented General Knowledge (GK) & Indian History Dataset for UPSC, SSC, Railways & State PSC Exams
SAMPLE_STATES_DATA = [
    {
        "slug": "rajasthan",
        "name": "Rajasthan",
        "region": "North-West India",
        "capital": "Jaipur",
        "short_description": "Land of Kings & Hill Forts (UNESCO World Heritage). Exam Highlights: Kalibangan (IVC ploughed field), Battle of Tarain (1191, 1192), Battle of Khanwa (1527), Battle of Haldighati (1576), Sawai Jai Singh II (Jantar Mantar).",
        "image_url": "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 2500 BCE – 1500 BCE",
                "title": "Kalibangan & Harappan Civilization (Exam Key Point)",
                "description": "Kalibangan in Hanumangarh district on Ghaggar (Sarasvati) river is famous for the earliest known ploughed field and fire altars in human history.",
                "key_events": [
                    "Earliest ploughed field discovery (UPSC/SSC favorite)",
                    "Evidence of fire altars indicating ritual worship",
                    "Cylindrical seals and terracotta bull figurines"
                ],
                "key_rulers": ["Indus Valley Guild Leaders"],
                "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1191 AD – 1576 AD",
                "title": "Prithviraj Chauhan & Maharana Pratap",
                "description": "Prithviraj III defeated Ghori in 1st Battle of Tarain (1191). Maharana Pratap fought Akbar's forces led by Man Singh I at Battle of Haldighati (1576).",
                "key_events": [
                    "1st and 2nd Battles of Tarain (1191, 1192 AD)",
                    "Siege of Chittorgarh & Rani Padmini's Jauhar (1303 AD)",
                    "Battle of Haldighati (1576 AD) - Mewar Resistance"
                ],
                "key_rulers": ["Prithviraj Chauhan", "Rana Sanga", "Maharana Pratap", "Sawai Jai Singh II"],
                "image_url": "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "gujarat",
        "name": "Gujarat",
        "region": "Western India",
        "capital": "Gandhinagar",
        "short_description": "Cradle of maritime trade & Freedom Movement. Exam Highlights: Lothal (World's oldest dockyard), Dholavira (UNESCO water harvesting), Solanki Sun Temple (Modhera), Rani ki Vav, Dandi March (1930).",
        "image_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 2400 BCE – 1900 BCE",
                "title": "Lothal Tidal Dockyard & Dholavira Water Engineering",
                "description": "Lothal was the primary Harappan port with a brick dockyard. Dholavira in Kutch featured unique 3-tier city planning and stone water reservoirs.",
                "key_events": [
                    "Lothal artificial tidal basin dockyard for Persian Gulf trade",
                    "Dholavira 10-character signboard and stone architecture",
                    "Surkotada evidence of horse remains"
                ],
                "key_rulers": ["Harappan Maritime Guilds"],
                "image_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1918 AD – 1930 AD",
                "title": "Satyagraha Movement & Dandi Salt March",
                "description": "Mahatma Gandhi launched Kheda Satyagraha (1918), Bardoli Satyagraha under Sardar Patel (1928), and the historic Dandi March from Sabarmati Ashram in 1930.",
                "key_events": [
                    "Kheda Satyagraha (1918) - First Non-Cooperation movement",
                    "Bardoli Satyagraha (1928) - Vallabhbhai Patel awarded title 'Sardar'",
                    "Dandi Salt March (March 12 – April 6, 1930)"
                ],
                "key_rulers": ["Mahatma Gandhi", "Sardar Vallabhbhai Patel"],
                "image_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "delhi",
        "name": "Delhi",
        "region": "Northern India",
        "capital": "New Delhi",
        "short_description": "Capital of Seven Cities. Exam Highlights: Iron Pillar of Chandragupta II (Mehrauli), Qutub Minar (Aibak & Iltutmish), Razia Sultana (First female ruler 1236-1240), Alai Darwaza (Khalji), Red Fort (Shah Jahan).",
        "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 1000 BCE – 1052 AD",
                "title": "Indraprastha & Anangpal Tomar's Lal Kot",
                "description": "Anangpal Tomar II founded Lal Kot in 1052 AD. Mehrauli Iron Pillar erected by Chandragupta II Vikramaditya shows rust-free metallurgy.",
                "key_events": [
                    "Relocation of Gupta Iron Pillar to Mehrauli",
                    "Construction of Lal Kot fortress and Surajkund",
                    "Chauhan conquest of Delhi by Vigraharaja IV"
                ],
                "key_rulers": ["Anangpal Tomar II", "Prithviraj Chauhan"],
                "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1206 AD – 1857 AD",
                "title": "Delhi Sultanate & Mughal Empire Capital",
                "description": "Five dynasties of Delhi Sultanate ruled from 1206 to 1526. Shah Jahan built Shahjahanabad (Old Delhi), Red Fort, and Jama Masjid in 1638.",
                "key_events": [
                    "Qutub Minar & Quwwat-ul-Islam Mosque construction",
                    "Reign of Razia Sultana (1236–1240 AD)",
                    "Market & Agrarian Reforms of Alauddin Khalji (Dag & Hulia systems)",
                    "Shift of Mughal Capital from Agra to Delhi (1638 AD)"
                ],
                "key_rulers": ["Qutb-ud-din Aibak", "Razia Sultana", "Alauddin Khalji", "Shah Jahan"],
                "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "maharashtra",
        "name": "Maharashtra",
        "region": "Western India",
        "capital": "Mumbai",
        "short_description": "Land of Maratha Hindavi Swarajya & Rock-Cut Caves. Exam Highlights: Ajanta (Buddhist murals), Ellora Cave 16 (Kailashnath monolithic temple), Chhatrapati Shivaji Maharaj Coronation (Raigad 1674), Treaty of Purandar, Peshwa Rule.",
        "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "200 BCE – 800 AD",
                "title": "Ellora Kailashnath Monolith & Ajanta Frescoes",
                "description": "Rashtrakuta King Krishna I carved the monolithic Kailashnath Temple at Ellora out of a single rock top-down. Ajanta caves feature Mahayana mural paintings.",
                "key_events": [
                    "Kailashnath Temple (Ellora Cave 16) carved under Krishna I",
                    "Gautamiputra Satakarni Nasik Prashasti inscription",
                    "Tagara (Ter) & Kalyan ancient trade centers"
                ],
                "key_rulers": ["Gautamiputra Satakarni", "Krishna I (Rashtrakuta)"],
                "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1674 AD – 1818 AD",
                "title": "Maratha Empire & Ashta Pradhan System",
                "description": "Chhatrapati Shivaji Maharaj pioneered Ganimi Kava guerilla warfare and established the Council of Eight Ministers (Ashta Pradhan). Peshwa Baji Rao I expanded empire across India.",
                "key_events": [
                    "Coronation at Raigad Fort & Title 'Chhatrapati' (1674 AD)",
                    "Ashta Pradhan administrative council setup",
                    "Third Battle of Panipat (1761 AD) under Sadashivrao Bhau"
                ],
                "key_rulers": ["Chhatrapati Shivaji Maharaj", "Chhatrapati Sambhaji Maharaj", "Peshwa Baji Rao I"],
                "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "uttar-pradesh",
        "name": "Uttar Pradesh",
        "region": "Northern India",
        "capital": "Lucknow",
        "short_description": "Gangetic Heartland & Birthplace of Revolts. Exam Highlights: Mahajanapadas (Kashi, Kosala, Vatsa, Malla), Buddha's 1st Sermon (Sarnath Dhamek Stupa), Agra Fort & Taj Mahal, 1857 Revolt (Meerut, Jhansi, Kanpur, Lucknow).",
        "image_url": "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 528 BCE – 300 AD",
                "title": "Buddha's First Sermon at Sarnath & Mathura School of Art",
                "description": "Gautam Buddha delivered his first sermon (Dharmachakrapravartana) at Sarnath Deer Park. Mathura evolved as a major red sandstone sculpture center under Kushanas.",
                "key_events": [
                    "First Sermon of Buddha at Sarnath (Dharmachakrapravartana)",
                    "Ashoka Lion Capital pillar at Sarnath (India's National Emblem)",
                    "Mathura Art School under Kanishka"
                ],
                "key_rulers": ["Emperor Ashoka", "Kanishka I"],
                "image_url": "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1857 AD – 1922 AD",
                "title": "1857 First War of Independence & Chauri Chaura",
                "description": "1857 Revolt broke out at Meerut (May 10). Rani Lakshmibai (Jhansi), Begum Hazrat Mahal (Lucknow), Nana Saheb & Tatya Tope (Kanpur) led uprising. Chauri Chaura event (1922) led Gandhi to halt Non-Cooperation Movement.",
                "key_events": [
                    "Outbreak of 1857 Uprising at Meerut",
                    "Lucknow Defense by Begum Hazrat Mahal",
                    "Chauri Chaura incident near Gorakhpur (Feb 4, 1922)"
                ],
                "key_rulers": ["Rani Lakshmibai", "Begum Hazrat Mahal", "Nana Saheb"],
                "image_url": "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "tamil-nadu",
        "name": "Tamil Nadu",
        "region": "Southern India",
        "capital": "Chennai",
        "short_description": "Dravidian Architectural Marvels & Chola Naval Empire. Exam Highlights: Sangam Literature (Thirukkural), Mahabalipuram Pancha Rathas (Pallavas), Brihadeeswarar Temple (Rajaraja Chola I), Chola Maritime Expeditions to Srivijaya.",
        "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "300 BCE – 700 AD",
                "title": "Sangam Assemblies & Pallava Rock Cut Art",
                "description": "Three Tamil Sangams held at Madurai produced classical literature. Narasimhavarman I built Mahabalipuram Shore Temple & Pancha Rathas.",
                "key_events": [
                    "Compilation of Silappatikaram and Manimekalai epics",
                    "Construction of Mahabalipuram monolithic rathas",
                    "Pallava-Chalukya conflict (Battle of Vatapi)"
                ],
                "key_rulers": ["Karikala Chola", "Mahendravarman I", "Narasimhavarman I"],
                "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "850 AD – 1279 AD",
                "title": "Chola Naval Power & Great Living Temples",
                "description": "Rajaraja Chola I built Brihadeeswarar Temple (Thanjavur) in 1010 AD. Rajendra Chola I assumed title 'Gangaikonda Chola' and launched Southeast Asian naval strikes.",
                "key_events": [
                    "Brihadeeswarar Temple granite vimana construction",
                    "Naval victory over Srivijaya Empire (Malaya/Sumatra)",
                    "Local self-government democratic system (Uttiramerur inscriptions)"
                ],
                "key_rulers": ["Rajaraja Chola I", "Rajendra Chola I"],
                "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "bihar",
        "name": "Bihar",
        "region": "Eastern India",
        "capital": "Patna",
        "short_description": "Seat of Ancient Empires & Universities. Exam Highlights: Mahavira (Kundagrama) & Buddha (Bodh Gaya), Maurya Empire (Chandragupta & Chanakya Arthashastra), Ashokan Pillar Edicts, Nalanda Mahavihara (Kumaragupta I), Champaran Satyagraha (1917).",
        "image_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 563 BCE – 232 BCE",
                "title": "Rise of Jainism, Buddhism & Maurya Empire",
                "description": "Bodh Gaya host to Buddha's Enlightenment. Chandragupta Maurya defeated Dhana Nanda with Chanakya's guidance, establishing Pataliputra capital.",
                "key_events": [
                    "First Buddhist Council at Rajgriha (483 BCE) under Ajatashatru",
                    "Compilation of Chanakya's Arthashastra on statecraft",
                    "Kalinga War & Ashoka's Major Rock Edicts"
                ],
                "key_rulers": ["Bimbisara", "Chandragupta Maurya", "Emperor Ashoka"],
                "image_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1917 AD – 1942 AD",
                "title": "Champaran Satyagraha & Quit India Movement",
                "description": "Mahatma Gandhi launched his 1st Satyagraha in India at Champaran (1917) against European Indigo planters (Tinkathia system). Kunwar Singh led 1857 revolt in Jagdishpur.",
                "key_events": [
                    "Champaran Satyagraha (1917) - Abolition of Tinkathia system",
                    "1857 Revolt leadership by 80-year-old Veer Kunwar Singh",
                    "Patna Secretariat Martyr's Memorial during Quit India (1942)"
                ],
                "key_rulers": ["Veer Kunwar Singh", "Mahatma Gandhi", "Dr. Rajendra Prasad"],
                "image_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "karnataka",
        "name": "Karnataka",
        "region": "Southern India",
        "capital": "Bengaluru",
        "short_description": "Stone Architecture Cradle & Vijayanagara Empire. Exam Highlights: Badami Cave Temples (Chalukyas), Pattadakal (UNESCO site), Vijayanagara Empire at Hampi (Harihara, Bukka, Krishnadevaraya), Mysore Kingdom (Hyder Ali & Tipu Sultan).",
        "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Medieval Era",
                "period": "1336 AD – 1565 AD",
                "title": "Vijayanagara Empire at Hampi & Krishnadevaraya",
                "description": "Harihara I & Bukka I founded Vijayanagara in 1336 on Tungabhadra river. Krishnadevaraya (Tuluva dynasty) authored Amuktamalyada in Telugu.",
                "key_events": [
                    "Construction of Vittala Temple Stone Chariot at Hampi",
                    "Foreign accounts by Domingo Paes, Niccolo de Conti, Abdur Razzaq",
                    "Battle of Talikota / Rakshasi-Tangadi (1565 AD)"
                ],
                "key_rulers": ["Harihara I", "Krishnadevaraya"],
                "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Colonial Era",
                "period": "1761 AD – 1799 AD",
                "title": "Anglo-Mysore Wars & Tipu Sultan",
                "description": "Four Anglo-Mysore Wars fought between Mysore and British East India Company. Tipu Sultan ('Tiger of Mysore') used iron-cased Mysorean rockets.",
                "key_events": [
                    "Siege of Seringapatam & Treaty of Seringapatam (1792)",
                    "Death of Tipu Sultan in 4th Anglo-Mysore War (1799 AD)",
                    "Introduction of Mysorean rocket artillery"
                ],
                "key_rulers": ["Hyder Ali", "Tipu Sultan"],
                "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "haryana",
        "name": "Haryana",
        "region": "Northern India",
        "capital": "Chandigarh",
        "short_description": "Land of Historic Battles & Epics. Exam Highlights: Kurukshetra (Mahabharata war & Bhagavad Gita exposition), Three Battles of Panipat (1526, 1556, 1761), Banawali (IVC site), Surajkund.",
        "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 2500 BCE – 600 BCE",
                "title": "Banawali Harappan Site & Kurukshetra",
                "description": "Banawali in Fatehabad district revealed high quality barley and radial city planning. Kurukshetra is the legendary battleground of Mahabharata.",
                "key_events": [
                    "Banawali IVC excavations showing fortified township",
                    "Vedic Sarasvati river basin settlements",
                    "Exposition of Bhagavad Gita at Jyotisar, Kurukshetra"
                ],
                "key_rulers": ["Kuru Dynasty Kings"],
                "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1526 AD – 1761 AD",
                "title": "Three Decisive Battles of Panipat",
                "description": "Panipat witnessed 3 turning-point battles in Indian history: Babur vs Ibrahim Lodi (1526), Akbar/Bairam Khan vs Hemu (1556), Ahmad Shah Abdali vs Marathas (1761).",
                "key_events": [
                    "1st Battle of Panipat (1526) - Foundation of Mughal Empire",
                    "2nd Battle of Panipat (1556) - Consolidation under Akbar",
                    "3rd Battle of Panipat (1761) - Maratha expansion halted"
                ],
                "key_rulers": ["Babur", "Akbar", "Hemu (Hemchandra)", "Ahmad Shah Abdali"],
                "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "punjab",
        "name": "Punjab",
        "region": "North-West India",
        "capital": "Chandigarh",
        "short_description": "Land of Five Rivers & Sikh Heritage. Exam Highlights: Ropar (1st IVC site excavated in Independent India), Sri Harmandir Sahib, Maharaja Ranjit Singh Empire, Jallianwala Bagh Massacre (Amritsar 1919).",
        "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Medieval Era",
                "period": "1799 AD – 1849 AD",
                "title": "Sikh Sovereign Empire & Maharaja Ranjit Singh",
                "description": "Maharaja Ranjit Singh ('Sher-e-Punjab') unified 12 Misls, established capital at Lahore, gold-plated Harmandir Sahib, and built modernized Fauj-i-Khas army.",
                "key_events": [
                    "Unification of Sikh Misls under Sukerchakia Misl",
                    "Gold-plating of Sri Harmandir Sahib (Golden Temple)",
                    "Treaty of Amritsar (1809) with British East India Company"
                ],
                "key_rulers": ["Maharaja Ranjit Singh", "Hari Singh Nalwa"],
                "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1919 AD – 1931 AD",
                "title": "Jallianwala Bagh Massacre & Shaheed Bhagat Singh",
                "description": "General Dyer fired on unarmed crowd at Jallianwala Bagh on Baisakhi (April 13, 1919) protesting Rowlatt Act. Rabindranath Tagore renounced Knighthood. Bhagat Singh, Rajguru & Sukhdev martyred in 1931.",
                "key_events": [
                    "Jallianwala Bagh Massacre (April 13, 1919) & Hunter Commission setup",
                    "Establishment of Naujawan Bharat Sabha by Bhagat Singh (1926)",
                    "Martyrdom of Shaheed Bhagat Singh (March 23, 1931)"
                ],
                "key_rulers": ["Shaheed Bhagat Singh", "Lala Lajpat Rai", "Udham Singh"],
                "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "west-bengal",
        "name": "West Bengal",
        "region": "Eastern India",
        "capital": "Kolkata",
        "short_description": "Epicenter of British Colonial Rule & Bengal Renaissance. Exam Highlights: Battle of Plassey (1757 - Clive vs Siraj-ud-Daulah), Battle of Buxar (1764), Permanent Settlement (Cornwallis 1793), Partition of Bengal (Curzon 1905), Swadeshi Movement.",
        "image_url": "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Colonial Era",
                "period": "1757 AD – 1765 AD",
                "title": "Battle of Plassey & Diwani Rights",
                "description": "Robert Clive defeated Nawab Siraj-ud-Daulah at Plassey (June 23, 1757) due to Mir Jafar's treachery. Treaty of Allahabad (1765) granted Diwani rights (revenue collection) to East India Company.",
                "key_events": [
                    "Battle of Plassey (1757) - Beginning of British political control",
                    "Battle of Buxar (1764) - Defeat of Mir Qasim, Shuja-ud-Daula & Shah Alam II",
                    "Treaty of Allahabad (1765) & Dual Government system of Clive"
                ],
                "key_rulers": ["Siraj-ud-Daulah", "Robert Clive", "Warren Hastings"],
                "image_url": "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1905 AD – 1943 AD",
                "title": "Partition of Bengal (1905) & Netaji Subhas Chandra Bose",
                "description": "Lord Curzon partitioned Bengal in 1905 sparking Swadeshi & Boycott movements. Netaji Subhas Chandra Bose founded Forward Bloc and reorganized Indian National Army (INA / Azad Hind Fauj).",
                "key_events": [
                    "Partition of Bengal (1905) & Vande Mataram chant origin",
                    "Forming of Anushilan Samiti & Jugantar revolutionary groups",
                    "Netaji Subhas Chandra Bose & Azad Hind Government (1943)"
                ],
                "key_rulers": ["Raja Ram Mohan Roy", "Rabindranath Tagore", "Netaji Subhas Chandra Bose"],
                "image_url": "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "madhya-pradesh",
        "name": "Madhya Pradesh",
        "region": "Central India",
        "capital": "Bhopal",
        "short_description": "Heart of India & Prehistoric/Buddhist Monuments. Exam Highlights: Bhimbetka Rock Shelters (Paleolithic cave paintings), Sanchi Stupa (Ashoka), Khajuraho Temples (Chandela Rajputs), Gwalior Fort (Tomar dynasty), Rani Durgavati.",
        "image_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 100,000 BCE – 100 BCE",
                "title": "Bhimbetka Cave Art & Sanchi Stupa",
                "description": "Bhimbetka in Raisen district shows human cave art from Paleolithic & Mesolithic eras. Emperor Ashoka built Great Stupa 1 at Sanchi.",
                "key_events": [
                    "Bhimbetka rock shelters discovered by V. S. Wakankar (1957)",
                    "Sanchi Stupa sandstone gateways (Toranas) under Shungas",
                    "Heliodorus Pillar at Vidisha (Besnagar) - Vasudeva worship"
                ],
                "key_rulers": ["Emperor Ashoka", "Heliodorus (Greek Ambassador)"],
                "image_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "950 AD – 1857 AD",
                "title": "Khajuraho Temples & Rani Lakshmibai at Gwalior",
                "description": "Chandela kings built Khajuraho temples in Nagara architectural style. Rani Lakshmibai sacrificed her life fighting British forces at Gwalior in June 1858.",
                "key_events": [
                    "Kandariya Mahadeva temple construction at Khajuraho",
                    "Rani Durgavati's defense of Garha-Katanga against Asaf Khan",
                    "Martyrdom of Rani Lakshmibai at Gwalior (1858 AD)"
                ],
                "key_rulers": ["Yashovarman", "Rani Durgavati", "Rani Lakshmibai"],
                "image_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "kerala",
        "name": "Kerala",
        "region": "Southern India",
        "capital": "Thiruvananthapuram",
        "short_description": "Spice Coast & Early European Encounters. Exam Highlights: Chera Dynasty, Vasco da Gama landing at Calicut/Kozhikode (1498), Battle of Colachel (1741 - Marthanda Varma defeats Dutch), Vaikom Satyagraha (1924).",
        "image_url": "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Colonial Era",
                "period": "1498 AD – 1741 AD",
                "title": "Vasco da Gama Landing & Battle of Colachel",
                "description": "Vasco da Gama reached Kapad near Kozhikode in May 1498, opening sea route to India. Marthanda Varma defeated Dutch VOC armada at Colachel in 1741.",
                "key_events": [
                    "Arrival of Vasco da Gama received by Zamorin of Calicut (1498)",
                    "Battle of Colachel (1741) - First Asian naval victory over Europeans",
                    "Treaty of Mavelikkara ending Dutch monopoly in Malabar"
                ],
                "key_rulers": ["Zamorin of Calicut", "Anizham Thirunal Marthanda Varma"],
                "image_url": "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1921 AD – 1924 AD",
                "title": "Malabar / Moplah Rebellion & Vaikom Satyagraha",
                "description": "Moplah Rebellion took place in Malabar in 1921. Vaikom Satyagraha (1924–25) led by K. Kelappan & Periyar E. V. Ramasamy fought temple entry discrimination.",
                "key_events": [
                    "Malabar / Moplah Rebellion (1921)",
                    "Vaikom Satyagraha (1924) for temple road access",
                    "Temple Entry Proclamation by Chithira Thirunal (1936)"
                ],
                "key_rulers": ["K. Kelappan (Kerala Gandhi)", "Periyar E. V. Ramasamy"],
                "image_url": "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "odisha",
        "name": "Odisha",
        "region": "Eastern India",
        "capital": "Bhubaneswar",
        "short_description": "Land of Kalinga & Maritime Trade. Exam Highlights: Kalinga War (261 BCE - Ashoka's conversion), Kharavela Hathigumpha Inscription, Konark Sun Temple (Narasimhadeva I), Paika Rebellion (1817 - Bakshi Jagabandhu).",
        "image_url": "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "261 BCE – 150 BCE",
                "title": "Kalinga War & King Kharavela's Hathigumpha Inscription",
                "description": "Ashoka conquered Kalinga in 261 BCE (Dhauli Edicts). King Kharavela of Chedi dynasty recorded 13 years of victories in Brahmi Hathigumpha Inscription at Udayagiri.",
                "key_events": [
                    "Kalinga War (261 BCE) & Dhauli Rock Edict XIII",
                    "Hathigumpha Prakrit Inscription of King Kharavela",
                    "Maritime Sadhabas Bali Yatra sea commerce to South-East Asia"
                ],
                "key_rulers": ["Emperor Ashoka", "King Kharavela"],
                "image_url": "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1817 AD",
                "title": "Paika Rebellion - Early Armed Uprising against British",
                "description": "Bakshi Jagabandhu Bidyadhar led the Paikas (hereditary landed militia) of Khurda in an armed revolt against British East India Company land tax policies in 1817.",
                "key_events": [
                    "Paika Bidroha / Rebellion (1817 AD)",
                    "Attack on British treasury at Banapur & Khurda",
                    "Recognition as one of India's earliest organized anti-colonial revolts"
                ],
                "key_rulers": ["Bakshi Jagabandhu", "Raja Mukunda Deva II"],
                "image_url": "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "assam",
        "name": "Assam",
        "region": "North-East India",
        "capital": "Dispur",
        "short_description": "Invincible Ahom Dynasty & Tea Industry. Exam Highlights: Kamarupa Kingdom (Bhaskaravarman), Ahom Rule (1228-1826), Battle of Saraighat (1671 - Lachit Borphukan defeats Mughals), Treaty of Yandabo (1826).",
        "image_url": "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Medieval Era",
                "period": "1228 AD – 1671 AD",
                "title": "Ahom Kingdom & Battle of Saraighat",
                "description": "Sukaphaa established Ahom kingdom in 1228. General Lachit Borphukan defeated Mughal Admiral Ram Singh I in naval Battle of Saraighat on Brahmaputra river (1671).",
                "key_events": [
                    "Founding of Ahom rule at Charaideo by Sukaphaa (1228)",
                    "Naval Battle of Saraighat (1671) - Defense of Kamrup",
                    "Construction of Rang Ghar & Kareng Ghar amphitheaters"
                ],
                "key_rulers": ["Chaolung Sukaphaa", "Lachit Borphukan", "Rudra Singha"],
                "image_url": "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Colonial Era",
                "period": "1826 AD – 1861 AD",
                "title": "Treaty of Yandabo & Phulaguri Dhawa Revolt",
                "description": "Treaty of Yandabo (1826) ended 1st Anglo-Burmese War and brought Assam under British rule. Phulaguri Dhawa (1861) was the 1st peasant uprising in Assam.",
                "key_events": [
                    "Treaty of Yandabo (1826) annexation by East India Company",
                    "Phulaguri Dhawa peasant revolt against opium tax (1861)",
                    "Discovery of Assam tea by Robert Bruce (1823)"
                ],
                "key_rulers": ["Maniram Dewan", "Gomdhar Konwar"],
                "image_url": "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "telangana",
        "name": "Telangana",
        "region": "Southern India",
        "capital": "Hyderabad",
        "short_description": "Kakatiya Dynasty & Golconda Fort. Exam Highlights: Kakatiyas (Warangal Fort, Thousand Pillar Temple, Ramappa UNESCO site), Rani Rudrama Devi, Qutb Shahi Dynasty (Charminar 1591), Hyderabad State Accession (Operation Polo 1948).",
        "image_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Medieval Era",
                "period": "1163 AD – 1591 AD",
                "title": "Kakatiya Rule & Charminar Foundation",
                "description": "Rani Rudrama Devi & Prataparudra built Warangal Fort and Ramappa Temple. Muhammad Quli Qutb Shah founded Hyderabad and built Charminar in 1591.",
                "key_events": [
                    "Ramappa Temple (Rudreshwara) construction with floating bricks",
                    "Reign of warrior queen Rani Rudrama Devi (Marco Polo visit)",
                    "Construction of Charminar & Golconda acoustic fort"
                ],
                "key_rulers": ["Rani Rudrama Devi", "Muhammad Quli Qutb Shah"],
                "image_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1946 AD – 1948 AD",
                "title": "Telangana Peasant Rebellion & Operation Polo",
                "description": "Telangana Peasant Armed Struggle (1946–51) fought Nizam feudal Razakars. Indian Army launched Operation Polo in September 1948 to integrate Hyderabad into India.",
                "key_events": [
                    "Telangana Peasant Movement against Visnoori Deshmukhs",
                    "Operation Polo military action (Sept 13–18, 1948)",
                    "Accession of Hyderabad State under Nizam Mir Osman Ali Khan"
                ],
                "key_rulers": ["Sardar Vallabhbhai Patel", "Komaram Bheem", "Chakali Ilamma"],
                "image_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "jammu-and-kashmir",
        "name": "Jammu & Kashmir",
        "region": "Northern India",
        "capital": "Srinagar / Jammu",
        "short_description": "Trans-Himalayan Empire & Kalhana's Rajatarangini. Exam Highlights: Kalhana's Rajatarangini (First historical chronicle of India), Lalitaditya Muktapida (Martand Sun Temple), Zain-ul-Abidin (Bud Shah), Dogra Dynasty (Gulab Singh).",
        "image_url": "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "625 AD – 1148 AD",
                "title": "Karkota Dynasty & Kalhana's Rajatarangini",
                "description": "Emperor Lalitaditya built Martand Sun Temple. Kalhana compiled 'Rajatarangini' (River of Kings) in 1148 AD, India's first systematic historical chronicle.",
                "key_events": [
                    "Martand Sun Temple construction near Anantnag",
                    "Compilation of Rajatarangini by Kalhana in Sanskrit",
                    "4th Buddhist Council at Kundalvana under Kanishka"
                ],
                "key_rulers": ["Lalitaditya Muktapida", "Kalhana (Historian)"],
                "image_url": "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "himachal-pradesh",
        "name": "Himachal Pradesh",
        "region": "Northern India",
        "capital": "Shimla",
        "short_description": "Devbhumi & Ancient Katoch Dynasty. Exam Highlights: Kangra Fort (Ancient Trigarta kingdom), Masrur Rock-Cut Monolithic Temple, Pahari Miniature Paintings, Dhami Movement (1939).",
        "image_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 500 BCE – 1800 AD",
                "title": "Trigarta Kingdom & Masrur Monoliths",
                "description": "Trigarta kingdom mentioned in Mahabharata ruled from Kangra Fort. Masrur Rock-Cut temple complex features 15 monolithic rock shrines.",
                "key_events": [
                    "Kangra Fort siege by Mahmud of Ghazni (1009 AD)",
                    "Carving of Masrur Rock-Cut Temples (8th Century)",
                    "Flowering of Kangra & Basholi Pahari miniature art"
                ],
                "key_rulers": ["Maharaja Sansar Chand Katoch"],
                "image_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "goa",
        "name": "Goa",
        "region": "Konkan Coast",
        "capital": "Panaji",
        "short_description": "Pearl of Konkan Coast & Portuguese India Capital. Exam Highlights: Kadamba Dynasty, Afonso de Albuquerque conquest (1510), Basilica of Bom Jesus, Operation Vijay (December 19, 1961 - Goa Liberation).",
        "image_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Colonial Era",
                "period": "1510 AD – 1961 AD",
                "title": "Portuguese Conquest & Operation Vijay Liberation",
                "description": "Afonso de Albuquerque captured Goa from Bijapur Sultanate in 1510. Indian Armed Forces launched Operation Vijay on Dec 18–19, 1961 liberating Goa.",
                "key_events": [
                    "Portuguese conquest of Old Goa (1510 AD)",
                    "Construction of Basilica of Bom Jesus (St. Francis Xavier shrine)",
                    "Operation Vijay Liberation of Goa (December 19, 1961 - Goa Liberation Day)"
                ],
                "key_rulers": ["Afonso de Albuquerque", "General K. P. Candeth"],
                "image_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "jharkhand",
        "name": "Jharkhand",
        "region": "Eastern India",
        "capital": "Ranchi",
        "short_description": "Land of Forests & Tribal Hero Revolts. Exam Highlights: Chota Nagpur Plateau, Birsa Munda 'Ulgulan' Uprising (1899-1900), Santhal Rebellion (1855 - Sidhu & Kanhu Murmu), Chuar Revolt.",
        "image_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Modern Era",
                "period": "1855 AD – 1900 AD",
                "title": "Santhal Hool Revolt & Bhagwan Birsa Munda Ulgulan",
                "description": "Sidhu and Kanhu led Santhal Hool in 1855 against British zamindars. Birsa Munda led Great Tumult ('Ulgulan') in 1899 against British forest agrarian laws.",
                "key_events": [
                    "Santhal Rebellion / Hool (1855) led by Sidhu, Kanhu, Chand, Bhairav",
                    "Birsa Munda Ulgulan uprising (1899–1900) & Birsait movement",
                    "Enactment of Chota Nagpur Tenancy Act (CNTA) 1908"
                ],
                "key_rulers": ["Bhagwan Birsa Munda", "Sidhu Murmu", "Kanhu Murmu"],
                "image_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "andhra-pradesh",
        "name": "Andhra Pradesh",
        "region": "Southern India",
        "capital": "Amaravati",
        "short_description": "Satavahana Empire & Buddhist Stupas. Exam Highlights: Satavahanas (Gautamiputra Satakarni, Simuka), Amaravati Buddhist Stupa & Art, Alluri Sitarama Raju (Rampa Rebellion 1922), Potti Sreeramulu.",
        "image_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "230 BCE – 220 AD",
                "title": "Satavahana Empire & Amaravati Art",
                "description": "Satavahanas ruled Andhra region with capitals at Dhanyakataka (Amaravati) & Pratishthana. Promoted Prakrit literature (Gatha Saptasati by Hala).",
                "key_events": [
                    "Gautamiputra Satakarni victory over Saka ruler Nahapana",
                    "Construction of Amaravati Mahachaitya Stupa",
                    "Compilation of Gatha Saptasati Prakrit poems by King Hala"
                ],
                "key_rulers": ["Simuka", "Gautamiputra Satakarni", "Yajna Sri Satakarni"],
                "image_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80"
            },
            {
                "id": 2,
                "era": "Modern Era",
                "period": "1922 AD – 1953 AD",
                "title": "Rampa Rebellion & Linguistic Statehood Movement",
                "description": "Alluri Sitarama Raju led Manyam Rampa Rebellion (1922–24) against British 1882 Madras Forest Act. Potti Sreeramulu's 58-day fast led to 1st linguistic state Andhra in 1953.",
                "key_events": [
                    "Rampa Tribal Rebellion (1922–24) under Alluri Sitarama Raju",
                    "Fast unto death by Potti Sreeramulu for Andhra State (1952)",
                    "Formation of 1st linguistic Andhra State (October 1, 1953)"
                ],
                "key_rulers": ["Alluri Sitarama Raju", "Potti Sreeramulu"],
                "image_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "uttarakhand",
        "name": "Uttarakhand",
        "region": "Northern India",
        "capital": "Dehradun",
        "short_description": "Land of Sacred Char Dham & Environmental Movements. Exam Highlights: Katyuri & Chand Dynasties, Garhwal Kingdom (Rani Karnavati - 'Nak-Kati-Rani'), Chipko Movement (1973 - Gaura Devi, Sunderlal Bahuguna).",
        "image_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Modern Era",
                "period": "1973 AD",
                "title": "Chipko Movement - Grassroots Eco-Satyagraha",
                "description": "Chipko Movement started in Mandal village, Chamoli district in 1973 where villagers led by Gaura Devi, Chandi Prasad Bhatt, and Sunderlal Bahuguna hugged trees to prevent commercial logging.",
                "key_events": [
                    "Tree-hugging protest at Chamoli district (1973)",
                    "Leadership of Gaura Devi and Mahila Mangal Dal",
                    "15-year green tree felling ban by Govt of India"
                ],
                "key_rulers": ["Sunderlal Bahuguna", "Gaura Devi", "Chandi Prasad Bhatt"],
                "image_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80"
            }
        ]
    },
    {
        "slug": "chhattisgarh",
        "name": "Chhattisgarh",
        "region": "Central India",
        "capital": "Raipur",
        "short_description": "Land of Ancient Temples & Bastar Tribal Uprisings. Exam Highlights: Kalachuri Dynasty of Ratanpur, Sirpur Lakshmana Brick Temple, Bhumkal Bastar Rebellion (1910 - Gunda Dhur).",
        "image_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Modern Era",
                "period": "1910 AD",
                "title": "Bhumkal Bastar Tribal Rebellion",
                "description": "Gunda Dhur led the Bhumkal tribal rebellion in Bastar in 1910 against British forestry reservation policies and exploitation.",
                "key_events": [
                    "Outbreak of Bhumkal Rebellion at Jagdalpur (1910)",
                    "Mango branches and chili signals used for mobilization",
                    "Leadership of tribal hero Gunda Dhur"
                ],
                "key_rulers": ["Gunda Dhur", "Pravir Chandra Bhanj Deo"],
                "image_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80"
            }
        ]
    }
]

def seed_database():
    try:
        print("[Seed] Connecting to MongoDB Atlas...")
        client = MongoClient(Config.MONGO_URI, serverSelectionTimeoutMS=5000)
        db = client[Config.DB_NAME]
        states_collection = db["states"]

        # Drop existing collection
        states_collection.drop()
        print("[Seed] Cleared existing 'states' collection.")

        # Create unique index on 'slug'
        states_collection.create_index("slug", unique=True)

        # Insert sample data
        result = states_collection.insert_many(SAMPLE_STATES_DATA)
        print(f"[Seed] Successfully seeded {len(result.inserted_ids)} exam-focused Indian states/UTs dataset into MongoDB Atlas!")

        client.close()
    except Exception as e:
        print(f"[Seed Error] Failed to seed database: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    seed_database()

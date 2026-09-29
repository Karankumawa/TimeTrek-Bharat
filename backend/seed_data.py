import sys
from pymongo import MongoClient
from config import Config

SAMPLE_STATES_DATA = [
    {
        "slug": "rajasthan",
        "name": "Rajasthan",
        "region": "North-West India",
        "capital": "Jaipur",
        "short_description": "Land of Kings, renowned for majestic hill forts, Thar Desert, royal Rajput heritage, and vibrant culture.",
        "image_url": "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 2500 BCE – 1500 BCE",
                "title": "Indus Valley & Kalibangan Civilization",
                "description": "Kalibangan in Hanumangarh district was a major provincial capital of the Indus Valley Civilization featuring ploughed field surfaces and advanced brick fortifications.",
                "key_events": [
                    "Early Harappan and Mature Harappan urban phases",
                    "Earliest known ploughed field in human history",
                    "Fire altars indicating ritual worship practices"
                ],
                "key_rulers": ["Indus Valley Clan Leaders"],
                "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "700 AD – 1192 AD",
                "title": "Rise of Rajput Dynasties & Prithviraj Chauhan",
                "description": "The Gurjara-Pratihara empire and later Chauhan, Guhila, and Rathore dynasties established fortified kingdoms across Mewar, Marwar, and Dhundhar.",
                "key_events": [
                    "Battle of Tarain (1191 AD & 1192 AD)",
                    "Construction of Chittorgarh Fort and Kumbhalgarh",
                    "Defense against early Arab incursions (Battle of Rajasthan)"
                ],
                "key_rulers": ["Prithviraj Chauhan", "Bappa Rawal", "Nagabhata I"],
                "image_url": "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 3,
                "era": "Medieval Era",
                "period": "1526 AD – 1707 AD",
                "title": "Mewar Resistance & Maharana Pratap",
                "description": "Maharana Pratap of Mewar valiantly defended Rajput sovereignty against Mughal Emperor Akbar, refusing to submit to foreign vassalage.",
                "key_events": [
                    "Battle of Haldighati (1576 AD)",
                    "Establishment of Jaipur city by Maharaja Sawai Jai Singh II (1727 AD)",
                    "Construction of Jantar Mantar astronomical observatory"
                ],
                "key_rulers": ["Maharana Pratap", "Rana Sanga", "Sawai Jai Singh II"],
                "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "gujarat",
        "name": "Gujarat",
        "region": "Western India",
        "capital": "Gandhinagar",
        "short_description": "Cradle of maritime trade, Harappan ports like Lothal, Solanki architecture, and birthplace of Mahatma Gandhi.",
        "image_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 2400 BCE – 1900 BCE",
                "title": "Lothal & Dholavira Maritime Hubs",
                "description": "Lothal featured the world's earliest known tidal dockyard, while Dholavira displayed sophisticated water harvesting reservoirs.",
                "key_events": [
                    "Construction of Lothal Dockyard for Persian Gulf maritime commerce",
                    "Dholavira signboard inscription and stone architecture",
                    "Bead-making and shell ornament industries"
                ],
                "key_rulers": ["Harappan Maritime Guilds"],
                "image_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "942 AD – 1244 AD",
                "title": "Solanki Dynasty Golden Age",
                "description": "The Solanki (Chaulukya) Dynasty fostered architectural marvels such as Rani ki Vav in Patan and Sun Temple in Modhera.",
                "key_events": [
                    "Construction of Rani ki Vav stepwell (UNESCO World Heritage Site)",
                    "Modhera Sun Temple architectural synthesis",
                    "Patronage of Jain scholars like Hemachandra"
                ],
                "key_rulers": ["Siddharaja Jayasimha", "Kumarapala", "Bhimdev I"],
                "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "delhi",
        "name": "Delhi",
        "region": "Northern India",
        "capital": "New Delhi",
        "short_description": "Historic imperial capital of seven cities, seat of Tomar Rajputs, Delhi Sultanate, Mughals, and modern India.",
        "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 1000 BCE – 1052 AD",
                "title": "Indraprastha & Tomar Rajput Foundation",
                "description": "Identified with mythological Indraprastha from Mahabharata; Anangpal Tomar founded Lal Kot in 1052 AD, establishing historic Delhi.",
                "key_events": [
                    "Construction of Lal Kot fortress and Iron Pillar relocation",
                    "Surajkund reservoir construction",
                    "Establishment of early urban habitation"
                ],
                "key_rulers": ["Anangpal Tomar II", "Prithviraj Chauhan"],
                "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1206 AD – 1526 AD",
                "title": "Delhi Sultanate Era",
                "description": "Five dynasties (Mamluk, Khalji, Tughlaq, Sayyid, Lodi) ruled from Delhi, erecting Qutub Minar and Tughlaqabad Fort.",
                "key_events": [
                    "Construction of Qutub Minar by Qutb-ud-din Aibak & Iltutmish",
                    "Reign of Razia Sultana (First female ruler of Delhi Sultanate)",
                    "Expansion under Alauddin Khalji"
                ],
                "key_rulers": ["Qutb-ud-din Aibak", "Razia Sultana", "Alauddin Khalji"],
                "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "maharashtra",
        "name": "Maharashtra",
        "region": "Western India",
        "capital": "Mumbai",
        "short_description": "Land of Maratha Empire, Chhatrapati Shivaji Maharaj, rock-cut Ajanta & Ellora caves, and economic power center.",
        "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "200 BCE – 800 AD",
                "title": "Satavahana Dynasty & Ajanta Caves",
                "description": "Satavahana, Rashtrakuta, and Vakataka rulers carved rock-cut sanctuaries at Ajanta, Ellora (Kailash Temple), and Elephanta.",
                "key_events": [
                    "Carving of Kailashnath Temple (Ellora Cave 16) out of a single rock monolith",
                    "Ajanta Cave Buddhist mural paintings",
                    "Trade with Roman Empire via Kalyan and Sopara ports"
                ],
                "key_rulers": ["Gautamiputra Satakarni", "Krishna I (Rashtrakuta)"],
                "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1674 AD – 1818 AD",
                "title": "Maratha Empire & Hindavi Swarajya",
                "description": "Chhatrapati Shivaji Maharaj founded the Maratha Empire, pioneering guerilla tactics (Ganimi Kava) and formidable hill forts.",
                "key_events": [
                    "Coronation of Chhatrapati Shivaji Maharaj at Raigad Fort (1674 AD)",
                    "Peshwa expansion across Northern India",
                    "Construction of Rajgad, Pratapgad, and Sindhudurg sea fort"
                ],
                "key_rulers": ["Chhatrapati Shivaji Maharaj", "Chhatrapati Sambhaji Maharaj", "Peshwa Baji Rao I"],
                "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "uttar-pradesh",
        "name": "Uttar Pradesh",
        "region": "Northern India",
        "capital": "Lucknow",
        "short_description": "Heartland of Gangetic civilization, birthland of Lord Rama and Krishna, Sarnath, and Mughal architectural masterworks.",
        "image_url": "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 1500 BCE – 500 BCE",
                "title": "Vedic Age & Sacred Cities",
                "description": "Varanasi (Kashi) evolved as the world's oldest living cultural city, while Sarnath hosted Gautam Buddha's first sermon.",
                "key_events": [
                    "Buddha's Dhamma Wheel sermon at Sarnath",
                    "Vedic literature compilation",
                    "Mathura sculpture school"
                ],
                "key_rulers": ["Mahajanapada Kings", "Emperor Ashoka"],
                "image_url": "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1526 AD – 1707 AD",
                "title": "Mughal Imperial Renaissance at Agra",
                "description": "Agra served as imperial capital under Akbar and Shah Jahan, featuring Taj Mahal, Agra Fort, and Fatehpur Sikri.",
                "key_events": [
                    "Construction of Taj Mahal",
                    "Creation of Fatehpur Sikri",
                    "Agra Fort expansion"
                ],
                "key_rulers": ["Akbar", "Shah Jahan"],
                "image_url": "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "tamil-nadu",
        "name": "Tamil Nadu",
        "region": "Southern India",
        "capital": "Chennai",
        "short_description": "Cradle of Dravidian temple architecture, Sangam Tamil literature, Chola naval power, and classical Bharatnatyam.",
        "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 300 BCE – 300 AD",
                "title": "Sangam Period & Chera Chola Pandya Triumvirate",
                "description": "Classical Tamil literature flourished during Sangam academies, alongside extensive maritime trade with Rome and Greece.",
                "key_events": [
                    "Sangam literary assemblies at Madurai",
                    "Port of Poompuhar trade",
                    "Kallanai Dam construction"
                ],
                "key_rulers": ["Karikala Chola", "Nedunjeliyan"],
                "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "850 AD – 1279 AD",
                "title": "Chola Imperial Golden Era",
                "description": "Rajaraja Chola I and Rajendra Chola I built naval armadas that expanded Chola influence to Southeast Asia and built Brihadeeswarar Temple.",
                "key_events": [
                    "Brihadeeswarar Great Living Chola Temple construction",
                    "Naval expedition to Southeast Asia",
                    "Bronze casting mastery"
                ],
                "key_rulers": ["Rajaraja Chola I", "Rajendra Chola I"],
                "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "goa",
        "name": "Goa",
        "region": "Konkan Coast",
        "capital": "Panaji",
        "short_description": "Pearl of the Orient, renowned for sun-kissed beaches, Portuguese colonial architecture, Basilica of Bom Jesus, and vibrant culture.",
        "image_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 300 BCE – 1312 AD",
                "title": "Kadamba Dynasty & Ancient Gomantak",
                "description": "Ruled by Bhojas, Satavahanas, and Kadambas of Goa who developed Chandor and Gopakapattana as thriving international ports.",
                "key_events": [
                    "Kadamba dynasty maritime expansion",
                    "Construction of Tambdi Surla Mahadev Temple",
                    "Arab & Persian trade interactions"
                ],
                "key_rulers": ["Jayakeshi I", "Shivachitta Permadideva"],
                "image_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Colonial Era",
                "period": "1510 AD – 1961 AD",
                "title": "Portuguese Era & Operation Vijay",
                "description": "Afonso de Albuquerque conquered Goa in 1510 AD. Goa served as the capital of Portuguese India for 451 years until liberation by Indian Armed Forces in 1961.",
                "key_events": [
                    "Portuguese Conquest of Goa (1510 AD)",
                    "Construction of Basilica of Bom Jesus (UNESCO World Heritage Site)",
                    "Operation Vijay Liberation of Goa (December 19, 1961)"
                ],
                "key_rulers": ["Afonso de Albuquerque", "General K. P. Candeth"],
                "image_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1599661046289-e31897703ca6?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 3,
                "era": "Modern Era",
                "period": "1987 AD – Present",
                "title": "North Goa & South Goa Districts",
                "description": "Goa attained full statehood on May 30, 1987, becoming India's 25th state with North Goa (HQ: Panaji) and South Goa (HQ: Margao).",
                "key_events": [
                    "Statehood of Goa (May 30, 1987)",
                    "Famous beaches: Baga, Calangute, Anjuna, Palolem, Colva",
                    "Transformation into world-renowned cultural tourism destination"
                ],
                "key_rulers": ["Dayanand Bandodkar", "Pratapsingh Rane"],
                "image_url": "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1609946850020-f571342d326e?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "punjab",
        "name": "Punjab",
        "region": "North-West India",
        "capital": "Chandigarh",
        "short_description": "Land of Five Rivers, Golden Temple, Sikh Empire of Maharaja Ranjit Singh, and granary of India.",
        "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 2600 BCE – 1500 BCE",
                "title": "Harappan Civilization & Sapta Sindhu",
                "description": "Ropar (Rupnagar) was a major Harappan urban center. Punjab formed the core of the ancient Vedic Sapta Sindhu.",
                "key_events": [
                    "Harappan Ropar urban excavations",
                    "Vedic Sapta Sindhu hymns composition",
                    "Taxila trade routes"
                ],
                "key_rulers": ["Ancient Vedic Clan Chiefs", "King Porus"],
                "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1799 AD – 1849 AD",
                "title": "Sikh Empire & Maharaja Ranjit Singh",
                "description": "Maharaja Ranjit Singh unified Misls into a sovereign empire stretching from Sutlej to Khyber Pass, gold-plating Sri Harmandir Sahib.",
                "key_events": [
                    "Unification of Sikh Misls",
                    "Gold-plating of Sri Harmandir Sahib (Golden Temple)",
                    "Secular rule & military modernization"
                ],
                "key_rulers": ["Maharaja Ranjit Singh", "Hari Singh Nalwa"],
                "image_url": "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1514222709107-a180c68d72b4?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "karnataka",
        "name": "Karnataka",
        "region": "Southern India",
        "capital": "Bengaluru",
        "short_description": "Cradle of stone architecture, Vijayanagara Empire at Hampi, Mysore Palace, and India's tech innovation hub.",
        "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "345 AD – 1000 AD",
                "title": "Kadambas, Badami Chalukyas & Rashtrakutas",
                "description": "Badami Chalukyas built cave temples at Badami and stone structural temples at Pattadakal and Aihole.",
                "key_events": [
                    "Carving of Badami Cave Temples",
                    "Pattadakal UNESCO World Heritage temples",
                    "Aihole temple experimentation"
                ],
                "key_rulers": ["Pulakeshin II", "Mayurasharma"],
                "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1336 AD – 1565 AD",
                "title": "Vijayanagara Empire Golden Age at Hampi",
                "description": "Harihara and Bukka founded Vijayanagara at Hampi on the banks of Tungabhadra, becoming a world metropolis.",
                "key_events": [
                    "Construction of Stone Chariot & Vittala Temple",
                    "Foreign trade in gems and horses",
                    "Patronage of Carnatic music and literature"
                ],
                "key_rulers": ["Krishnadevaraya", "Harihara I"],
                "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1600100397608-f010e423b971?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "west-bengal",
        "name": "West Bengal",
        "region": "Eastern India",
        "capital": "Kolkata",
        "short_description": "Cultural capital of India, Bengal Renaissance, Rabindranath Tagore, Victoria Memorial, and Sundarbans.",
        "image_url": "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 300 BCE – 1150 AD",
                "title": "Gangaridai, Maurya & Pala Dynasty",
                "description": "Gangaridai empire resisted Alexander the Great. Pala Empire made Bengal a global center for Mahayana Buddhist learning.",
                "key_events": [
                    "Pala Buddhist monastic art",
                    "Somapura Mahavihara patronage",
                    "Maritime commerce via Tamralipta port"
                ],
                "key_rulers": ["Gopala I", "Dharmapala"],
                "image_url": "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Colonial Era",
                "period": "1757 AD – 1947 AD",
                "title": "Bengal Renaissance & Freedom Movement",
                "description": "Battle of Plassey (1757) marked British East India Company rule. Bengal became the crucible of intellectual Renaissance and freedom movement.",
                "key_events": [
                    "Battle of Plassey (1757 AD)",
                    "Bengal Renaissance (Tagore, Vivekananda)",
                    "Subhas Chandra Bose & Azad Hind Fauj"
                ],
                "key_rulers": ["Nawab Siraj-ud-Daulah", "Rabindranath Tagore", "Netaji Subhas Chandra Bose"],
                "image_url": "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "kerala",
        "name": "Kerala",
        "region": "Southern India",
        "capital": "Thiruvananthapuram",
        "short_description": "God's Own Country, birthplace of Chera Dynasty, ancient maritime spice trade with Rome, Padmanabhaswamy Temple, and Kalarippayattu.",
        "image_url": "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 300 BCE – 1102 AD",
                "title": "Chera Empire & Muziris Global Spice Port",
                "description": "Muziris served as the world's chief spice trading hub connecting India with Roman, Greek, and Arab empires.",
                "key_events": [
                    "Muziris port maritime pepper trade with Roman Empire",
                    "Propagation of Kalarippayattu martial arts",
                    "Establishment of ancient coastal Jewish and Christian communities"
                ],
                "key_rulers": ["Uthiyan Cheralathan", "Kulasekhara Varman"],
                "image_url": "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Colonial Era",
                "period": "1729 AD – 1949 AD",
                "title": "Travancore Kingdom & Battle of Colachel",
                "description": "Marthanda Varma defeated the Dutch East India Company at Colachel (1741), marking the first Asian naval defeat of a European power.",
                "key_events": [
                    "Battle of Colachel (1741 AD)",
                    "Dedication of Travancore to Lord Padmanabha",
                    "Social reforms of Sri Narayana Guru"
                ],
                "key_rulers": ["Anizham Thirunal Marthanda Varma", "Swathi Thirunal"],
                "image_url": "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "madhya-pradesh",
        "name": "Madhya Pradesh",
        "region": "Central India",
        "capital": "Bhopal",
        "short_description": "Heart of India, home to UNESCO World Heritage Sanchi Stupa, Khajuraho erotic stone temples, Bhimbetka rock shelters, and Gwalior Fort.",
        "image_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 300 BCE – 100 BCE",
                "title": "Maurya & Shunga Era at Sanchi Stupa",
                "description": "Emperor Ashoka commissioned the Great Stupa at Sanchi, creating a masterpiece of Buddhist architecture and stone carving.",
                "key_events": [
                    "Construction of Great Stupa 1 by Emperor Ashoka",
                    "Shunga dynasty stone gateways (Toranas) creation",
                    "Bhimbetka rock shelters prehistoric cave art"
                ],
                "key_rulers": ["Emperor Ashoka", "Pushyamitra Shunga"],
                "image_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "950 AD – 1050 AD",
                "title": "Chandela Dynasty & Khajuraho Temples",
                "description": "Chandela Rajput kings built 85 intricate stone temples at Khajuraho celebrating architectural harmony and spiritual art.",
                "key_events": [
                    "Construction of Kandariya Mahadeva Temple",
                    "Gwalior Fort defense under Tomar Rajputs",
                    "Rani Durgavati's resistance against Akbar"
                ],
                "key_rulers": ["Yashovarman", "Dhanga", "Rani Durgavati"],
                "image_url": "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1627894483216-2138af692e32?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "bihar",
        "name": "Bihar",
        "region": "Eastern India",
        "capital": "Patna",
        "short_description": "Birthplace of Buddhism and Jainism, imperial seat of Maurya & Gupta Empires, Mahavira, Lord Buddha, and ancient Nalanda University.",
        "image_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 544 BCE – 232 BCE",
                "title": "Magadha Empire & Emperor Ashoka",
                "description": "Pataliputra served as imperial capital of Magadha, Nanda, and Maurya empires under Chandragupta and Ashoka the Great.",
                "key_events": [
                    "Enlightenment of Gautam Buddha at Bodh Gaya",
                    "Establishment of Maurya Empire under Chandragupta & Chanakya",
                    "Ashokan rock and pillar edicts"
                ],
                "key_rulers": ["Bimbisara", "Chandragupta Maurya", "Emperor Ashoka"],
                "image_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Ancient Era",
                "period": "240 AD – 550 AD",
                "title": "Nalanda University Golden Era",
                "description": "Nalanda University flourished as a global residential seat of higher learning attracting scholars like Xuanzang.",
                "key_events": [
                    "Founding of Nalanda Mahavihara by Kumaragupta I",
                    "Gupta Empire Golden Age of science, astronomy, and mathematics",
                    "Aryabhata's mathematical treatises"
                ],
                "key_rulers": ["Chandragupta II (Vikramaditya)", "Kumaragupta I"],
                "image_url": "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1622278647429-71bc97e904e8?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "odisha",
        "name": "Odisha",
        "region": "Eastern India",
        "capital": "Bhubaneswar",
        "short_description": "Land of Kalinga, magnificent Konark Sun Temple chariot architecture, Jagannath Temple at Puri, and ancient seafaring Sadhabas.",
        "image_url": "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 261 BCE",
                "title": "Kalinga War & Ashoka's Transformation",
                "description": "The Kalinga War fought on the banks of Daya river led Emperor Ashoka to renounce warfare and adopt Dhamma (Buddhism).",
                "key_events": [
                    "Kalinga War on Daya river",
                    "Inscription of Dhauli Ashokan Edicts",
                    "Maritime Sadhabas Bali Yatra sea voyages to Java & Bali"
                ],
                "key_rulers": ["Kalinga Generals", "Emperor Ashoka"],
                "image_url": "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1035 AD – 1250 AD",
                "title": "Eastern Ganga Dynasty & Konark Sun Temple",
                "description": "Narasimhadeva I built the Konark Sun Temple as a giant stone chariot with 24 carved wheels pulled by 7 horses.",
                "key_events": [
                    "Konark Sun Temple UNESCO site construction",
                    "Jagannath Puri Temple grand chariot procession",
                    "Mastery of Kalinga architectural style"
                ],
                "key_rulers": ["Anantavarman Chodaganga", "Narasimhadeva I"],
                "image_url": "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1606298855672-3efb63017be8?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "assam",
        "name": "Assam",
        "region": "North-East India",
        "capital": "Dispur",
        "short_description": "Land of the mighty Brahmaputra, 600-year invincible Ahom Kingdom, Lachit Borphukan, Kamakhya Temple, and world-renowned tea estates.",
        "image_url": "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 350 AD – 1140 AD",
                "title": "Kamarupa Kingdom & Kamakhya Shrine",
                "description": "Kamarupa kings established a powerful Eastern kingdom celebrated in ancient epics and visited by pilgrim Xuanzang.",
                "key_events": [
                    "Reign of Bhaskaravarman of Kamarupa",
                    "Patronage of ancient Kamakhya Tantric shrine",
                    "Pragjyotishpura astronomical heritage"
                ],
                "key_rulers": ["Pushyavarman", "Bhaskaravarman"],
                "image_url": "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80"
                ]
            },
            {
                "id": 2,
                "era": "Medieval Era",
                "period": "1228 AD – 1826 AD",
                "title": "Ahom Dynasty & Battle of Saraighat",
                "description": "Chaolung Sukaphaa founded the Ahom Kingdom. General Lachit Borphukan famously defeated Mughal forces in the naval Battle of Saraighat (1671).",
                "key_events": [
                    "Battle of Saraighat (1671 AD)",
                    "Construction of Rang Ghar amphitheater at Sivasagar",
                    "600-year unbroken Ahom sovereignty"
                ],
                "key_rulers": ["Sukaphaa", "Lachit Borphukan", "Rudra Singha"],
                "image_url": "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1596895111956-bf1cf0599ce5?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "jammu-and-kashmir",
        "name": "Jammu & Kashmir",
        "region": "Northern India",
        "capital": "Srinagar / Jammu",
        "short_description": "Paradise on Earth, ancient Martand Sun Temple, Karkota dynasty empire of Lalitaditya, Silk Route trade, and Dal Lake.",
        "image_url": "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "625 AD – 855 AD",
                "title": "Karkota Dynasty & Emperor Lalitaditya",
                "description": "Lalitaditya Muktapida constructed Martand Sun Temple and expanded a trans-Himalayan empire spanning Central Asia.",
                "key_events": [
                    "Construction of Martand Sun Temple",
                    "Kashmir Shaivism philosophy compilation",
                    "Silk Route mountain trade passes control"
                ],
                "key_rulers": ["Lalitaditya Muktapida", "Durlabhavardhana"],
                "image_url": "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1566837945700-30057527ade0?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "telangana",
        "name": "Telangana",
        "region": "Southern India",
        "capital": "Hyderabad",
        "short_description": "Land of Kakatiya Dynasty fortresses, Thousand Pillar Temple, Warangal Gateways, Charminar, and Koh-i-Noor diamond heritage.",
        "image_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Medieval Era",
                "period": "1163 AD – 1323 AD",
                "title": "Kakatiya Dynasty & Queen Rudrama Devi",
                "description": "The Kakatiyas built Warangal Fort, Ramappa Temple (UNESCO World Heritage Site), and advanced irrigation chain tanks.",
                "key_events": [
                    "Construction of Ramappa Temple with floating bricks",
                    "Warangal Fort Keerthi Toranas gateways",
                    "Reign of warrior queen Rani Rudrama Devi"
                ],
                "key_rulers": ["Rani Rudrama Devi", "Prataparudra", "Ganapati Deva"],
                "image_url": "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1605379399642-870262d3d051?auto=format&fit=crop&w=800&q=80"
                ]
            }
        ]
    },
    {
        "slug": "himachal-pradesh",
        "name": "Himachal Pradesh",
        "region": "Northern India",
        "capital": "Shimla",
        "short_description": "Devbhumi (Abode of Gods), ancient Trigarta Kingdom, rock-cut Masrur temples, Himalayan fortresses, and Kangra miniature art.",
        "image_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80",
        "banner_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=1200&q=80",
        "timeline": [
            {
                "id": 1,
                "era": "Ancient Era",
                "period": "c. 500 BCE – 1800 AD",
                "title": "Trigarta Kingdom & Kangra Miniature Art",
                "description": "Katoch Dynasty ruled Kangra Fort (one of India's oldest), patronizing sublime Kangra miniature school of painting.",
                "key_events": [
                    "Defense of Kangra Fort against ancient invasions",
                    "Carving of Masrur Rock Cut Monolithic Temples",
                    "Kangra Valley miniature painting tradition"
                ],
                "key_rulers": ["Maharaja Sansar Chand Katoch", "Susharma Chand"],
                "image_url": "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80",
                "images": [
                    "https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&w=800&q=80"
                ]
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
        print(f"[Seed] Successfully seeded {len(result.inserted_ids)} Indian states into MongoDB Atlas!")

        client.close()
    except Exception as e:
        print(f"[Seed Error] Failed to seed database: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    seed_database()

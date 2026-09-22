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
        "image_url": "https://images.unsplash.com/photo-1599661046289-e31897703ca6?q=80&w=800&auto=format&fit=crop",
        "banner_url": "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?q=80&w=1200&auto=format&fit=crop",
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
                "image_url": "https://images.unsplash.com/photo-1600100397608-f010e423b971?q=80&w=800&auto=format&fit=crop"
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
                "image_url": "https://images.unsplash.com/photo-1599661046289-e31897703ca6?q=80&w=800&auto=format&fit=crop"
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
                "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?q=80&w=800&auto=format&fit=crop"
            },
            {
                "id": 4,
                "era": "Modern Era",
                "period": "1949 AD – Present",
                "title": "Formation of Greater Rajasthan",
                "description": "Unification of 19 princely states and 3 chiefships into the state of Rajasthan following Indian Independence.",
                "key_events": [
                    "Integration of Jaipur, Jodhpur, Bikaner, and Jaisalmer on March 30, 1949",
                    "Designation of UNESCO World Heritage Hill Forts",
                    "Transformation into India's top cultural tourism hub"
                ],
                "key_rulers": ["Sardar Vallabhbhai Patel (Integrator)", "Maharaja Man Singh II"],
                "image_url": "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?q=80&w=800&auto=format&fit=crop"
            }
        ]
    },
    {
        "slug": "gujarat",
        "name": "Gujarat",
        "region": "Western India",
        "capital": "Gandhinagar",
        "short_description": "Cradle of maritime trade, Harappan ports like Lothal, Solanki architecture, and birthplace of Mahatma Gandhi.",
        "image_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?q=80&w=800&auto=format&fit=crop",
        "banner_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?q=80&w=1200&auto=format&fit=crop",
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
                "image_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?q=80&w=800&auto=format&fit=crop"
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
                "image_url": "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?q=80&w=800&auto=format&fit=crop"
            },
            {
                "id": 3,
                "era": "Modern Era",
                "period": "1915 AD – 1947 AD",
                "title": "Indian Freedom Struggle & Sabarmati Ashram",
                "description": "Mahatma Gandhi established Sabarmati Ashram in Ahmedabad and led pivotal non-violent movements including the Dandi Salt March.",
                "key_events": [
                    "Kheda Satyagraha (1918)",
                    "Dandi Salt March (1930)",
                    "Bardoli Satyagraha led by Sardar Vallabhbhai Patel (1928)"
                ],
                "key_rulers": ["Mahatma Gandhi", "Sardar Vallabhbhai Patel"],
                "image_url": "https://images.unsplash.com/photo-1609946850020-f571342d326e?q=80&w=800&auto=format&fit=crop"
            }
        ]
    },
    {
        "slug": "delhi",
        "name": "Delhi",
        "region": "Northern India",
        "capital": "New Delhi",
        "short_description": "Historic imperial capital of seven cities, seat of Tomar Rajputs, Delhi Sultanate, Mughals, and modern India.",
        "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=800&auto=format&fit=crop",
        "banner_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=1200&auto=format&fit=crop",
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
                "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=800&auto=format&fit=crop"
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
                "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=800&auto=format&fit=crop"
            },
            {
                "id": 3,
                "era": "Medieval Era",
                "period": "1638 AD – 1857 AD",
                "title": "Shahjahanabad & Mughal Capital",
                "description": "Emperor Shah Jahan built Shahjahanabad (Old Delhi), Red Fort, and Jama Masjid, making Delhi the jewel of Mughal art.",
                "key_events": [
                    "Inauguration of Red Fort and Chandni Chowk (1648 AD)",
                    "Construction of Jama Masjid",
                    "1857 First War of Independence and siege of Delhi"
                ],
                "key_rulers": ["Shah Jahan", "Aurangzeb", "Bahadur Shah Zafar"],
                "image_url": "https://images.unsplash.com/photo-1587474260584-136574528ed5?q=80&w=800&auto=format&fit=crop"
            }
        ]
    },
    {
        "slug": "maharashtra",
        "name": "Maharashtra",
        "region": "Western India",
        "capital": "Mumbai",
        "short_description": "Land of Maratha Empire, Chhatrapati Shivaji Maharaj, rock-cut Ajanta & Ellora caves, and economic power center.",
        "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=800&auto=format&fit=crop",
        "banner_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=1200&auto=format&fit=crop",
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
                "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=800&auto=format&fit=crop"
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
                "image_url": "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?q=80&w=800&auto=format&fit=crop"
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

        # Drop existing collection to ensure fresh clean state schema
        states_collection.drop()
        print("[Seed] Cleared existing 'states' collection.")

        # Create unique index on 'slug'
        states_collection.create_index("slug", unique=True)

        # Insert sample data
        result = states_collection.insert_many(SAMPLE_STATES_DATA)
        print(f"[Seed] Successfully seeded {len(result.inserted_ids)} Indian states into MongoDB!")

        client.close()
    except Exception as e:
        print(f"[Seed Error] Failed to seed database: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    seed_database()

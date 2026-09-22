import sys
from pymongo import MongoClient
from pymongo.errors import ConnectionFailure, ServerSelectionTimeoutError
from config import Config

class Database:
    _client = None
    _db = None

    @classmethod
    def get_db(cls):
        if cls._db is None:
            try:
                cls._client = MongoClient(
                    Config.MONGO_URI,
                    serverSelectionTimeoutMS=5000
                )
                # Verify connection with ping
                cls._client.admin.command('ping')
                cls._db = cls._client[Config.DB_NAME]
                print(f"[DB] Successfully connected to MongoDB database: '{Config.DB_NAME}'")
            except (ConnectionFailure, ServerSelectionTimeoutError) as e:
                print(f"[DB Error] Failed to connect to MongoDB Atlas: {e}", file=sys.stderr)
                raise e
        return cls._db

    @classmethod
    def close(cls):
        if cls._client:
            cls._client.close()
            cls._client = None
            cls._db = None
            print("[DB] MongoDB connection closed.")

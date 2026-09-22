import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

class Config:
    PORT = int(os.environ.get("PORT", 5000))
    MONGO_URI = os.environ.get("MONGO_URI", "mongodb://localhost:27017/timetrek_bharat")
    DB_NAME = os.environ.get("DB_NAME", "timetrek_bharat")
    DEBUG = os.environ.get("FLASK_ENV", "development") == "development"

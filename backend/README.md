# TimeTrek Bharat - Backend REST API (Flask & MongoDB Atlas)

A Python Flask REST API connected to MongoDB Atlas, providing historical timeline data for Indian states (Rajasthan, Gujarat, Delhi, Maharashtra, etc.) for the TimeTrek Bharat Android Application.

---

## 📁 Directory Structure
```
backend/
├── app.py           # Main Flask REST API application
├── config.py        # Environment & application configuration settings
├── db.py            # MongoDB Atlas connection manager (PyMongo)
├── seed_data.py     # Database seeding script for historical state timelines
├── requirements.txt # Python dependency specification
├── Procfile         # Render deployment WSGI command
├── render.yaml      # Render Blueprint deployment definition
└── .env.example     # Environment variables template file
```

---

## 🗄️ JSON Schema Definition

### 1. State Document Schema (`states` collection)
```json
{
  "_id": "ObjectId",
  "slug": "string (unique ID, e.g. 'rajasthan')",
  "name": "string (e.g. 'Rajasthan')",
  "region": "string (e.g. 'North-West India')",
  "capital": "string (e.g. 'Jaipur')",
  "short_description": "string",
  "image_url": "string (HTTPS Image URL for grid view)",
  "banner_url": "string (HTTPS Header Image URL for detail view)",
  "timeline": [
    {
      "id": "integer",
      "era": "string ('Ancient Era' | 'Medieval Era' | 'Colonial Era' | 'Modern Era')",
      "period": "string (e.g. 'c. 2500 BCE – 1500 BCE')",
      "title": "string (Historical Event / Dynasty title)",
      "description": "string (Detailed narrative description)",
      "key_events": ["string"],
      "key_rulers": ["string"],
      "image_url": "string (HTTPS Image URL)"
    }
  ]
}
```

---

## 🚀 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/health` | Service & Database diagnostic health check |
| `GET` | `/api/states` | List all states with summary metadata (Supports `?search=query`) |
| `GET` | `/api/states/<slug_or_id>` | Get state details and full chronological history timeline |
| `GET` | `/api/states/<slug_or_id>/timeline` | Get specific state timeline events (Supports `?era=Medieval Era`) |

---

## 🛠️ Setup Instructions

### 1. Prerequisites
- Python 3.9+
- MongoDB Atlas Account & Cluster URI

### 2. Local Setup
```bash
# Navigate to backend directory
cd backend

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Copy environment template
cp .env.example .env

# Edit .env and replace MONGO_URI with your MongoDB Atlas Connection String
```

### 3. Database Seeding
```bash
# Run seed script to populate MongoDB Atlas with initial historical data
python seed_data.py
```

### 4. Running Local Server
```bash
python app.py
```
Server starts on `http://localhost:5000`.

---

## ☁️ Deployment on Render

1. Push the code repository to **GitHub** or **GitLab**.
2. Log into [Render Dashboard](https://dashboard.render.com).
3. Click **New +** -> **Web Service**.
4. Connect your repository and select the `backend` directory (Root Directory: `backend`).
5. Configure Environment:
   - **Environment**: Python
   - **Build Command**: `pip install -r requirements.txt`
   - **Start Command**: `gunicorn app:app`
6. Add Environment Variable:
   - `MONGO_URI`: `mongodb+srv://<username>:<password>@cluster0.xxx.mongodb.net/timetrek_bharat?retryWrites=true&w=majority`
   - `DB_NAME`: `timetrek_bharat`
7. Click **Create Web Service**. Your API will be deployed and available at `https://<your-service-name>.onrender.com`.

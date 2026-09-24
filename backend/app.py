from flask import Flask, jsonify, request
from flask_cors import CORS
from bson.objectid import ObjectId
import re
import os

from config import Config
from db import Database

app = Flask(__name__)
# Enable CORS for cross-origin mobile and web requests
CORS(app)

def serialize_doc(doc):
    """
    Converts MongoDB BSON document into JSON serializable dict.
    Normalizes custom/flexible schemas (e.g. 'state_name' vs 'name', missing 'slug', 'districts')
    so that the Android app and web API always receive complete, valid State objects.
    """
    if not doc:
        return None

    # Handle ObjectId
    if "_id" in doc:
        doc["id"] = str(doc["_id"])
        del doc["_id"]

    # 1. Normalize State Name
    if not doc.get("name"):
        if doc.get("state_name"):
            doc["name"] = doc.get("state_name")
        elif doc.get("title"):
            doc["name"] = doc.get("title")
        else:
            doc["name"] = "Unknown State"

    # 2. Normalize Slug
    if not doc.get("slug"):
        clean_name = re.sub(r'[^a-zA-Z0-9\s-]', '', str(doc.get("name", ""))).strip().lower()
        doc["slug"] = clean_name.replace(' ', '-') if clean_name else f"state-{doc.get('id', 'item')}"

    # 3. Normalize Region
    if not doc.get("region"):
        doc["region"] = "India"

    # 4. Normalize Capital
    if not doc.get("capital"):
        # Check if districts exist with headquarters
        districts = doc.get("districts", [])
        if isinstance(districts, list) and len(districts) > 0 and isinstance(districts[0], dict):
            hq = districts[0].get("headquarters")
            if hq:
                doc["capital"] = hq
            else:
                doc["capital"] = "N/A"
        else:
            doc["capital"] = "N/A"

    # 5. Normalize Short Description
    if not doc.get("short_description"):
        districts = doc.get("districts", [])
        if isinstance(districts, list) and len(districts) > 0:
            district_names = [d.get("name") for d in districts if isinstance(d, dict) and d.get("name")]
            dist_str = ", ".join(district_names) if district_names else f"{len(districts)} districts"
            doc["short_description"] = f"Famous coastal & historic state in {doc.get('region')} region comprising {dist_str}."
        else:
            doc["short_description"] = f"Explore the rich heritage, culture, and historic timelines of {doc.get('name')}."

    # 6. Normalize Image & Banner URLs
    if not doc.get("image_url"):
        # Default scenic coastal/heritage image
        doc["image_url"] = "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?q=80&w=800&auto=format&fit=crop"
    if not doc.get("banner_url"):
        doc["banner_url"] = doc.get("image_url")

    # 7. Normalize Timeline from Districts or Default
    if not doc.get("timeline") or not isinstance(doc.get("timeline"), list):
        timeline_entries = []
        districts = doc.get("districts", [])
        if isinstance(districts, list) and len(districts) > 0:
            for idx, d in enumerate(districts, 1):
                if isinstance(d, dict):
                    beaches = ", ".join(d.get("key_beaches", [])) if d.get("key_beaches") else "Historic landmarks"
                    timeline_entries.append({
                        "id": idx,
                        "era": "Modern Era",
                        "period": f"District: {d.get('name', 'Region')}",
                        "title": f"{d.get('name')} District & {d.get('headquarters')} Headquarters",
                        "description": f"Population: {d.get('population', 'N/A'):,}, Area: {d.get('area_sq_km', 'N/A')} sq km. Key beaches/attractions: {beaches}.",
                        "key_events": [f"Headquarters at {d.get('headquarters')}", f"Talukas count: {d.get('talukas_count')}", f"Coastal Region: {d.get('is_coastal')}"],
                        "key_rulers": ["State Administration", "Local Authorities"],
                        "image_url": doc.get("image_url")
                    })
        if not timeline_entries:
            timeline_entries.append({
                "id": 1,
                "era": "Ancient & Modern Era",
                "period": "Historic Period",
                "title": f"History & Culture of {doc.get('name')}",
                "description": doc.get("short_description"),
                "key_events": [f"Capital at {doc.get('capital')}", f"Region: {doc.get('region')}"],
                "key_rulers": ["Regional Dynasties"],
                "image_url": doc.get("image_url")
            })
        doc["timeline"] = timeline_entries

    return doc

@app.route("/api/health", methods=["GET"])
def health_check():
    """Health check endpoint for Render deployment diagnostics."""
    try:
        db = Database.get_db()
        db.command('ping')
        return jsonify({
            "status": "healthy",
            "database": "connected",
            "service": "TimeTrek Bharat REST API"
        }), 200
    except Exception as e:
        return jsonify({
            "status": "degraded",
            "database": "disconnected",
            "error": str(e)
        }), 500

@app.route("/api/states", methods=["GET"])
def get_all_states():
    """
    GET /api/states
    Retrieves summary list of all Indian states available in the historical database.
    Optional search query parameter: ?search=goa
    """
    try:
        db = Database.get_db()
        states_collection = db["states"]

        search_query = request.args.get("search", "").strip()
        filter_criteria = {}
        if search_query:
            filter_criteria["$or"] = [
                {"name": {"$regex": search_query, "$options": "i"}},
                {"state_name": {"$regex": search_query, "$options": "i"}},
                {"region": {"$regex": search_query, "$options": "i"}},
                {"short_description": {"$regex": search_query, "$options": "i"}}
            ]

        # Fetch without restrictive projection so all fields are available
        states_cursor = states_collection.find(filter_criteria)
        states = [serialize_doc(s) for s in states_cursor]

        return jsonify({
            "success": True,
            "count": len(states),
            "data": states
        }), 200
    except Exception as e:
        return jsonify({
            "success": False,
            "error": "Failed to fetch states list",
            "details": str(e)
        }), 500

@app.route("/api/states/<state_identifier>", methods=["GET"])
def get_state_detail(state_identifier):
    """
    GET /api/states/<state_identifier>
    Retrieves complete details for a state, including its historical timeline entries.
    state_identifier can be state 'slug' (e.g. 'goa'), 'name' or Mongo '_id'.
    """
    try:
        db = Database.get_db()
        states_collection = db["states"]

        identifier_lower = state_identifier.lower()
        query_conditions = [
            {"slug": identifier_lower},
            {"name": {"$regex": f"^{re.escape(state_identifier)}$", "$options": "i"}},
            {"state_name": {"$regex": f"^{re.escape(state_identifier)}$", "$options": "i"}}
        ]
        if ObjectId.is_valid(state_identifier):
            query_conditions.append({"_id": ObjectId(state_identifier)})

        state_doc = states_collection.find_one({"$or": query_conditions})

        if not state_doc:
            # Fallback search across all documents
            for doc in states_collection.find():
                serialized = serialize_doc(doc)
                if serialized.get("slug") == identifier_lower or serialized.get("name", "").lower() == identifier_lower or serialized.get("id") == state_identifier:
                    return jsonify({
                        "success": True,
                        "data": serialized
                    }), 200

            return jsonify({
                "success": False,
                "error": f"State '{state_identifier}' not found."
            }), 404

        return jsonify({
            "success": True,
            "data": serialize_doc(state_doc)
        }), 200

    except Exception as e:
        return jsonify({
            "success": False,
            "error": "Failed to fetch state details",
            "details": str(e)
        }), 500

@app.route("/api/states/<state_identifier>/timeline", methods=["GET"])
def get_state_timeline(state_identifier):
    """
    GET /api/states/<state_identifier>/timeline
    Retrieves chronological historical timeline events for a requested state.
    """
    try:
        db = Database.get_db()
        states_collection = db["states"]

        identifier_lower = state_identifier.lower()
        query_conditions = [
            {"slug": identifier_lower},
            {"name": {"$regex": f"^{re.escape(state_identifier)}$", "$options": "i"}},
            {"state_name": {"$regex": f"^{re.escape(state_identifier)}$", "$options": "i"}}
        ]
        if ObjectId.is_valid(state_identifier):
            query_conditions.append({"_id": ObjectId(state_identifier)})

        state_doc = states_collection.find_one({"$or": query_conditions})
        if not state_doc:
            return jsonify({
                "success": False,
                "error": f"State '{state_identifier}' not found."
            }), 404

        serialized = serialize_doc(state_doc)
        timeline = serialized.get("timeline", [])

        era_filter = request.args.get("era", "").strip()
        if era_filter:
            timeline = [t for t in timeline if t.get("era", "").lower() == era_filter.lower()]

        return jsonify({
            "success": True,
            "state_name": serialized.get("name"),
            "slug": serialized.get("slug"),
            "count": len(timeline),
            "timeline": timeline
        }), 200

    except Exception as e:
        return jsonify({
            "success": False,
            "error": "Failed to fetch state timeline",
            "details": str(e)
        }), 500

@app.errorhandler(404)
def not_found(e):
    return jsonify({"success": False, "error": "Endpoint not found"}), 404

@app.errorhandler(500)
def server_error(e):
    return jsonify({"success": False, "error": "Internal server error"}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=Config.PORT, debug=Config.DEBUG)

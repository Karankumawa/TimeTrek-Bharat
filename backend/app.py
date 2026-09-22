from flask import Flask, jsonify, request
from flask_cors import CORS
from bson.objectid import ObjectId
import os

from config import Config
from db import Database

app = Flask(__name__)
# Enable CORS for cross-origin mobile and web requests
CORS(app)

def serialize_doc(doc):
    """
    Converts MongoDB BSON document into JSON serializable dict.
    Converts ObjectId to string if present.
    """
    if not doc:
        return None
    if "_id" in doc:
        doc["id"] = str(doc["_id"])
        del doc["_id"]
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
    Optional search query parameter: ?search=rajasthan
    """
    try:
        db = Database.get_db()
        states_collection = db["states"]

        search_query = request.args.get("search", "").strip()
        filter_criteria = {}
        if search_query:
            filter_criteria["$or"] = [
                {"name": {"$regex": search_query, "$options": "i"}},
                {"region": {"$regex": search_query, "$options": "i"}},
                {"short_description": {"$regex": search_query, "$options": "i"}}
            ]

        # Projection for grid list view
        projection = {
            "slug": 1,
            "name": 1,
            "region": 1,
            "capital": 1,
            "short_description": 1,
            "image_url": 1,
            "banner_url": 1,
            "timeline_length": {"$size": {"$ifNull": ["$timeline", []]}}
        }

        states_cursor = states_collection.find(filter_criteria, projection)
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
    state_identifier can be state 'slug' (e.g. 'rajasthan') or Mongo '_id'.
    """
    try:
        db = Database.get_db()
        states_collection = db["states"]

        query = {}
        if ObjectId.is_valid(state_identifier):
            query = {"$or": [{"_id": ObjectId(state_identifier)}, {"slug": state_identifier.lower()}]}
        else:
            query = {"slug": state_identifier.lower()}

        state_doc = states_collection.find_one(query)

        if not state_doc:
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
    Optional query parameter: ?era=Medieval Era
    """
    try:
        db = Database.get_db()
        states_collection = db["states"]

        query = {}
        if ObjectId.is_valid(state_identifier):
            query = {"$or": [{"_id": ObjectId(state_identifier)}, {"slug": state_identifier.lower()}]}
        else:
            query = {"slug": state_identifier.lower()}

        state_doc = states_collection.find_one(query, {"name": 1, "slug": 1, "timeline": 1})

        if not state_doc:
            return jsonify({
                "success": False,
                "error": f"State '{state_identifier}' not found."
            }), 404

        timeline = state_doc.get("timeline", [])

        era_filter = request.args.get("era", "").strip()
        if era_filter:
            timeline = [t for t in timeline if t.get("era", "").lower() == era_filter.lower()]

        return jsonify({
            "success": True,
            "state_name": state_doc.get("name"),
            "slug": state_doc.get("slug"),
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

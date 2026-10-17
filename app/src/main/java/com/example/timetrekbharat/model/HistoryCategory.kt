package com.example.timetrekbharat.model

import com.google.gson.annotations.SerializedName

enum class HistoryCategory(val id: String, val title: String, val icon: String) {
    @SerializedName(value = "states", alternate = ["STATES"])
    STATES("states", "Indian States", "🏛️"),

    @SerializedName(value = "kings", alternate = ["KINGS"])
    KINGS("kings", "Indian Kings & Dynasties", "👑"),

    @SerializedName(value = "politics", alternate = ["POLITICS"])
    POLITICS("politics", "Indian Politics & Freedom", "⚖️"),

    @SerializedName(value = "ancient", alternate = ["ANCIENT"])
    ANCIENT("ancient", "Ancient History", "📜"),

    @SerializedName(value = "modern", alternate = ["MODERN"])
    MODERN("modern", "Modern History", "🚀");

    companion object {
        fun fromId(id: String?): HistoryCategory {
            if (id == null) return KINGS
            val clean = id.trim().lowercase()
            return entries.firstOrNull { it.id.lowercase() == clean || it.name.lowercase() == clean } ?: KINGS
        }
    }
}

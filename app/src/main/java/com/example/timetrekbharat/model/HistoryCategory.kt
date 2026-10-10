package com.example.timetrekbharat.model

enum class HistoryCategory(val id: String, val title: String, val icon: String) {
    STATES("states", "Indian States", "🏛️"),
    KINGS("kings", "Indian Kings & Dynasties", "👑"),
    POLITICS("politics", "Indian Politics & Freedom", "⚖️"),
    ANCIENT("ancient", "Ancient History", "📜"),
    MODERN("modern", "Modern History", "🚀")
}

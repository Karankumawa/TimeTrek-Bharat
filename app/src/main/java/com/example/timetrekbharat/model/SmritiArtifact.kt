package com.example.timetrekbharat.model

enum class DepthTier(val displayName: String, val audioDuration: String, val targetWords: String) {
    QUICK("Smriti Quick ⚡", "~15-20s", "30-50 words"),
    TALE("Smriti Tale 📜", "~45-60s", "100-150 words"),
    CHRONICLE("Smriti Chronicle 🏰", "~2.5-3.5m", "350-500 words")
}

enum class StoryPersona(val id: String, val displayName: String, val icon: String, val description: String) {
    ARCHITECT("architect", "Royal Architect", "📐", "Load-bearing arches, stone mortar, hydraulic flows & acoustics"),
    SENTRY("sentry", "Fortress Sentry", "⚔️", "Defensive parapets, night sieges, blindspots & ambush channels"),
    BARD("bard", "Court Bard (Charana)", "🪕", "Heroic ballads, court intrigue, oral legends & romantic folklore"),
    CHRONICLER("chronicler", "Invader's Chronicler", "📜", "Intimidating bastions, siege machinery & tactical maneuvers")
}

data class NarrativeContent(
    val depth: DepthTier,
    val persona: StoryPersona,
    val text: String,
    val audioScript: String = text
)

data class SmritiArtifact(
    val id: String,
    val name: String,
    val hindiName: String,
    val locationEra: String,
    val imageResOrUrl: String,
    val epigraphyOriginal: String? = null,
    val epigraphyDeciphered: String? = null,
    val acousticResonanceDesc: String? = null,
    val hydraulicMonsoonLevel: Int = 85, // percentage for stepwell cutaway
    val narratives: Map<Pair<DepthTier, StoryPersona>, String> = emptyMap()
)

data class BardicSeal(
    val id: String,
    val title: String,
    val tellerName: String,
    val location: String,
    val durationSec: Int,
    val audioQuote: String,
    val isCollected: Boolean = false
)

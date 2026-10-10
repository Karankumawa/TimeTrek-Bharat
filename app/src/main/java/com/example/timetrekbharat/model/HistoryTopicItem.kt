package com.example.timetrekbharat.model

import java.io.Serializable

data class HistoryTopicItem(
    val id: String,
    val category: HistoryCategory,
    val title: String,
    val subtitle: String,
    val description: String,
    val imageUrl: String,
    val keyFacts: List<String> = emptyList(),
    val prominentFigures: List<String> = emptyList(),
    val periodEra: String = "",
    val dynastyOrParty: String? = null,
    val location: String? = null,
    val stateSlug: String? = null
) : Serializable

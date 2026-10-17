package com.example.timetrekbharat.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HistoryTopicItem(
    @SerializedName("id") var id: String = "",
    @SerializedName("category") var category: HistoryCategory = HistoryCategory.KINGS,
    @SerializedName("title") var title: String = "",
    @SerializedName("subtitle") var subtitle: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("image_url") var imageUrl: String = "",
    @SerializedName("key_facts") var keyFacts: List<String> = emptyList(),
    @SerializedName("prominent_figures") var prominentFigures: List<String> = emptyList(),
    @SerializedName("period_era") var periodEra: String = "",
    @SerializedName("dynasty_or_party") var dynastyOrParty: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("state_slug") var stateSlug: String? = null
) : Serializable

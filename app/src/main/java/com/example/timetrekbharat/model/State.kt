package com.example.timetrekbharat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StateSymbols(
    @SerializedName("animal") var animal: String? = null,
    @SerializedName("bird") var bird: String? = null,
    @SerializedName("tree") var tree: String? = null,
    @SerializedName("flower") var flower: String? = null
) : Serializable

data class CulturalHeritage(
    @SerializedName("dance_music") var danceMusic: List<String>? = ArrayList(),
    @SerializedName("festivals") var festivals: List<String>? = ArrayList(),
    @SerializedName("cuisine") var cuisine: List<String>? = ArrayList(),
    @SerializedName("handicrafts") var handicrafts: List<String>? = ArrayList()
) : Serializable

data class FamousMonument(
    @SerializedName("name") var name: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("UNESCO") var isUnesco: Boolean = false
) : Serializable

@Entity(tableName = "states")
data class State(
    @PrimaryKey
    @SerializedName(value = "slug", alternate = ["_id", "id"])
    var slug: String = "",

    @SerializedName(value = "name", alternate = ["state_name", "title"])
    var name: String? = null,

    @SerializedName("region")
    var region: String? = null,

    @SerializedName("capital")
    var capital: String? = null,

    @SerializedName(value = "short_description", alternate = ["description"])
    var shortDescription: String? = null,

    @SerializedName("detailed_overview")
    var detailedOverview: String? = null,

    @SerializedName("language")
    var language: String? = null,

    @SerializedName("area_sq_km")
    var areaSqKm: String? = null,

    @SerializedName("best_time_to_visit")
    var bestTimeToVisit: String? = null,

    @SerializedName("state_symbols")
    var stateSymbols: StateSymbols? = null,

    @SerializedName("cultural_heritage")
    var culturalHeritage: CulturalHeritage? = null,

    @SerializedName("famous_monuments")
    var famousMonuments: List<FamousMonument>? = ArrayList(),

    @SerializedName("image_url")
    var imageUrl: String? = null,

    @SerializedName("banner_url")
    var bannerUrl: String? = null,

    @SerializedName("timeline")
    var timeline: List<TimelineEntry>? = ArrayList(),

    var isFavorite: Boolean = false
) : Serializable

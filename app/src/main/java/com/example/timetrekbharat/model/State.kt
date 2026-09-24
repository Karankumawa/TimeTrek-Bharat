package com.example.timetrekbharat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

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

    @SerializedName("image_url")
    var imageUrl: String? = null,

    @SerializedName("banner_url")
    var bannerUrl: String? = null,

    @SerializedName("timeline")
    var timeline: List<TimelineEntry>? = ArrayList()
) : Serializable

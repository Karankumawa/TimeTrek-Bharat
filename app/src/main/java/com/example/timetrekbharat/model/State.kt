package com.example.timetrekbharat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "states")
data class State(
    @PrimaryKey
    @SerializedName("slug")
    var slug: String = "",

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("region")
    var region: String? = null,

    @SerializedName("capital")
    var capital: String? = null,

    @SerializedName("short_description")
    var shortDescription: String? = null,

    @SerializedName("image_url")
    var imageUrl: String? = null,

    @SerializedName("banner_url")
    var bannerUrl: String? = null,

    @SerializedName("timeline")
    var timeline: List<TimelineEntry>? = ArrayList()
) : Serializable

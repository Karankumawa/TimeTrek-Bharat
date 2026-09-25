package com.example.timetrekbharat.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TimelineEntry(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("era") var era: String? = null,
    @SerializedName("period") var period: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("key_events") var keyEvents: List<String>? = ArrayList(),
    @SerializedName("key_rulers") var keyRulers: List<String>? = ArrayList(),
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("images") var images: List<String>? = ArrayList()
) : Serializable

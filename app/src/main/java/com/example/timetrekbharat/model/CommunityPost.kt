package com.example.timetrekbharat.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CommunityPost(
    @SerializedName("id") var id: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("state_slug") var stateSlug: String = "",
    @SerializedName("location_name") var locationName: String = "",
    @SerializedName("author_name") var authorName: String = "",
    @SerializedName("image_url") var imageUrl: String = "",
    @SerializedName("created_at") var createdAt: String? = null
) : Serializable

data class CommunityResponse(
    @SerializedName("success") var isSuccess: Boolean = false,
    @SerializedName("data") var data: List<CommunityPost>? = ArrayList()
)

data class SingleCommunityResponse(
    @SerializedName("success") var isSuccess: Boolean = false,
    @SerializedName("data") var data: CommunityPost? = null
)

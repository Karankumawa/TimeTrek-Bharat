package com.example.timetrekbharat.model

import com.google.gson.annotations.SerializedName

data class StateDetailResponse(
    @SerializedName("success") var isSuccess: Boolean = false,
    @SerializedName("data") var data: State? = null
)

package com.example.timetrekbharat.model

import com.google.gson.annotations.SerializedName

data class StateResponse(
    @SerializedName("success") var isSuccess: Boolean = false,
    @SerializedName("count") var count: Int = 0,
    @SerializedName("data") var data: List<State>? = ArrayList()
)

package com.example.timetrekbharat.network

import com.example.timetrekbharat.model.StateDetailResponse
import com.example.timetrekbharat.model.StateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HistoryApi {

    @GET("api/states")
    fun getAllStates(@Query("search") searchQuery: String?): Call<StateResponse>

    @GET("api/states/{id}")
    fun getStateDetail(@Path("id") stateIdentifier: String): Call<StateDetailResponse>
}

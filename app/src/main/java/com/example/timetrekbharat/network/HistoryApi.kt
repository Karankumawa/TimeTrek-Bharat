package com.example.timetrekbharat.network

import com.example.timetrekbharat.model.CommunityPost
import com.example.timetrekbharat.model.CommunityResponse
import com.example.timetrekbharat.model.SingleCommunityResponse
import com.example.timetrekbharat.model.StateDetailResponse
import com.example.timetrekbharat.model.StateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HistoryApi {

    @GET("api/states")
    fun getAllStates(@Query("search") searchQuery: String?): Call<StateResponse>

    @GET("api/states/{id}")
    fun getStateDetail(@Path("id") stateIdentifier: String): Call<StateDetailResponse>

    @GET("api/community")
    fun getCommunityPosts(@Query("state") stateSlug: String?): Call<CommunityResponse>

    @POST("api/community")
    fun addCommunityPost(@Body post: CommunityPost): Call<SingleCommunityResponse>

    @POST("api/community/{id}/like")
    fun likeCommunityPost(@Path("id") postId: String): Call<SingleCommunityResponse>
}

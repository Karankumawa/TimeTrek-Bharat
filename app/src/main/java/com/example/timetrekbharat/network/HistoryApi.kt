package com.example.timetrekbharat.network

import com.example.timetrekbharat.model.CommunityPost
import com.example.timetrekbharat.model.CommunityResponse
import com.example.timetrekbharat.model.SingleCommunityResponse
import com.example.timetrekbharat.model.StateDetailResponse
import com.example.timetrekbharat.model.StateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HistoryApi {

    @GET("api/states")
    suspend fun getAllStates(
        @Query("search") searchQuery: String? = null,
        @Query("region") regionQuery: String? = null
    ): Response<StateResponse>

    @GET("api/states/{id}")
    suspend fun getStateDetail(
        @Path("id") stateIdentifier: String
    ): Response<StateDetailResponse>

    @GET("api/community")
    suspend fun getCommunityPosts(
        @Query("state") stateSlug: String? = null
    ): Response<CommunityResponse>

    @POST("api/community")
    suspend fun addCommunityPost(
        @Body post: CommunityPost
    ): Response<SingleCommunityResponse>

    @POST("api/community/{id}/like")
    suspend fun likeCommunityPost(
        @Path("id") postId: String
    ): Response<SingleCommunityResponse>
}

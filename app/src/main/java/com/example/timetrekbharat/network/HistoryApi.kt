package com.example.timetrekbharat.network

import com.example.timetrekbharat.model.CommunityPost
import com.example.timetrekbharat.model.HistoryTopicItem
import com.example.timetrekbharat.model.State
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HistoryApi {

    @GET("states?select=*&order=name.asc")
    suspend fun getAllStates(): Response<List<State>>

    @GET("states?select=*")
    suspend fun getStateDetail(
        @Query("slug") slugEqFilter: String
    ): Response<List<State>>

    @GET("history_topics?select=*&order=id.asc")
    suspend fun getHistoryTopics(
        @Query("category") categoryFilter: String? = null
    ): Response<List<HistoryTopicItem>>

    @GET("community_posts?select=*&order=created_at.desc")
    suspend fun getCommunityPosts(
        @Query("state_slug") stateFilter: String? = null
    ): Response<List<CommunityPost>>

    @POST("community_posts")
    suspend fun addCommunityPost(
        @Body post: CommunityPost
    ): Response<List<CommunityPost>>

    @POST("rpc/increment_post_likes")
    suspend fun likeCommunityPost(
        @Body body: Map<String, String>
    ): Response<Void>
}

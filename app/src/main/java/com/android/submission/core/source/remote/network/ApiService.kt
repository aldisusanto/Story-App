package com.android.submission.core.source.remote.network

import com.android.submission.core.source.remote.response.DefaultResponse
import com.android.submission.core.source.remote.response.LoginResponse
import com.android.submission.core.source.remote.response.StoryResponse
import com.android.submission.core.source.remote.response.item.StoryItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") fullName: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<DefaultResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Response<DefaultResponse>

    @GET("stories")
    suspend fun getStoryWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Response<StoryResponse>
}
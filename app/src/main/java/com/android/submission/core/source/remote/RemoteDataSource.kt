package com.android.submission.core.source.remote

import com.android.submission.core.source.remote.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RemoteDataSource(private val apiService: ApiService) {
    suspend fun register(name: String, email: String, password: String) =
        apiService.register(name, email, password)

    suspend fun login(email: String, password: String) = apiService.login(email, password)

    suspend fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ) =
        apiService.addStory(token, description, file, lat, lon)

    suspend fun getAllStoryWithLocation(token: String, location: Int) =
        apiService.getStoryWithLocation(token, location)

}

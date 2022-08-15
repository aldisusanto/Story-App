package com.android.submission.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.submission.core.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ) =
        storyRepository.addStory(token, description, file, lat, lon).asLiveData()
}
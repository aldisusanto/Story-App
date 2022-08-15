package com.android.submission.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.submission.core.repository.StoryRepository

class MapViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getAllStoryWithLocation(token: String, location: Int) =
        repository.getAllStoryWithLocation(token, location).asLiveData()
}
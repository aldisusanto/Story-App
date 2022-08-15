package com.android.submission.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.submission.core.repository.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        storyRepository.register(name, email, password).asLiveData()
}
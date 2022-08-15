package com.android.submission.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.submission.core.repository.StoryRepository

class LoginViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun login(email: String, password: String) = storyRepository.login(email, password).asLiveData()

}
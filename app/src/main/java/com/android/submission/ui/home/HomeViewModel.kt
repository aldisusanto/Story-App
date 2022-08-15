package com.android.submission.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.submission.core.repository.StoryRepository
import com.android.submission.core.source.remote.response.item.StoryItem

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getAllStory(token: String): LiveData<PagingData<StoryItem>> =
        storyRepository.
        getAllStory(token).cachedIn(viewModelScope)

}
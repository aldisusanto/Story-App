package com.android.submission.core.source.remote.response

import com.android.submission.core.source.remote.response.item.StoryItem

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryItem>
)
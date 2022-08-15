package com.android.submission.core.source.remote.response.item

data class StoryItem(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val lat: Double,
    val lon: Double
)
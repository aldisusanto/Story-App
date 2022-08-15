package com.android.submission.core.source.remote.response

import com.android.submission.core.source.remote.response.item.ResultItem
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val error: Boolean,
    val message: String,
    @field:SerializedName("loginResult")
    val result: ResultItem
)
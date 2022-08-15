package com.android.submission.core.source.remote.response.item

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultItem(
    val name: String,
    val userId: String,
    val token: String,
    val isLogin: Boolean
): Parcelable
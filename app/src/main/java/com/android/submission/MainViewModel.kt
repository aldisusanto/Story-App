package com.android.submission

import androidx.lifecycle.ViewModel
import com.android.submission.core.source.remote.response.item.ResultItem
import com.android.submission.session.SessionRepository
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val pref: SessionRepository) : ViewModel() {
    fun getUser(): Flow<ResultItem> {
        return pref.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}
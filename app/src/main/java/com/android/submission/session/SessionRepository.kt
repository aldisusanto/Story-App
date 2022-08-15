package com.android.submission.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.android.submission.core.source.remote.response.item.ResultItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class SessionRepository(private val dataStore: DataStore<Preferences>) {
    fun getUser(): Flow<ResultItem> {
        return dataStore.data.map {
            ResultItem(
                it[NAME_KEY] ?: "",
                it[USERID_KEY] ?: "",
                it[TOKEN_KEY] ?: "",
                it[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveUser(user: ResultItem) {
        dataStore.edit {
            it[NAME_KEY] = user.name
            it[USERID_KEY] = user.userId
            it[TOKEN_KEY] = user.token
            it[STATE_KEY] = user.isLogin
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it[STATE_KEY] = false
            it[USERID_KEY] = ""
            it[TOKEN_KEY] = ""
            it[PASSWORD_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SessionRepository? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val USERID_KEY = stringPreferencesKey("userId")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): SessionRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionRepository(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
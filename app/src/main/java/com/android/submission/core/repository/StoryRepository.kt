package com.android.submission.core.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.android.submission.core.source.remote.RemoteDataSource
import com.android.submission.core.source.remote.StoryPagingSource
import com.android.submission.core.source.remote.network.ApiService
import com.android.submission.core.source.remote.network.Resource
import com.android.submission.core.source.remote.response.DefaultResponse
import com.android.submission.core.source.remote.response.LoginResponse
import com.android.submission.core.source.remote.response.StoryResponse
import com.android.submission.core.source.remote.response.item.StoryItem
import com.inyongtisto.myhelper.extension.getErrorBody
import com.inyongtisto.myhelper.extension.logs
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val remote: RemoteDataSource,
    private val apiService: ApiService
) {
    fun register(name: String, email: String, password: String) = flow {
        emit(Resource.loading(null))
        try {
            remote.register(name, email, password).let {
                if (it.isSuccessful) {
                    val body = it.body()
                    emit(Resource.success(body))
                    logs("success: " + body.toString())

                } else {
                    emit(
                        Resource.error(
                            it.getErrorBody(DefaultResponse::class.java)?.message
                                ?: "Default error", null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Terjadi kesalahan", null))
        }
    }

    fun login(email: String, password: String) = flow {
        emit(Resource.loading(null))
        try {
            remote.login(email, password).let {
                if (it.isSuccessful) {
                    val body = it.body()
                    emit(Resource.success(body))
                    logs("success: " + body.toString())
                } else {
                    emit(
                        Resource.error(
                            it.getErrorBody(LoginResponse::class.java)?.message
                                ?: "Default Error", null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Terjadi kesalahan", null))
        }
    }


    fun getAllStory(token: String): LiveData<PagingData<StoryItem>> {

        return Pager(
            config = PagingConfig(
                pageSize = 1,
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
        
    }

    fun addStory(
        token: String,
        description: RequestBody,
        file: MultipartBody.Part,
        lat: Double?,
        lon: Double?
    ) = flow {
        emit(Resource.loading(null))
        try {
            remote.addStory(token, file, description, lat, lon).let {
                if (it.isSuccessful) {
                    val body = it.body()
                    emit(Resource.success(body))
                    logs("success: " + body.toString())
                } else {
                    emit(
                        Resource.error(
                            it.getErrorBody(DefaultResponse::class.java)?.message
                                ?: "Default Error", null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Terjadi kesalahan", null))
        }
    }

    fun getAllStoryWithLocation(token: String, location: Int) = flow {
        emit(Resource.loading(null))
        try {
            remote.getAllStoryWithLocation(token, location).let {
                if (it.isSuccessful) {
                    val body = it.body()
                    emit(Resource.success(body))
                    logs("success: " + body.toString())
                } else {
                    emit(
                        Resource.error(
                            it.getErrorBody(StoryResponse::class.java)?.message
                                ?: "Default Error", null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "Terjadi kesalahan", null))
        }
    }


}
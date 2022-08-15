package com.android.submission.core.di

import com.android.submission.core.source.remote.RemoteDataSource
import com.android.submission.core.source.remote.network.NetworkClient
import org.koin.dsl.module


val storyModules = module {
    single { NetworkClient.getApiService() }
    single { RemoteDataSource(get()) }

}
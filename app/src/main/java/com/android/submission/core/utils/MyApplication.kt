package com.android.submission.core.utils

import android.app.Application
import com.android.submission.core.di.repositoryModules
import com.android.submission.core.di.storyModules
import com.android.submission.core.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    storyModules,
                    repositoryModules,
                    viewModelModules,
                )
            )
        }
    }
}
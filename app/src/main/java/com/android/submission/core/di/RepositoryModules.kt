package com.android.submission.core.di

import com.android.submission.core.repository.StoryRepository
import org.koin.dsl.module

val repositoryModules = module {
    single { StoryRepository(get(), get()) }
}
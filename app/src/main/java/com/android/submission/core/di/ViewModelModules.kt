package com.android.submission.core.di

import com.android.submission.ui.home.HomeViewModel
import com.android.submission.ui.login.LoginViewModel
import com.android.submission.ui.map.MapViewModel
import com.android.submission.ui.register.RegisterViewModel
import com.android.submission.ui.story.AddStoryViewModel
import org.koin.dsl.module

val viewModelModules = module {
    single { RegisterViewModel(get()) }
    single { LoginViewModel(get()) }
    single { HomeViewModel(get()) }
    single { AddStoryViewModel(get()) }
    single { MapViewModel(get()) }
}
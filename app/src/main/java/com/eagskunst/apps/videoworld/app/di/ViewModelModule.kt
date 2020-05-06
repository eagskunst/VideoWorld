package com.eagskunst.apps.videoworld.app.di

import com.eagskunst.apps.videoworld.app.repositories.CommentsLocalRepository
import com.eagskunst.apps.videoworld.app.repositories.CommentsRepository
import com.eagskunst.apps.videoworld.viewmodels.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by eagskunst in 5/5/2020.
 */

val viewModelModule = module {
    viewModel { CommentsViewModel(get()) }
    viewModel { DownloadViewModel(androidContext()) }
    viewModel { OrientationViewModel() }
    viewModel { PlayerViewModel() }
    viewModel { TwitchViewModel(get()) }
}
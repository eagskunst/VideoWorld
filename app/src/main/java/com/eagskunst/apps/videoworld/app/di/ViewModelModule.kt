package com.eagskunst.apps.videoworld.app.di

import com.eagskunst.apps.videoworld.viewmodels.CommentsViewModel
import com.eagskunst.apps.videoworld.viewmodels.DownloadViewModel
import com.eagskunst.apps.videoworld.viewmodels.OrientationViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by eagskunst in 5/5/2020.
 */

val viewModelModule = module {
    viewModel { CommentsViewModel(get()) }
    viewModel { DownloadViewModel(androidContext().filesDir.path) }
    viewModel { OrientationViewModel() }
    viewModel { PlayerViewModel() }
    viewModel { TwitchViewModel(get()) }
}

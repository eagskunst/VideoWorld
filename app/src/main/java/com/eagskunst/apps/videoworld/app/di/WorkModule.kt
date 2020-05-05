package com.eagskunst.apps.videoworld.app.di

import android.content.Context
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Created by eagskunst in 5/5/2020.
 */

val workModule = module {
    single { WorkManager.getInstance(androidApplication()) }
}
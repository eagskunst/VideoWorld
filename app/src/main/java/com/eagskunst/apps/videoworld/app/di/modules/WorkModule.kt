package com.eagskunst.apps.videoworld.app.di.modules

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.Reusable

/**
 * Created by eagskunst in 5/5/2020.
 */
@Module
class WorkModule {

    @Provides
    @Reusable
    fun provideWorkManager(context: Context): WorkManager = WorkManager.getInstance(context)
}
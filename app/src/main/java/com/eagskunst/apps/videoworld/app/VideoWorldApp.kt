package com.eagskunst.apps.videoworld.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.eagskunst.apps.videoworld.app.di.component.AppComponent
import com.eagskunst.apps.videoworld.app.di.component.ComponentProvider
import com.eagskunst.apps.videoworld.app.di.component.DaggerAppComponent
import timber.log.Timber

/**
 * Created by eagskunst in 26/4/2020.
 */
class VideoWorldApp: Application(), ComponentProvider {

    override val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        val config = Configuration.Builder()
            .setWorkerFactory(appComponent.workerFactory)
            .build()

        WorkManager.initialize(this, config)

        Timber.plant(Timber.DebugTree())
    }
}
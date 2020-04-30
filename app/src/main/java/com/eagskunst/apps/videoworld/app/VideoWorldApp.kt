package com.eagskunst.apps.videoworld.app

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.eagskunst.apps.videoworld.app.di.component.AppComponent
import com.eagskunst.apps.videoworld.app.di.component.DaggerAppComponent
import com.eagskunst.apps.videoworld.app.di.modules.AppModule
import timber.log.Timber

/**
 * Created by eagskunst in 26/4/2020.
 */
class VideoWorldApp: Application(){

    companion object {

        lateinit var instance: VideoWorldApp
            private set

        val context: Context by lazy {
            instance.applicationContext
        }
    }

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.factory()
            .create(AppModule(this))

        val config = Configuration.Builder()
            .setWorkerFactory(appComponent.workerFactory())
            .build()

        WorkManager.initialize(this, config)

        Timber.plant(Timber.DebugTree())
    }
}
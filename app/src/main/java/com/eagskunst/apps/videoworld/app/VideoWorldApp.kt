package com.eagskunst.apps.videoworld.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import androidx.work.Configuration
import androidx.work.WorkManager
import com.eagskunst.apps.videoworld.app.di.*
import timber.log.Timber

/**
 * Created by eagskunst in 26/4/2020.
 */
class VideoWorldApp: Application(){

    companion object {
        lateinit var instance: VideoWorldApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@VideoWorldApp)
            modules(getModules())
        }
        Timber.plant(Timber.DebugTree())
    }

    private fun getModules() = networkModule + retrofitModule + apiModule +
    exoplayerModule + databaseModule + workModule + repositoriesModule +
            viewModelModule

}
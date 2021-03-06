package com.eagskunst.apps.videoworld.app

import android.app.Application
import com.eagskunst.apps.videoworld.app.di.apiModule
import com.eagskunst.apps.videoworld.app.di.databaseModule
import com.eagskunst.apps.videoworld.app.di.exoplayerModule
import com.eagskunst.apps.videoworld.app.di.networkModule
import com.eagskunst.apps.videoworld.app.di.repositoriesModule
import com.eagskunst.apps.videoworld.app.di.retrofitModule
import com.eagskunst.apps.videoworld.app.di.viewModelModule
import com.eagskunst.apps.videoworld.app.di.workModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

/**
 * Created by eagskunst in 26/4/2020.
 */
class VideoWorldApp : Application() {

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

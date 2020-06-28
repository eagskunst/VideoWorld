package com.eagskunst.apps.videoworld

import android.app.Application
import androidx.annotation.VisibleForTesting
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Level
import org.koin.core.module.Module
import timber.log.Timber

/**
 * Created by eagskunst in 18/6/2020.
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
class VideoWorldTestApp : Application() {

    companion object {
        lateinit var instance: VideoWorldTestApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.DEBUG) else EmptyLogger()
            androidContext(this@VideoWorldTestApp)
            modules(emptyList())
        }
        Timber.plant(Timber.DebugTree())
    }

    internal fun injectModules(modules: List<Module>) {
        loadKoinModules(modules)
    }
}

package com.eagskunst.apps.videoworld.app

import android.app.Application
import android.content.Context
import com.eagskunst.apps.videoworld.app.di.component.AppComponent
import com.eagskunst.apps.videoworld.app.di.component.DaggerAppComponent
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

        appComponent = DaggerAppComponent.factory().create(instance)

        Timber.plant(Timber.DebugTree())
    }
}
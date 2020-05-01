package com.eagskunst.apps.videoworld.app.di.component

import android.content.Context
import androidx.work.WorkerFactory
import com.eagskunst.apps.videoworld.app.VideoWorldApp
import com.eagskunst.apps.videoworld.app.di.modules.*
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import com.google.android.exoplayer2.upstream.DataSource
import dagger.BindsInstance
import dagger.Component

/**
 * Created by eagskunst in 26/4/2020.
 */
@AppScope
@Component(modules = [ApiModule::class, WorkerAssistedModule::class, WorkersModule::class,
    ExoPlayerModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    val workerFactory: WorkerFactory
    val dataSourceFactory: DataSource.Factory
}
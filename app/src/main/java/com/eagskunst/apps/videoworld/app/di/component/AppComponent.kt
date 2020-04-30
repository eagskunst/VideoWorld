package com.eagskunst.apps.videoworld.app.di.component

import androidx.work.WorkerFactory
import com.eagskunst.apps.videoworld.app.VideoWorldApp
import com.eagskunst.apps.videoworld.app.di.modules.ApiModule
import com.eagskunst.apps.videoworld.app.di.modules.AppModule
import com.eagskunst.apps.videoworld.app.di.modules.WorkerAssistedModule
import com.eagskunst.apps.videoworld.app.di.modules.WorkersModule
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import dagger.BindsInstance
import dagger.Component

/**
 * Created by eagskunst in 26/4/2020.
 */
@AppScope
@Component(modules = [AppModule::class, ApiModule::class,
    WorkerAssistedModule::class, WorkersModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(appModule: AppModule): AppComponent
    }

    fun workerFactory(): WorkerFactory

}
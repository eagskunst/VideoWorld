package com.eagskunst.apps.videoworld.app.di.modules

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.eagskunst.apps.videoworld.app.di.factories.ChildWorkerFactory
import com.eagskunst.apps.videoworld.app.di.factories.WorkersFactory
import com.eagskunst.apps.videoworld.app.workers.VideoDownloadWorker
import dagger.*
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

/**
 * Created by eagskunst in 18/4/2020.
 */

@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
abstract class WorkersModule {

    @Binds
    abstract fun bindWorkerFactory(workersFactory: WorkersFactory): WorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(VideoDownloadWorker::class)
    abstract fun bindRatesWorker(worker: VideoDownloadWorker.Factory): ChildWorkerFactory

}
package com.eagskunst.apps.videoworld.app.di.modules

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

/**
 * Created by eagskunst in 19/4/2020.
 */
@Module(includes = [AssistedInject_WorkerAssistedModule::class])
@AssistedModule
interface WorkerAssistedModule
package com.eagskunst.apps.videoworld.app.di.modules

import android.content.res.Resources
import com.eagskunst.apps.videoworld.app.VideoWorldApp
import com.eagskunst.apps.videoworld.app.di.component.AppComponent
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import dagger.Module
import dagger.Provides

/**
 * Created by eagskunst in 26/4/2020.
 */
@Module
class AppModule(private val app: VideoWorldApp){

    @Provides
    @AppScope
    fun provideApp(): VideoWorldApp = app

    @Provides
    @AppScope
    fun provideResources(): Resources = app.resources

    @Provides
    @AppScope
    fun provideAppComponent(appComponent: AppComponent): AppComponent = appComponent
}
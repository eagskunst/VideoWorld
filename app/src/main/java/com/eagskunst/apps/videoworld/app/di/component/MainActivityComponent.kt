package com.eagskunst.apps.videoworld.app.di.component

import com.eagskunst.apps.videoworld.MainActivity
import com.eagskunst.apps.videoworld.app.di.modules.ExoPlayerModule
import com.eagskunst.apps.videoworld.app.di.scopes.MainActivityScope
import dagger.Subcomponent

/**
 * Created by eagskunst in 30/4/2020.
 */

@MainActivityScope
@Subcomponent(modules = [ExoPlayerModule::class])
interface MainActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(exoPlayerModule: ExoPlayerModule): MainActivityComponent
    }

    fun inject(mainActivity: MainActivity)
}
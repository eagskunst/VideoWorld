package com.eagskunst.apps.videoworld.app.di

import com.eagskunst.apps.videoworld.app.network.api.ClipsApi
import com.eagskunst.apps.videoworld.app.network.api.UserApi
import com.eagskunst.apps.videoworld.app.repositories.CommentsLocalRepository
import com.eagskunst.apps.videoworld.app.repositories.CommentsRepository
import com.eagskunst.apps.videoworld.app.repositories.TwitchRemoteRepository
import com.eagskunst.apps.videoworld.app.repositories.TwitchRepository
import org.koin.dsl.module

/**
 * Created by eagskunst in 5/5/2020.
 */

val repositoriesModule = module {
    single { CommentsLocalRepository(get()) }
    single { CommentsRepository(get()) }
    single { TwitchRemoteRepository(get<UserApi>(), get<ClipsApi>()) }
    single { TwitchRepository(get()) }
}
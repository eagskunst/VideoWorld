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
 * We don't need to share instances of repositories. Better use Factory.
 */

val repositoriesModule = module {
    factory { CommentsLocalRepository(get()) }
    factory { CommentsRepository(get()) }
    factory { TwitchRemoteRepository(get<UserApi>(), get<ClipsApi>()) }
    factory { TwitchRepository(get()) }
}
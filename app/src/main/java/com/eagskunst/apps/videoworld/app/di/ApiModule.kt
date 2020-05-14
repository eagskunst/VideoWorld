package com.eagskunst.apps.videoworld.app.di

import com.eagskunst.apps.videoworld.app.network.api.ClipsApi
import com.eagskunst.apps.videoworld.app.network.api.TwitchAuthApi
import com.eagskunst.apps.videoworld.app.network.api.TwitchDownloadApi
import com.eagskunst.apps.videoworld.app.network.api.UserApi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Created by eagskunst in 30/4/2020.
 */

val apiModule = module {
    single {
        val retrofit: Retrofit = get(named(KoinQualifiers.TwitchApi))
        retrofit.create(UserApi::class.java)
    }

    single {
        val retrofit: Retrofit = get(named(KoinQualifiers.TwitchApi))
        retrofit.create(ClipsApi::class.java)
    }

    single {
        val retrofit: Retrofit = get(named(KoinQualifiers.ClipsApi))
        retrofit.create(TwitchDownloadApi::class.java)
    }

    single {
        val retrofit: Retrofit = get(named(KoinQualifiers.TwitchAuth))
        retrofit.create(TwitchAuthApi::class.java)
    }
}
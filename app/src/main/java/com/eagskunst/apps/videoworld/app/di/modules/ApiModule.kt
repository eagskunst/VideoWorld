package com.eagskunst.apps.videoworld.app.di.modules

import com.eagskunst.apps.videoworld.app.di.qualifiers.TwitchDownloaderQualifier
import com.eagskunst.apps.videoworld.app.di.qualifiers.TwitchQualifier
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import com.eagskunst.apps.videoworld.app.network.api.ClipsApi
import com.eagskunst.apps.videoworld.app.network.api.TwitchDownloadApi
import com.eagskunst.apps.videoworld.app.network.api.UserApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Created by eagskunst in 26/4/2020.
 */
@Module(includes = [RetrofitModule::class])
class ApiModule {

    @Provides
    @AppScope
    fun provideTwitchUserApi(@TwitchQualifier retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @AppScope
    fun provideTwitchClipsApi(@TwitchQualifier retrofit: Retrofit): ClipsApi =
        retrofit.create(ClipsApi::class.java)

    @Provides
    @AppScope
    fun provideTwitchDownloaderApi(@TwitchDownloaderQualifier retrofit: Retrofit): TwitchDownloadApi =
        retrofit.create(TwitchDownloadApi::class.java)
}
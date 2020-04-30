package com.eagskunst.apps.videoworld.app.di.modules

import android.content.res.Resources
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.di.modules.NetworkModule
import com.eagskunst.apps.videoworld.app.di.qualifiers.TwitchDownloaderQualifier
import com.eagskunst.apps.videoworld.app.di.qualifiers.TwitchQualifier
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by eagskunst in 26/4/2020.
 */
@Module(includes = [NetworkModule::class])
class RetrofitModule {


    @Provides
    @AppScope
    fun provideMoshi() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @AppScope
    @TwitchQualifier
    fun provideTwitchApiUrl(resources: Resources): String = resources.getString(R.string.twitch_api_url)

    @Provides
    @AppScope
    @TwitchDownloaderQualifier
    fun provideTwitchClipsUrl(resources: Resources): String = resources.getString(R.string.twitch_clips_url)




    @Provides
    @AppScope
    @TwitchQualifier
    fun provideRetrofitTwitch(
        okHttpClient: OkHttpClient,
        @TwitchQualifier url: String,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(url)
        .build()

    @Provides
    @AppScope
    @TwitchDownloaderQualifier
    fun provideRetrofitTwitchDownloader(
        okHttpClient: OkHttpClient,
        @TwitchDownloaderQualifier url: String,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(url)
        .build()

}
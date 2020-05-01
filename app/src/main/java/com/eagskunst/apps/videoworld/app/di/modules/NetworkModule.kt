package com.eagskunst.apps.videoworld.app.di.modules

import android.content.Context
import android.os.Build
import com.eagskunst.apps.videoworld.BuildConfig
import com.eagskunst.apps.videoworld.app.VideoWorldApp
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by eagskunst in 26/4/2020.
 */

@Module
class NetworkModule {

    @Provides
    @AppScope
    fun provideInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @AppScope
    fun provideCacheFile(context: Context): File = File(context.cacheDir, "OkHttp_Cache")

    @Provides
    @AppScope
    fun provideCache(cacheFile: File): Cache = Cache(cacheFile, 10*1000*1000)


    @Provides
    @AppScope
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor,
                            cache: Cache
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader(
                    "User-Agent",
                    "VideoWorld-ANDROID " + " BUILD VERSION: " + BuildConfig.VERSION_NAME + " SMARTPHONE: " + Build.MODEL + " ANDROID VERSION: " + Build.VERSION.RELEASE
                )
                .addHeader("Content-Type", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .readTimeout(90, TimeUnit.SECONDS)
        .connectTimeout(90, TimeUnit.SECONDS)
        .cache(cache)
        .build()
}
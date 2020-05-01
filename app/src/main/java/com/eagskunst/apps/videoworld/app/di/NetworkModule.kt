package com.eagskunst.apps.videoworld.app.di

import android.os.Build
import com.eagskunst.apps.videoworld.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by eagskunst in 30/4/2020.
 */

val networkModule = module {

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single { File(androidContext().cacheDir, "okhttp_cache") }

    single { Cache(get(), 10*1000*1000) }

    single {
        OkHttpClient.Builder()
            .addInterceptor( get<HttpLoggingInterceptor>() )
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
            .cache(get())
            .build()
    }
}


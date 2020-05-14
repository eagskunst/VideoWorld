package com.eagskunst.apps.videoworld.app.di

import com.eagskunst.apps.videoworld.R
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by eagskunst in 30/4/2020.
 */

val retrofitModule = module {
    single {
        Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    }

    factory(named(KoinQualifiers.TwitchApi)) {
        val res = androidContext().resources
        res.getString(R.string.twitch_api_url)
    }


    factory(named(KoinQualifiers.TwitchAuth)) {
        val res = androidContext().resources
        res.getString(R.string.twitch_auth_url)
    }


    factory(named(KoinQualifiers.ClipsApi)) {
        val res = androidContext().resources
        res.getString(R.string.twitch_clips_url)
    }

    single(named(KoinQualifiers.TwitchApi)) {
        Retrofit.Builder()
            .client(get<OkHttpClient>(
                named(KoinQualifiers.TwitchApi)
            ))
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .baseUrl(
                get<String>(
                    named(KoinQualifiers.TwitchApi)
                )
            )
            .build()
    }

    single(named(KoinQualifiers.ClipsApi)) {
        Retrofit.Builder()
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .baseUrl(
                get<String>(
                    named(KoinQualifiers.ClipsApi)
                )
            )
            .build()
    }

    single(named(KoinQualifiers.TwitchAuth)) {
        Retrofit.Builder()
            .client(get<OkHttpClient>(
                named(KoinQualifiers.TwitchAuth)
            ))
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .baseUrl(
                get<String>(
                    named(KoinQualifiers.TwitchAuth)
                )
            )
            .build()
    }
}
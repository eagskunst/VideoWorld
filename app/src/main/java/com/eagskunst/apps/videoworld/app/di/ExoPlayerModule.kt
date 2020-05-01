package com.eagskunst.apps.videoworld.app.di

import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheEvictor
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

/**
 * Created by eagskunst in 30/4/2020.
 */

val exoplayerModule = module {
    factory { (100 * 1024 * 1024).toLong() }

    single { LeastRecentlyUsedCacheEvictor(get()) }

    single(named(KoinQualifiers.ExoPlayer)){
        File(androidContext().cacheDir, "media-cache")
    }

    single { ExoDatabaseProvider(androidContext()) }

    single {
        SimpleCache(
            get(named(KoinQualifiers.ExoPlayer)),
            get<CacheEvictor>(),
            get<ExoDatabaseProvider>()
        )
    }

    single { DefaultDataSourceFactory(androidContext(), "Video-World") }

    single<DataSource.Factory> {
        CacheDataSourceFactory(get(), get())
    }
}
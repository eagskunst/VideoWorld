package com.eagskunst.apps.videoworld.app.di

import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

/**
 * Created by eagskunst in 30/4/2020.
 */

val exoplayerModule = module {

    single { (100 * 1024 * 1024).toLong() }

    single<CacheEvictor> { LeastRecentlyUsedCacheEvictor(get()) }

    single(named(KoinQualifiers.ExoPlayer)){
        File(androidContext().cacheDir, "media-cache")
    }

    single { ExoDatabaseProvider(androidContext()) }

    single {
        SimpleCache(
            get<File>(named(KoinQualifiers.ExoPlayer)),
            get<CacheEvictor>(),
            get<ExoDatabaseProvider>()
        )
    }

    single { DefaultDataSourceFactory(androidContext(), "Video-World") }

    single <DataSource.Factory> {
        CacheDataSourceFactory(get<SimpleCache>(), get<DefaultDataSourceFactory>())
    }
}
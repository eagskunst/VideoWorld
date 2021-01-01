package com.eagskunst.apps.videoworld.app.di

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheEvictor
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Created by eagskunst in 30/4/2020.
 */

val exoplayerModule = module {

    factory { (100 * 1024 * 1024).toLong() }

    single<CacheEvictor> { LeastRecentlyUsedCacheEvictor(get()) }

    single(named(KoinQualifiers.ExoPlayer)) {
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

    factory { (context: Context) -> SimpleExoPlayer.Builder(context).build() }
}

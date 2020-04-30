package com.eagskunst.apps.videoworld.app.di.modules

import android.content.Context
import com.eagskunst.apps.videoworld.app.di.qualifiers.ExoPlayerQualifier
import com.eagskunst.apps.videoworld.app.di.scopes.MainActivityScope
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheEvictor
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import java.io.File

/**
 * Created by eagskunst in 30/4/2020.
 */

@Module
class ExoPlayerModule(private val context: Context) {

    @Provides
    fun cacheSize(): Long = (100 * 1024 * 1024).toLong()

    @Provides
    @MainActivityScope
    fun provideEvictor(cacheSize: Long): CacheEvictor = LeastRecentlyUsedCacheEvictor(cacheSize)

    @Provides
    @MainActivityScope
    @ExoPlayerQualifier
    fun provideCacheFile(): File = File(context.cacheDir, "media-cache")

    @Provides
    @MainActivityScope
    fun provideExoDbProvider(): ExoDatabaseProvider = ExoDatabaseProvider(context)

    @Provides
    @MainActivityScope
    fun provideSimpleCache(
        @ExoPlayerQualifier file: File,
        evictor: CacheEvictor,
        db: ExoDatabaseProvider
    ): SimpleCache =
        SimpleCache(file, evictor, db)

    @Provides
    @MainActivityScope
    fun provideDefaultDataSourceFactory(): DefaultDataSourceFactory =
        DefaultDataSourceFactory(context, "Video-World")


    @Provides
    @MainActivityScope
    fun provideDataSourceFactory(
        cache: SimpleCache,
        defaultDsf: DefaultDataSourceFactory
    ): DataSource.Factory =
        CacheDataSourceFactory(cache, defaultDsf)
}
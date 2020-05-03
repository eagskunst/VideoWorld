package com.eagskunst.apps.videoworld.app.di.modules

import android.content.Context
import androidx.room.Room
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import com.eagskunst.apps.videoworld.db.VideoWorldDatabase
import com.eagskunst.apps.videoworld.db.daos.CommentsDao
import dagger.Module
import dagger.Provides

/**
 * Created by eagskunst in 3/5/2020.
 */
@Module
class DatabaseModule {

    @Provides
    @AppScope
    fun provideDatabase(context: Context): VideoWorldDatabase = Room.databaseBuilder(
        context,
        VideoWorldDatabase::class.java,
        "VideoWorldDatabase")
        .build()

    @Provides
    @AppScope
    fun provideCommentsDao(db: VideoWorldDatabase): CommentsDao = db.commentsDao()

}
package com.eagskunst.apps.videoworld.app.di

import androidx.room.Room
import com.eagskunst.apps.videoworld.db.VideoWorldDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


/**
 * Created by eagskunst in 5/5/2020.
 */

val databaseModule = module {
    single {
        Room.databaseBuilder(
        androidApplication(),
        VideoWorldDatabase::class.java,
        "VideoWorldDatabase")
        .build()
    }
    single {
       get<VideoWorldDatabase>().commentsDao()
    }
}
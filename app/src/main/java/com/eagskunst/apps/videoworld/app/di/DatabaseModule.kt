package com.eagskunst.apps.videoworld.app.di

import org.koin.dsl.module


/**
 * Created by eagskunst in 5/5/2020.
 */

val databaseModule = module {
    single {
        //TODO: Create RoomDb:
        /*
        VideoWorldDatabase = Room.databaseBuilder(
        context,
        VideoWorldDatabase::class.java,
        "VideoWorldDatabase")
        .build()
         */
    }
    single {
        //TODO: comments dao
        /**
         * db.commentsDao()
         */
    }
}
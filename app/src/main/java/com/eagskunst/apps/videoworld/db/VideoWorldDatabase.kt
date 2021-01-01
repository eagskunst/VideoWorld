package com.eagskunst.apps.videoworld.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eagskunst.apps.videoworld.db.daos.CommentsDao
import com.eagskunst.apps.videoworld.db.entities.Comment

/**
 * Created by eagskunst in 3/5/2020.
 */
@Database(entities = [Comment::class], version = 1)
abstract class VideoWorldDatabase : RoomDatabase() {
    abstract fun commentsDao(): CommentsDao
}

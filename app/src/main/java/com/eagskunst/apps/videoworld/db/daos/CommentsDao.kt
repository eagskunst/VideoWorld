package com.eagskunst.apps.videoworld.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.eagskunst.apps.videoworld.db.entities.Comment

/**
 * Created by eagskunst in 3/5/2020.
 */
@Dao
interface CommentsDao {

    @Insert
    suspend fun insertComment(comment: Comment)

    @Delete
    suspend fun deleteCommnet(comment: Comment)

    @Query("SELECT * FROM Comment WHERE videoId = :videoId")
    fun getCommentsOfVideoLiveData(videoId: String): LiveData<List<Comment>>
}
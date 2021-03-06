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
    suspend fun deleteComment(comment: Comment)

    @Query("SELECT * FROM Comment")
    fun getCommentsOfVideoLiveData(): LiveData<List<Comment>>
}

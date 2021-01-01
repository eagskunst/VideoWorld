package com.eagskunst.apps.videoworld.app.repositories

import com.eagskunst.apps.videoworld.db.daos.CommentsDao
import com.eagskunst.apps.videoworld.db.entities.Comment

/**
 * Created by eagskunst in 3/5/2020.
 */
class CommentsLocalRepository(private val commentsDao: CommentsDao) {

    suspend fun insertComment(comment: Comment) = commentsDao.insertComment(comment)

    suspend fun deleteComment(comment: Comment) = commentsDao.deleteComment(comment)

    fun commentLiveData() = commentsDao.getCommentsOfVideoLiveData()
}

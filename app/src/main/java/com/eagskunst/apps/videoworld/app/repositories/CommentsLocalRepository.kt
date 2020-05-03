package com.eagskunst.apps.videoworld.app.repositories

import com.eagskunst.apps.videoworld.db.daos.CommentsDao
import com.eagskunst.apps.videoworld.db.entities.Comment
import javax.inject.Inject

/**
 * Created by eagskunst in 3/5/2020.
 */
class CommentsLocalRepository @Inject constructor(private val commentsDao: CommentsDao) {

    suspend fun insertComment(comment: Comment) = commentsDao.insertComment(comment)

    suspend fun deleteComment(comment: Comment) = commentsDao.deleteCommnet(comment)

    fun commentLiveData(videoId: String) = commentsDao.getCommentsOfVideoLiveData(videoId)

}
package com.eagskunst.apps.videoworld.app.repositories

import com.eagskunst.apps.videoworld.db.entities.Comment
import javax.inject.Inject

/**
 * Created by eagskunst in 3/5/2020.
 */
class CommentsRepository @Inject constructor(private val localRepository: CommentsLocalRepository) {

    suspend fun insertComment(comment: Comment) = localRepository.insertComment(comment)

    suspend fun deleteComment(comment: Comment) = localRepository.deleteComment(comment)

    fun commentsLiveData() = localRepository.commentLiveData()

}
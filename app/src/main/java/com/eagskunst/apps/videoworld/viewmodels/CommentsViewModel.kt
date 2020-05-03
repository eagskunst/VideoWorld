package com.eagskunst.apps.videoworld.viewmodels

import androidx.lifecycle.viewModelScope
import com.eagskunst.apps.videoworld.app.repositories.CommentsRepository
import com.eagskunst.apps.videoworld.db.entities.Comment
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by eagskunst in 3/5/2020.
 */
class CommentsViewModel @Inject constructor(private val commentsRepository: CommentsRepository): BaseViewModel() {

    val commentsLiveData = commentsRepository.commentsLiveData()

    fun insertNewComment(content: String, videoId: String) {
        viewModelScope.launch {
            commentsRepository.insertComment(Comment(
                videoId = videoId,
                content = content
            ))
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            commentsRepository.deleteComment(comment)
        }
    }

}
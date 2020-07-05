package com.eagskunst.apps.videoworld.builders

import androidx.annotation.VisibleForTesting
import com.eagskunst.apps.videoworld.db.entities.Comment

/**
 * Created by eagskunst in 7/6/2020.
 */
class CommentBuilder {

    var id: Int? = null
    var videoId = String()
    var content = String()

    fun build() =
        if(id == null) {
            Comment(
                videoId = videoId,
                content = content
            )
        }
        else {
            Comment(
                id = id!!,
                videoId = videoId,
                content = content
            )
        }
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun comment(block: CommentBuilder.() -> Unit) = CommentBuilder()
    .apply(block).build()

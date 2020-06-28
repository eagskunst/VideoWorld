package com.eagskunst.apps.videoworld.builders

import androidx.annotation.VisibleForTesting
import com.eagskunst.apps.videoworld.db.entities.Comment

/**
 * Created by eagskunst in 7/6/2020.
 */
class CommentBuilder {

    var videoId = String()
    var content = String()

    fun build() = Comment(
        videoId = videoId,
        content = content
    )
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun comment(block: CommentBuilder.() -> Unit) = CommentBuilder()
    .apply(block).build()

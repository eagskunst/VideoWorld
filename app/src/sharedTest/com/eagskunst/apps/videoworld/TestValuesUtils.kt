package com.eagskunst.apps.videoworld

import androidx.annotation.VisibleForTesting
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.builders.clipResponse
import com.eagskunst.apps.videoworld.builders.comment
import com.eagskunst.apps.videoworld.db.entities.Comment
import java.util.UUID

/**
 * Created by eagskunst in 7/6/2020.
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
object TestValuesUtils {

    fun getRandomString(length: Int = 30): String {
        val allowedChars = ('A'..'Z') + ('a'..'z')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun createComments(range: IntRange = 0..4): List<Comment> {
        val comments = mutableListOf<Comment>()

        for (n in range) {
            val comment =
                createComment()
            comments.add(comment)
        }

        return comments
    }

    fun createClipsResponses(size: Int = 5, thumbUrl: String? = null): List<ClipResponse> {
        val clips = mutableListOf<ClipResponse>()
        for (n in 0..size) {
            val clip =
                createClipResponse(
                    thumbUrl
                )
            clips.add(clip)
        }

        return clips
    }

    fun createComment(vId: String? = null, cont: String? = null) =
        comment {
            videoId = vId ?: UUID.randomUUID().toString()
            content = cont ?: getRandomString()
        }

    fun createClipResponse(thumbUrl: String? = null, id: String? = null) =
        clipResponse {
            thumbnailurl = thumbUrl
                ?: "https://clips-media-assets2.twitch.tv/AT-cm%7C386828697-preview-480x272.jpg"
            this.id = id ?: UUID.randomUUID().toString()
        }
}

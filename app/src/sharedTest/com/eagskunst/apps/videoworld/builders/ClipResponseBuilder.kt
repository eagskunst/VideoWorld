package com.eagskunst.apps.videoworld.builders

import androidx.annotation.VisibleForTesting
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse

/**
 * Created by eagskunst in 9/6/2020.
 */
class ClipResponseBuilder {

    var broadcasterId = ""
    var broadcasterName = ""
    var createdAt = ""
    var creatorId = ""
    var creatorName = ""
    var embedUrl = ""
    var gameId = ""
    var id = ""
    var language = ""
    var thumbnailurl = ""
    var title = ""
    var url = ""
    var videoId = ""
    var viewCount = 0

    fun build() = ClipResponse(
        broadcasterId = broadcasterId,
        broadcasterName = broadcasterName,
        createdAt = createdAt,
        creatorId = creatorId,
        creatorName = creatorName,
        embedUrl = embedUrl,
        gameId = gameId,
        id = id,
        language = language,
        thumbnailUrl = thumbnailurl,
        title = title,
        url = url,
        videoId = videoId,
        viewCount = viewCount
    )
}
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun clipResponse(block: ClipResponseBuilder.() -> Unit) = ClipResponseBuilder()
    .apply(block).build()
package com.eagskunst.apps.videoworld.app.network.responses.clips


import com.eagskunst.apps.videoworld.utils.Constants
import com.eagskunst.apps.videoworld.utils.formatInt
import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.*

data class ClipResponse(
    @Json(name = "broadcaster_id")
    val broadcasterId: String,
    @Json(name = "broadcaster_name")
    val broadcasterName: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "creator_id")
    val creatorId: String,
    @Json(name = "creator_name")
    val creatorName: String,
    @Json(name = "embed_url")
    val embedUrl: String,
    @Json(name = "game_id")
    val gameId: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "language")
    val language: String,
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val url: String,
    @Json(name = "video_id")
    val videoId: String,
    @Json(name = "view_count")
    val viewCount: Int
) {
    val viewCountFormatted = "Views: ${viewCount.formatInt()}"
    fun dateFormatted(): String {
        val date = Constants.TWITCH_DATE_SDF.parse(createdAt)
        return Constants.GLOBAL_SDF.format(date ?: Date())
    }

    fun getClipUrl(): String = Regex(".*(?=-preview)").run {
        "${find(thumbnailUrl)?.value}.mp4"
    }
}
package com.eagskunst.apps.videoworld.app.network.responses.user

import com.eagskunst.apps.videoworld.app.network.api.UserApi
import com.squareup.moshi.Json
/**
 * Associated with [UserApi]
 * */
data class UserResponse(
    @Json(name = "broadcaster_type")
    val broadcasterType: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "login")
    val login: String,
    @Json(name = "offline_image_url")
    val offlineImageUrl: String,
    @Json(name = "profile_image_url")
    val profileImageUrl: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "view_count")
    val viewCount: Int
)

package com.eagskunst.apps.videoworld.app.network.responses.clips

import com.squareup.moshi.Json

data class UserClipsResponse(
    @Json(name = "data")
    val clipResponseList: List<ClipResponse>, // Change with `data` if doesn't work.
    @Json(name = "pagination")
    val pagination: Pagination
)

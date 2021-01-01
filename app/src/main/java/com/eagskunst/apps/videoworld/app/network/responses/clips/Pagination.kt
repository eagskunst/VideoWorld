package com.eagskunst.apps.videoworld.app.network.responses.clips

import com.squareup.moshi.Json

data class Pagination(
    @Json(name = "cursor")
    val cursor: String
)

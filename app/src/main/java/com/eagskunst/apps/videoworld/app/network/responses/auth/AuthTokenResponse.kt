package com.eagskunst.apps.videoworld.app.network.responses.auth

import com.squareup.moshi.Json

data class AuthTokenResponse(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "expires_in")
    val expiresIn: Int,
    @Json(name = "token_type")
    val tokenType: String
)

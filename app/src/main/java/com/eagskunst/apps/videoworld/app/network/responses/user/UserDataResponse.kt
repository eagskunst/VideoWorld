package com.eagskunst.apps.videoworld.app.network.responses.user

import com.eagskunst.apps.videoworld.app.network.api.UserApi
import com.squareup.moshi.Json

/**
 * Associated with [UserApi]
 * */
data class UserDataResponse(
    @Json(name = "data")
    val dataList: List<UserResponse> // If fails, change dataList to `data`
)

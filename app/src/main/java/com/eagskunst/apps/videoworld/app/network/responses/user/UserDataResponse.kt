package com.eagskunst.apps.videoworld.app.network.responses.user


import com.squareup.moshi.Json
import com.eagskunst.apps.videoworld.app.network.api.UserApi

/**
 * Associated with [UserApi]
 * */
data class UserDataResponse(
    @Json(name = "data")
    val dataList: List<UserResponse> //If fails, change dataList to `data`
)